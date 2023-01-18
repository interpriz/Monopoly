package com.example.monopoly.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.monopoly.MainActivity;
import com.example.monopoly.R;

import java.util.ArrayList;
import java.util.stream.Collectors;

import entities.Offer;
import enums.OfferStates;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewOffersFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewOffersFrag extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ListView offersList;

    public ViewOffersFrag() {
        super(R.layout.fragment_view_offers);
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ViewOffersFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewOffersFrag newInstance(String param1, String param2) {
        ViewOffersFrag fragment = new ViewOffersFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_view_offers, container, false);

        offersList = (ListView) view.findViewById(R.id.offersList);

        ArrayList<Offer> playerOffers = (ArrayList<Offer>) ((MainActivity) getActivity())
                .yourPlayer.offers.stream()
                .filter(x->x.state.equals(OfferStates.newOffer))
                .collect(Collectors.toList());
        ArrayList<String> playerOffersStrings = ((MainActivity) getActivity()).gameService.getPlayersOffersStrings(((MainActivity) getActivity()).yourPlayer);


        // создаем адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter(this.getContext(),
                android.R.layout.simple_list_item_1, playerOffersStrings);

        // устанавливаем для списка адаптер
        offersList.setAdapter(adapter);

        // добавляем для списка слушатель
        offersList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                Offer offer = playerOffers.get(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Важное сообщение!")
                        .setMessage("Что хотите сделать с предлождением?")
                        .setCancelable(false)
                        .setPositiveButton("Принять",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        String result = ((MainActivity) getActivity()).gameService.acceptOffer(offer, ((MainActivity) getActivity()).yourPlayer);
                                        dialog.cancel();
                                        ((MainActivity) getActivity()).showMessage(result);
                                        ((MainActivity) getActivity()).offerClick(((MainActivity) getActivity()).yourPlayer.name);

                                    }
                                })
                        .setNegativeButton("Отклонить",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        String result = ((MainActivity) getActivity()).gameService.rejectOffer(offer, ((MainActivity) getActivity()).yourPlayer);
                                        dialog.cancel();
                                        ((MainActivity) getActivity()).showMessage(result);
                                        ((MainActivity) getActivity()).offerClick(((MainActivity) getActivity()).yourPlayer.name);
                                    }
                                })
                        .setNeutralButton("Ничего",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });

        return view;
    }
}