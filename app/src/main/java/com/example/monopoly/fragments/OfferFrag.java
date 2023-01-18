package com.example.monopoly.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.example.monopoly.MainActivity;
import com.example.monopoly.R;

import java.util.ArrayList;
import java.util.stream.Collectors;

import entities.Property;
import enums.OfferTypes;
import services.MapService;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OfferFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class  OfferFrag extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button makeOfferBtn;
    private Spinner senderList;
    private Spinner recipientList;
    private EditText sumEditText;

    private RadioButton radioButtonBuy;
    private RadioButton radioButtonSold;
    private RadioButton radioButtonChange;
    private RadioGroup radioGroup;


    ArrayList<Property> listSenderProperty;
    ArrayList<Property> listRecipientProperty;
    ArrayList<String> listSenderPropertiesNames;
    ArrayList<String> listRecipientPropertiesNames;

    OfferTypes offerType = OfferTypes.sold;

    public OfferFrag() {
        super(R.layout.fragment_offer);
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OfferFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static OfferFrag newInstance(String param1, String param2) {
        OfferFrag fragment = new OfferFrag();
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
        View view =  inflater.inflate(R.layout.fragment_offer, container, false);

        makeOfferBtn   = (Button) view.findViewById(R.id.makeOfferBtn);
        senderList   = (Spinner) view.findViewById(R.id.senderList);
        recipientList = (Spinner) view.findViewById(R.id.recipientList);
        sumEditText = (EditText) view.findViewById(R.id.sum);

        radioButtonBuy    = (RadioButton) view.findViewById(R.id.radioButtonBuy);
        radioButtonSold   = (RadioButton) view.findViewById(R.id.radioButtonSold);
        radioButtonChange = (RadioButton) view.findViewById(R.id.radioButtonChange);
        radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);


        int idRecipient = getArguments().getInt("idPlayer");
        int idSender = ((MainActivity) getActivity()).game.players.indexOf(((MainActivity) getActivity()).yourPlayer);

        listSenderProperty = ((MainActivity) getActivity()).gameService.getPlayersProperty(idSender);
        listRecipientProperty = ((MainActivity) getActivity()).gameService.getPlayersProperty(idRecipient);


        listSenderPropertiesNames = (ArrayList<String>) listSenderProperty.stream().map(x-> MapService.getInstance().getPropertyName(x)).collect(Collectors.toList());
        listRecipientPropertiesNames = (ArrayList<String>) listRecipientProperty.stream().map(x-> MapService.getInstance().getPropertyName(x)).collect(Collectors.toList());


        // Создаем адаптер ArrayAdapter с помощью массива строк и стандартной разметки элемета spinner
        ArrayAdapter<String> senderAdapter = new ArrayAdapter(this.getContext(), android.R.layout.simple_spinner_item, listSenderPropertiesNames);
        //ArrayAdapter<CharSequence>adapter=ArrayAdapter.createFromResource(this, R.array.oilTypes, android.R.layout.simple_spinner_item);
        // Определяем разметку для использования при выборе элемента
        senderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Применяем адаптер к элементу spinner
        senderList.setAdapter(senderAdapter);

        // Создаем адаптер ArrayAdapter с помощью массива строк и стандартной разметки элемета spinner
        ArrayAdapter<String> recipientAdapter = new ArrayAdapter(this.getContext(), android.R.layout.simple_spinner_item, listRecipientPropertiesNames);
        //ArrayAdapter<CharSequence>adapter=ArrayAdapter.createFromResource(this, R.array.oilTypes, android.R.layout.simple_spinner_item);
        // Определяем разметку для использования при выборе элемента
        recipientAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Применяем адаптер к элементу spinner
        recipientList.setAdapter(recipientAdapter);

        makeOfferBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                long senderPropertyId = senderList.getChildCount()>0 ? senderList.getSelectedItemId() : -1;
                long recipientPropertyId = recipientList.getChildCount()>0 ? recipientList.getSelectedItemId() : -1;

                Property senderProperty = senderPropertyId==-1 ? null:listSenderProperty.get((int) senderPropertyId);
                Property recipientProperty = recipientPropertyId ==-1 ? null: listRecipientProperty.get((int) recipientPropertyId);

                String sumTxt = sumEditText.getText().toString();
                int sum = sumTxt.equals("")?0:Integer.parseInt(sumTxt);


                String result = ((MainActivity) getActivity()).gameService.makeOffer(
                        ((MainActivity) getActivity()).game.players.get(idRecipient),
                        senderProperty,
                        offerType,
                        sum,
                        recipientProperty,
                        ((MainActivity) getActivity()).game.players.get(idSender)
                );
                ((MainActivity) getActivity()).showMessage(result);
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup arg0, int id) {
                switch(id) {
                    case R.id.radioButtonBuy:
                        offerType = OfferTypes.buy;
                        break;
                    case R.id.radioButtonSold:
                        offerType = OfferTypes.sold;
                        break;
                    case R.id.radioButtonChange:
                        offerType = OfferTypes.change;
                        break;
                    default:
                        break;
                }
            }});

        return view;
    }

}