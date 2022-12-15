package com.example.monopoly;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.monopoly.fragments.FieldRecBottom;
import com.example.monopoly.fragments.FieldRecLeft;
import com.example.monopoly.fragments.FieldRecRight;
import com.example.monopoly.fragments.FieldRecTop;
import com.example.monopoly.fragments.FieldSquare;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import entities.Auction;
import entities.FieldDB;
import entities.Game;
import entities.Player;
import enums.GameStates;
import repositories.GameRepository;
import repositories.PlayerRepository;
import services.GameService;
import services.MapService;

// token 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
public class MainActivity extends AppCompatActivity {

    String text;
    private TextView textView;

    //объекты логики игры 
    private Game game;
    private Player yourPlayer;
    private GameService gameService;
    private GameRepository gr;
    private PlayerRepository pr;
    private MapService mapService = MapService.getInstance();

    //объекты взаимодействия с БД
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://monopoly-b9e36-default-rtdb.europe-west1.firebasedatabase.app/");
    DatabaseReference testMessageRef = database.getReference("testMessage");
    DatabaseReference gameRef = database.getReference("testGame");
    DatabaseReference gameDice1Ref = gameRef.child("dice1");
    DatabaseReference gameDice2Ref = gameRef.child("dice2");
    DatabaseReference gameAuctionRef = gameRef.child("auction");
    DatabaseReference gameStateRef = gameRef.child("state");
    DatabaseReference gameCurPlayerRef = gameRef.child("currentPlayerId");
    DatabaseReference gameBankRef = gameRef.child("bank");
    DatabaseReference gamePausedPlayerRef = gameRef.child("pausedPlayer");
    DatabaseReference gameWinnerRef = gameRef.child("winnerId");
    DatabaseReference gameFieldsOwnersRef = gameRef.child("fieldsOwners");
    DatabaseReference gamePlayersRef = gameRef.child("players");
    //DatabaseReference gamePlayer0Ref = gameRef.child("players").child("0");

