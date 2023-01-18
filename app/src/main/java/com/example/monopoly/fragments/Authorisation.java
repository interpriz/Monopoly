package com.example.monopoly.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.monopoly.MainActivity;
import com.example.monopoly.R;

import java.util.Optional;

import entities.Player;
import entities.Property;
import entities.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Authorisation#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Authorisation extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button startButton;

    public Authorisation() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Authorisation.
     */
    // TODO: Rename and change types and number of parameters
    public static Authorisation newInstance(String param1, String param2) {
        Authorisation fragment = new Authorisation();
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
        View view =   inflater.inflate(R.layout.fragment_authorisation, container, false);

        startButton   = (Button) view.findViewById(R.id.startGame);
        EditText editText1 = (EditText) view.findViewById(R.id.nickName);
        EditText editText2 = (EditText) view.findViewById(R.id.password);


        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nickName = editText1.getText().toString();
                String password = editText2.getText().toString();

                Optional<User> user = ((MainActivity) getActivity()).users.stream().filter(x-> x.name.equals(nickName)).findFirst();

                if(user.isPresent()){
                    boolean checkPassword = ((MainActivity) getActivity()).users.contains(user.get());
                    if(!checkPassword){
                        ((MainActivity) getActivity()).showMessage("Неправильное имя или пароль!");
                        return;
                    }
                }else{
                    User newUser = new User(nickName, password);
                    ((MainActivity) getActivity()).usersRef.child(Integer.toString( ((MainActivity) getActivity()).users.size())).setValue(newUser);
                    ((MainActivity) getActivity()).users.add(newUser);
                }
                ((MainActivity) getActivity()).start(nickName);

            }
        });

        return view;
    }
}