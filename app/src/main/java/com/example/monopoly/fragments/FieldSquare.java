package com.example.monopoly.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.monopoly.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FieldSquare#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FieldSquare extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FieldSquare() {
        super(R.layout.fragment_field_square);
        // Required empty public constructor
    }

    public void setVisiblePLayer(int idPlayer) {
        ImageView img = null;
        switch(idPlayer){
            case 1:
            default:
                img =  (ImageView) getView().findViewById(R.id.player1);
                break;
            case 2:
                img =  (ImageView) getView().findViewById(R.id.player2);
                break;
            case 3:
                img =  (ImageView) getView().findViewById(R.id.player3);
                break;
            case 4:
                img =  (ImageView) getView().findViewById(R.id.player4);
                break;
        }
        img.setVisibility(View.VISIBLE);
        // do something in fragment
    }

    public void setInvisiblePLayer(int idPlayer) {
        ImageView img = null;
        switch(idPlayer){
            case 1:
            default:
                img =  (ImageView) getView().findViewById(R.id.player1);
                break;
            case 2:
                img =  (ImageView) getView().findViewById(R.id.player2);
                break;
            case 3:
                img =  (ImageView) getView().findViewById(R.id.player3);
                break;
            case 4:
                img =  (ImageView) getView().findViewById(R.id.player4);
                break;
        }
        img.setVisibility(View.INVISIBLE);
        // do something in fragment
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FieldSquare.
     */
    // TODO: Rename and change types and number of parameters
    public static FieldSquare newInstance(String param1, String param2) {
        FieldSquare fragment = new FieldSquare();
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
        return inflater.inflate(R.layout.fragment_field_square, container, false);
    }
}