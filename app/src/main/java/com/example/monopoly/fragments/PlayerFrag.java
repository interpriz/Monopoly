package com.example.monopoly.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.monopoly.MainActivity;
import com.example.monopoly.R;

import entities.Player;
import entities.Property;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlayerFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayerFrag extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ImageButton offerBtn;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PlayerFrag() {
        // Required empty public constructor
        super(R.layout.fragment_player);
    }

    public void setImage(int idResource) {
        ImageView img = (ImageView) getView().findViewById(R.id.playerImg);
        img.setImageResource(idResource);
        // do something in fragment
    }

    public void setFrame(int idPlayer) {
        LinearLayout frag = (LinearLayout) getView();
        switch (idPlayer) {
            case 0:
                frag.setBackgroundResource(R.drawable.frame_1);
                break;
            case 1:
                frag.setBackgroundResource(R.drawable.frame_2);
                break;
            case 2:
                frag.setBackgroundResource(R.drawable.frame_3);
                break;
            case 3:
                frag.setBackgroundResource(R.drawable.frame_4);
                break;
            default:
                frag.setBackgroundResource(0);
                break;
        }
        // do something in fragment
    }



    public void setCash(int cash){
        TextView cashText = (TextView) getView().findViewById(R.id.playerCash);
        cashText.setText(cash+"$");
    }

    public void setPlayerName(String name){
        TextView playerNameText = (TextView) getView().findViewById(R.id.playerName);
        playerNameText.setText(name);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Player.
     */
    // TODO: Rename and change types and number of parameters
    public static PlayerFrag newInstance(String param1, String param2) {
        PlayerFrag fragment = new PlayerFrag();
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
        View view =  inflater.inflate(R.layout.fragment_player, container, false);

        offerBtn   = (ImageButton) view.findViewById(R.id.offerBtn);

        offerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView playerNameText = (TextView) getView().findViewById(R.id.playerName);
                String playerName = playerNameText.getText().toString();
                ((MainActivity) getActivity()).offerClick(playerName);
            }
        });
        return view;
    }
}