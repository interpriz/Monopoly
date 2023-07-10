package com.example.monopoly.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.monopoly.MainActivity;
import com.example.monopoly.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EndOfGame#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EndOfGame extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "idPlayer";
    private static final String ARG_PARAM2 = "win";

    // TODO: Rename and change types of parameters
    private int idPlayer;
    private boolean win ;

    public EndOfGame() {
        // Required empty public constructor
        super(R.layout.fragment_end_of_game);
    }

    private void setInfo(int idPlayer, boolean win) {

        // do something in fragment
    }



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EndOfGame.
     */
    // TODO: Rename and change types and number of parameters
    public static PlayerFrag newInstance(int param1, boolean param2) {
        PlayerFrag fragment = new PlayerFrag();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putBoolean(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idPlayer = getArguments().getInt(ARG_PARAM1);
            win = getArguments().getBoolean(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_end_of_game, container, false);

        //setInfo(idPlayer, win);

        ImageView img = (ImageView) view.findViewById(R.id.playerImage);
        TextView  nickName = (TextView) view.findViewById(R.id.playerName);
        TextView  info = (TextView) view.findViewById(R.id.text);

        String playerName = ((MainActivity) getActivity()).gameService.getGame().players.get(idPlayer).name;

        nickName.setText(playerName);
        if (win) {
            info.setText("You WIN!!!");
        } else {
            info.setText("You lost the game!");
        }

        switch (idPlayer) {
            case 0:
                img.setImageResource(R.drawable.player_1);
                break;
            case 1:
                img.setImageResource(R.drawable.player_2);
                break;
            case 2:
                img.setImageResource(R.drawable.player_3);
                break;
            case 3:
                img.setImageResource(R.drawable.player_4);
                break;
            default:
                img.setImageResource(0);
                break;
        }

        return view;
    }
}