    //слушатели БД
    ValueEventListener testMessageListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            String value = dataSnapshot.getValue(String.class);
            //textView.setText(value);
            Log.d(TAG, "Value is: " + value);
        }

        @Override
        public void onCancelled(DatabaseError error) {
            // Failed to read value
            Log.w(TAG, "Failed to read value.", error.toException());
        }
    };

    /*ValueEventListener gameFirstListen = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            game = dataSnapshot.getValue(Game.class);
            int a =0;
            if(game==null){
                game = new Game(4,"God");
                gameService = new GameService(game);
                gr = new GameRepository(game);
                pr = new PlayerRepository(game);
                gameRef.setValue(game);
                gameService.enterGame("God");
                gameService.enterGame("Sasha");
                gameService.enterGame("Sveta");
                gameService.enterGame("Lola");
                gr.setNewOwner(2, 0);
            }else {
                gameService = new GameService(game);
                gr = new GameRepository(game);
                pr = new PlayerRepository(game);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.e(TAG,"Error while reading data");
        }
    };*/
    //первичное считавание игры (если ее нет, то создается новая)
    OnCompleteListener gameFirstListen = new OnCompleteListener<DataSnapshot>() {
        @Override
        public void onComplete(@NonNull Task<DataSnapshot> task) {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                Log.d("firebase", String.valueOf(task.getResult().getValue()));
                DataSnapshot ds = task.getResult();
                game = ds.getValue(Game.class);
                int a = 0;
                if (game == null) {
                    game = new Game(4, "God");
                    gameRef.setValue(game);
                    gameService = new GameService(game);
                    yourPlayer = gameService.enterGame("God");
                    gameService.enterGame("Sasha");
                    gameService.enterGame("Sveta");
                    gameService.enterGame("Lola");
                }

                gameService = new GameService(game);
                gr = new GameRepository(game);
                pr = new PlayerRepository(game);

                bindGameParameters();

                /*
                gr.setDice1(1);
                gr.setDice2(2);
                gr.mixPLayers();
                gr.setAuction(new Auction(mapService.getPropertyByPosition(1)));
                gr.setAuctionNewParticipant(3);
                gr.setAuctionNewParticipant(1);
                gr.setAuctionNewParticipant(0);
                gr.setAuctionNewParticipant(2);
                gr.setAuctionRemovePlayer(0);
                gr.addHouse(mapService.getStreets().get(0));
                gr.addHouse(mapService.getStreets().get(0));
                gr.reduceHouses(mapService.getStreets().get(0));
                pr.addDebt(game.players.get(0),new Debt(0, 1, 200));
                pr.setPosition(game.players.get(0),10);
                pr.setCanRollDice(game.players.get(0),true);
                pr.addOffer(game.players.get(0),
                        new Offer(
                                1,
                                0,
                                1,
                                200,
                                OfferTypes.change,
                                OfferStates.newOffer,
                                3
                        ));

                pr.removeOffer(game.players.get(0),
                        game.players.get(0).getLastOffer());
                */
            }
        }
    };


    ValueEventListener gameDice1Listener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            game.dice1 = dataSnapshot.getValue(Integer.class);
            Log.d(TAG, "Value is: " + game.dice1);
        }

        @Override
        public void onCancelled(DatabaseError error) {
            // Failed to read value
            Log.w(TAG, "Failed to read value.", error.toException());
        }
    };
    ValueEventListener gameDice2Listener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            game.dice2 = dataSnapshot.getValue(Integer.class);
            Log.d(TAG, "Value is: " + game.dice2);
        }

        @Override
        public void onCancelled(DatabaseError error) {
            // Failed to read value
            Log.w(TAG, "Failed to read value.", error.toException());
        }
    };
    ValueEventListener gameAuctionListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            game.auction = dataSnapshot.getValue(Auction.class);
            Log.d(TAG, "Value is: " + game.auction);
        }

        @Override
        public void onCancelled(DatabaseError error) {
            // Failed to read value
            Log.w(TAG, "Failed to read value.", error.toException());
        }
    };
    ValueEventListener gameStateListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            game.state = dataSnapshot.getValue(GameStates.class);
            Log.d(TAG, "Value is: " + game.state);
        }

        @Override
        public void onCancelled(DatabaseError error) {
            // Failed to read value
            Log.w(TAG, "Failed to read value.", error.toException());
        }
    };
    ValueEventListener gameCurPlayerListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            game.currentPlayerId = dataSnapshot.getValue(Integer.class);
            Log.d(TAG, "Value is: " + game.currentPlayerId);
        }

        @Override
        public void onCancelled(DatabaseError error) {
            // Failed to read value
            Log.w(TAG, "Failed to read value.", error.toException());
        }
    };
    ValueEventListener gameBankListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            game.bank = dataSnapshot.getValue(Player.class);
            Log.d(TAG, "Value is: " + game.bank);
        }

        @Override
        public void onCancelled(DatabaseError error) {
            // Failed to read value
            Log.w(TAG, "Failed to read value.", error.toException());
        }
    };
    ValueEventListener gamePausedPlayerListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            game.pausedPlayer = dataSnapshot.getValue(Integer.class);
            Log.d(TAG, "Value is: " + game.pausedPlayer);
        }

        @Override
        public void onCancelled(DatabaseError error) {
            // Failed to read value
            Log.w(TAG, "Failed to read value.", error.toException());
        }
    };
    ValueEventListener gameWinnerListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            game.winnerId = dataSnapshot.getValue(Integer.class);
            Log.d(TAG, "Value is: " + game.winnerId);
        }

        @Override
        public void onCancelled(DatabaseError error) {
            // Failed to read value
            Log.w(TAG, "Failed to read value.", error.toException());
        }
    };

    ChildEventListener gameFieldsOwnersListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
            //Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

            // A new comment has been added, add it to the displayed list
            //FieldDB field = dataSnapshot.getValue(FieldDB.class);
            //game.fieldsOwners.add(field);

            // ...
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
            Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

            // A comment has changed, use the key to determine if we are displaying this
            // comment and if so displayed the changed comment.
            FieldDB updatedField = dataSnapshot.getValue(FieldDB.class);
            int fieldId = Integer.parseInt(dataSnapshot.getKey());
            game.fieldsOwners.set(fieldId, updatedField);
            setHousesOnField(fieldId,updatedField.houses);
            setPlayerFrame(fieldId, updatedField.owner);

            // ...
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            //Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

            // A comment has changed, use the key to determine if we are displaying this
            // comment and if so remove it.
            //String commentKey = dataSnapshot.getKey();

            // ...
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            //Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

            // A comment has changed position, use the key to determine if we are
            // displaying this comment and if so move it.
            //FieldDB movedField = dataSnapshot.getValue(FieldDB.class);
            //String fieldId = dataSnapshot.getKey();
            // ...
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.w(TAG, "postComments:onCancelled", databaseError.toException());
           /* Toast.makeText(mContext, "Failed to load comments.",
                    Toast.LENGTH_SHORT).show();*/
        }
    };

    ChildEventListener gamePlayersListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
            //Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

            // A new comment has been added, add it to the displayed list
            //FieldDB field = dataSnapshot.getValue(FieldDB.class);
            //game.fieldsOwners.add(field);

            // ...
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
            Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

            // A comment has changed, use the key to determine if we are displaying this
            // comment and if so displayed the changed comment.
            Player updatedPlayer = dataSnapshot.getValue(Player.class);
            int PlayerId = Integer.parseInt(dataSnapshot.getKey());

            int oldPosition = game.players.get(PlayerId).position;
            int newPosition = updatedPlayer.position;
            if(oldPosition != newPosition){
                movePlayerFigure(oldPosition,newPosition,PlayerId);
            }

            game.players.set(PlayerId, updatedPlayer);

            // ...
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            //Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

            // A comment has changed, use the key to determine if we are displaying this
            // comment and if so remove it.
            //String commentKey = dataSnapshot.getKey();

            // ...
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            //Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

            // A comment has changed position, use the key to determine if we are
            // displaying this comment and if so move it.
            //FieldDB movedField = dataSnapshot.getValue(FieldDB.class);
            //String fieldId = dataSnapshot.getKey();
            // ...
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.w(TAG, "postComments:onCancelled", databaseError.toException());
           /* Toast.makeText(mContext, "Failed to load comments.",
                    Toast.LENGTH_SHORT).show();*/
        }
    };

    //объекты интерфейса
    public List<Integer> fields = new ArrayList<Integer>(
            Arrays.asList(
                    R.id.field0, R.id.field1, R.id.field2, R.id.field3, R.id.field4, R.id.field5, R.id.field6, R.id.field7, R.id.field8, R.id.field9,
                    R.id.field10,R.id.field11,R.id.field12,R.id.field13,R.id.field14,R.id.field15,R.id.field16,R.id.field17,R.id.field18,R.id.field19,
                    R.id.field20,R.id.field21,R.id.field22,R.id.field23,R.id.field24,R.id.field25,R.id.field26,R.id.field27,R.id.field28,R.id.field29,
                    R.id.field30,R.id.field31,R.id.field32,R.id.field33,R.id.field34,R.id.field35,R.id.field36,R.id.field37,R.id.field38,R.id.field39
            )
    );

    //функции интерфейса
    private Fragment getFieldById(int id) {
        return getSupportFragmentManager().findFragmentById(fields.get(id));
    }

    private void setHousesOnField(int fieldId, int houses) {
        switch (fieldId) {
            case 1: case 2: case 3: case 4: case 5: case 6: case 7: case 8: case 9:
                FieldRecBottom fragmentB = (FieldRecBottom) getFieldById(fieldId);
                fragmentB.setVisibleHouses(houses);
                break;

            case 11: case 12: case 13: case 14: case 15: case 16: case 17: case 18: case 19:
                FieldRecLeft fragmentL = (FieldRecLeft) getFieldById(fieldId);
                fragmentL.setVisibleHouses(houses);
                break;

            case 21: case 22: case 23: case 24: case 25: case 26: case 27: case 28: case 29:
                FieldRecTop fragmentT = (FieldRecTop) getFieldById(fieldId);
                fragmentT.setVisibleHouses(houses);
                break;

            case 31: case 32: case 33: case 34: case 35: case 36: case 37: case 38: case 39:
                FieldRecRight fragmentR = (FieldRecRight) getFieldById(fieldId);
                fragmentR.setVisibleHouses(houses);
                break;
        }
    }

    private void setPlayerFrame(int fieldId, int idPlayer){
        switch (fieldId) {
            case 1: case 2: case 3: case 4: case 5: case 6: case 7: case 8: case 9:
                FieldRecBottom fragmentB = (FieldRecBottom) getFieldById(fieldId);
                fragmentB.setFramePlayer(idPlayer);
                break;

            case 11: case 12: case 13: case 14: case 15: case 16: case 17: case 18: case 19:
                FieldRecLeft fragmentL = (FieldRecLeft) getFieldById(fieldId);
                fragmentL.setFramePlayer(idPlayer);
                break;

            case 21: case 22: case 23: case 24: case 25: case 26: case 27: case 28: case 29:
                FieldRecTop fragmentT = (FieldRecTop) getFieldById(fieldId);
                fragmentT.setFramePlayer(idPlayer);
                break;

            case 31: case 32: case 33: case 34: case 35: case 36: case 37: case 38: case 39:
                FieldRecRight fragmentR = (FieldRecRight) getFieldById(fieldId);
                fragmentR.setFramePlayer(idPlayer);
                break;
        }
    }

    private void movePlayerFigure(int fieldFrom, int fieldTo, int idPlayer){
        switch (fieldFrom) {
            case 0: case 10: case 20: case 30:
                FieldSquare fragmentS = (FieldSquare) getFieldById(fieldFrom);
                fragmentS.setInvisiblePLayer(idPlayer);
                break;

            case 1: case 2: case 3: case 4: case 5: case 6: case 7: case 8: case 9:
                FieldRecBottom fragmentB = (FieldRecBottom) getFieldById(fieldFrom);
                fragmentB.setInvisiblePLayer(idPlayer);
                break;

            case 11: case 12: case 13: case 14: case 15: case 16: case 17: case 18: case 19:
                FieldRecLeft fragmentL = (FieldRecLeft) getFieldById(fieldFrom);
                fragmentL.setInvisiblePLayer(idPlayer);
                break;

            case 21: case 22: case 23: case 24: case 25: case 26: case 27: case 28: case 29:
                FieldRecTop fragmentT = (FieldRecTop) getFieldById(fieldFrom);
                fragmentT.setInvisiblePLayer(idPlayer);
                break;

            case 31: case 32: case 33: case 34: case 35: case 36: case 37: case 38: case 39:
                FieldRecRight fragmentR = (FieldRecRight) getFieldById(fieldFrom);
                fragmentR.setInvisiblePLayer(idPlayer);
                break;
        }

        switch (fieldTo) {
            case 0: case 10: case 20: case 30:
                FieldSquare fragmentS = (FieldSquare) getFieldById(fieldTo);
                fragmentS.setVisiblePLayer(idPlayer);
                break;

            case 1: case 2: case 3: case 4: case 5: case 6: case 7: case 8: case 9:
                FieldRecBottom fragmentB = (FieldRecBottom) getFieldById(fieldTo);
                fragmentB.setVisiblePLayer(idPlayer);
                break;

            case 11: case 12: case 13: case 14: case 15: case 16: case 17: case 18: case 19:
                FieldRecLeft fragmentL = (FieldRecLeft) getFieldById(fieldTo);
                fragmentL.setVisiblePLayer(idPlayer);
                break;

            case 21: case 22: case 23: case 24: case 25: case 26: case 27: case 28: case 29:
                FieldRecTop fragmentT = (FieldRecTop) getFieldById(fieldTo);
                fragmentT.setVisiblePLayer(idPlayer);
                break;

            case 31: case 32: case 33: case 34: case 35: case 36: case 37: case 38: case 39:
                FieldRecRight fragmentR = (FieldRecRight) getFieldById(fieldTo);
                fragmentR.setVisiblePLayer(idPlayer);
                break;
        }
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //textView = (TextView) findViewById(R.id.text);

        FieldSquare fragment0 = (FieldSquare) getFieldById(0);
        fragment0.setVisiblePLayer(1);
        fragment0.setVisiblePLayer(2);
        fragment0.setVisiblePLayer(3);
        fragment0.setVisiblePLayer(4);


        gameRef.get().addOnCompleteListener(gameFirstListen);


        testMessageRef.addValueEventListener(testMessageListener);

    }

    private void bindGameParameters() {
        //gameRef            .addListenerForSingleValueEvent(gameFirstListen);
        gameDice1Ref.addValueEventListener(gameDice1Listener);
        gameDice2Ref.addValueEventListener(gameDice2Listener);
        gameAuctionRef.addValueEventListener(gameAuctionListener);
        gameStateRef.addValueEventListener(gameStateListener);
        gameCurPlayerRef.addValueEventListener(gameCurPlayerListener);
        gameBankRef.addValueEventListener(gameBankListener);
        gamePausedPlayerRef.addValueEventListener(gamePausedPlayerListener);
        gameWinnerRef.addValueEventListener(gameWinnerListener);
        gameFieldsOwnersRef.addChildEventListener(gameFieldsOwnersListener);
        gamePlayersRef.addChildEventListener(gamePlayersListener);
    }
    
    /* List<String> users = new ArrayList<>();
        DatabaseReference myRef1 = database.getReference("players");

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                // A new comment has been added, add it to the displayed list
                String player = dataSnapshot.getValue(String.class);
                users.add(player);
                textView.setText(textView.getText()+player);

                // ...
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
                String player = dataSnapshot.getValue(String.class);
                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
                String player = dataSnapshot.getValue(String.class);
                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
            }
        };

        myRef1.addChildEventListener(childEventListener);*/


}