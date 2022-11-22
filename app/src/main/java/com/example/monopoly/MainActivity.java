package com.example.monopoly;

import static android.content.ContentValues.TAG;
import static entities.StaticStrings.SUCCESS;
import static enums.GameStates.onPause;
import static enums.GameStates.onPlay;
import static enums.GameStates.onStart;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import entities.Auction;
import entities.FieldDB;
import entities.Game;
import entities.Player;
import entities.Property;
import entities.Street;
import enums.GameStates;
import services.GameService;
import services.MapService;

// token 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
public class MainActivity extends AppCompatActivity {

    String text = "Привет";
    private TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.text);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://monopoly-b9e36-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference("testGame");

        /*Game game = new Game(4);
        GameService gameService = new GameService(game);
        gameService.enterGame();*/
        //MapService service = MapService.getInstance();
        //Property prop = service.getPropertyByPosition(1);

        myRef.setValue("Привет");

        /*Player newPlayer = gameService.enterGame();
        game.fieldsOwners.get(0).owner=0;
        myRef.child("fieldsOwners").child("0").child("owner").setValue(0);
        myRef.child("players").child(Integer.toString(game.players.size()-1)).setValue(newPlayer);

        newPlayer = gameService.enterGame();
        myRef.child("players").child(Integer.toString(game.players.size()-1)).setValue(newPlayer);*/

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                textView.setText(value);
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }


}