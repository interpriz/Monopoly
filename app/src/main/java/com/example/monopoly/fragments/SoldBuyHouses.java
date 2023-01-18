package com.example.monopoly.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.monopoly.MainActivity;
import com.example.monopoly.R;

import java.util.ArrayList;
import java.util.stream.Collectors;

import entities.Street;
import enums.PropTypes;
import services.MapService;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SoldBuyHouses#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SoldBuyHouses extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button buyBtn;
    private Button soldBtn;
    private Spinner dropDownList;

    ArrayList<Integer> listPropertyIds;
    ArrayList<Street> listStreets;
    ArrayList<String> listStreetsNames;

    public SoldBuyHouses() {
        super(R.layout.fragment_buy_sold_houses);
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment buySoldHouses.
     */
    // TODO: Rename and change types and number of parameters
    public static SoldBuyHouses newInstance(String param1, String param2) {
        SoldBuyHouses fragment = new SoldBuyHouses();
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
        View view = inflater.inflate(R.layout.fragment_buy_sold_houses, container, false);

        buyBtn = (Button) view.findViewById(R.id.buyBtn);
        soldBtn = (Button) view.findViewById(R.id.soldBtn);
        dropDownList = (Spinner) view.findViewById(R.id.dropDownList);

        int idPlayer = ((MainActivity) getActivity()).game.players.indexOf(((MainActivity) getActivity()).yourPlayer);

        listPropertyIds = (ArrayList<Integer>) ((MainActivity) getActivity())
                .game.fieldsOwners
                .stream().filter(x->x.owner==idPlayer)
                .map(y->((MainActivity) getActivity()).game.fieldsOwners.indexOf(y)).collect(Collectors.toList());

        listStreets = (ArrayList<Street>) listPropertyIds.stream()
                .map(x-> MapService.getInstance()
                        .getPropertyByPosition(x))
                .filter(x->x.type == PropTypes.street)
                .map(x-> (Street)x)
                .collect(Collectors.toList());

        listStreetsNames = (ArrayList<String>) listStreets.stream()
                .map(x-> x.name)
                .collect(Collectors.toList());


        // Создаем адаптер ArrayAdapter с помощью массива строк и стандартной разметки элемета spinner
        ArrayAdapter<String> adapter = new ArrayAdapter(this.getContext(), android.R.layout.simple_spinner_item, listStreetsNames);
        //ArrayAdapter<CharSequence>adapter=ArrayAdapter.createFromResource(this, R.array.oilTypes, android.R.layout.simple_spinner_item);
        // Определяем разметку для использования при выборе элемента
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Применяем адаптер к элементу spinner
        dropDownList.setAdapter(adapter);


        buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dropDownList.getCount()!=0){
                    String streetName = dropDownList.getSelectedItem().toString();
                    int idStreetInList = listStreetsNames.indexOf(streetName);
                    Street street = listStreets.get(idStreetInList);
                    String result = ((MainActivity) getActivity()).gameService.buyHouse(street, ((MainActivity) getActivity()).yourPlayer);
                    ((MainActivity) getActivity()).showMessage(result);
                }else
                    ((MainActivity) getActivity()).showMessage("У вас нет улиц!");

            }
        });

        soldBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dropDownList.getCount()!=0){
                    String streetName = dropDownList.getSelectedItem().toString();
                    int idStreetInList = listStreetsNames.indexOf(streetName);
                    Street street = listStreets.get(idStreetInList);
                    String result = ((MainActivity) getActivity()).gameService.soldHouse(street, ((MainActivity) getActivity()).yourPlayer);
                    ((MainActivity) getActivity()).showMessage(result);
                }else
                    ((MainActivity) getActivity()).showMessage("У вас нет улиц!");

            }
        });

        return view;
    }
}