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

import entities.Property;
import services.MapService;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SoldBuyProperty#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SoldBuyProperty extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button depositBtn;
    private Button buyBackBtn;
    private Spinner dropDownList;

    ArrayList<Integer> listPropertyIds;
    ArrayList<Property> listProperty;
    ArrayList<String> listPropertiesNames;

    public SoldBuyProperty() {
        // Required empty public constructor
        super(R.layout.fragment_sold_buy_property);
    }



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment soldBuyProperty.
     */
    // TODO: Rename and change types and number of parameters
    public static SoldBuyProperty newInstance(String param1, String param2) {
        SoldBuyProperty fragment = new SoldBuyProperty();
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

        View view = inflater.inflate(R.layout.fragment_sold_buy_property, container, false);

        depositBtn   = (Button) view.findViewById(R.id.depositBtn);
        buyBackBtn   = (Button) view.findViewById(R.id.buyBackBtn);
        dropDownList = (Spinner) view.findViewById(R.id.dropDownList);

        int idPlayer = ((MainActivity) getActivity()).game.players.indexOf(((MainActivity) getActivity()).currentPlayer);


        listProperty = ((MainActivity) getActivity()).gameService.getPlayersProperty(idPlayer);

        listPropertiesNames = (ArrayList<String>) listProperty.stream().map(x-> MapService.getInstance().getPropertyName(x)).collect(Collectors.toList());



        // Создаем адаптер ArrayAdapter с помощью массива строк и стандартной разметки элемета spinner
        ArrayAdapter<String> adapter = new ArrayAdapter(this.getContext(), android.R.layout.simple_spinner_item, listPropertiesNames);
        //ArrayAdapter<CharSequence>adapter=ArrayAdapter.createFromResource(this, R.array.oilTypes, android.R.layout.simple_spinner_item);
        // Определяем разметку для использования при выборе элемента
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Применяем адаптер к элементу spinner
        dropDownList.setAdapter(adapter);


        depositBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String propertyName = dropDownList.getSelectedItem().toString();
                int idPropertyInList = listPropertiesNames.indexOf(propertyName);
                Property property = listProperty.get(idPropertyInList);
                String result = ((MainActivity) getActivity()).gameService.createDeposit(property, ((MainActivity) getActivity()).currentPlayer);
                ((MainActivity) getActivity()).showMessage(result);
            }
        });

        buyBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String propertyName = dropDownList.getSelectedItem().toString();
                int idPropertyInList = listPropertiesNames.indexOf(propertyName);
                Property property = listProperty.get(idPropertyInList);
                String result = ((MainActivity) getActivity()).gameService.destroyDeposit(property, ((MainActivity) getActivity()).currentPlayer);
                ((MainActivity) getActivity()).showMessage(result);
            }
        });

        return view;
    }

}