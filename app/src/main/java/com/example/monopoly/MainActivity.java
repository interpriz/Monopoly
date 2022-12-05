package com.example.monopoly;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    private GameService gameService;
    private GameRepository gr;
    private PlayerRepository pr;
    private MapService mapService = MapService.getInstance();
    
    //объекты взаимодействия с БД
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://monopoly-b9e36-default-rtdb.europe-west1.firebasedatabase.app/");
    DatabaseReference testMessageRef        = database.getReference("testMessage");
    DatabaseReference gameRef               = database.getReference("testGame");
    DatabaseReference gameDice1Ref          = gameRef.child("dice1");
    DatabaseReference gameDice2Ref          = gameRef.child("dice2");
    DatabaseReference gameAuctionRef        = gameRef.child("auction");
    DatabaseReference gameStateRef          = gameRef.child("state");
    DatabaseReference gameCurPlayerRef      = gameRef.child("currentPlayerId");
    DatabaseReference gameBankRef           = gameRef.child("bank");
    DatabaseReference gamePausedPlayerRef   = gameRef.child("pausedPlayer");
    DatabaseReference gameWinnerRef         = gameRef.child("winnerId");
    DatabaseReference gameFieldsOwnersRef   = gameRef.child("fieldsOwners");
    DatabaseReference gamePlayersRef        = gameRef.child("players");
    
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
            }
            else {
                Log.d("firebase", String.valueOf(task.getResult().getValue()));
                DataSnapshot ds = task.getResult();
                game = ds.getValue(Game.class);
                int a =0;
                if(game==null){
                    game = new Game(4,"God");
                    gameRef.setValue(game);
                    gameService.enterGame("God");
                    gameService.enterGame("Sasha");
                    gameService.enterGame("Sveta");
                    gameService.enterGame("Lola");
                    gr.setNewOwner(2, 0);
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


    ValueEventListener gameDice1Listener        = new ValueEventListener() {
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
    ValueEventListener gameDice2Listener        = new ValueEventListener() {
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
    ValueEventListener gameAuctionListener      = new ValueEventListener() {
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
    ValueEventListener gameStateListener        = new ValueEventListener() {
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
    ValueEventListener gameCurPlayerListener    = new ValueEventListener() {
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
    ValueEventListener gameBankListener         = new ValueEventListener() {
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
    ValueEventListener gameWinnerListener       = new ValueEventListener() {
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //textView = (TextView) findViewById(R.id.text);



        gameRef.get().addOnCompleteListener(gameFirstListen);


        testMessageRef.addValueEventListener(testMessageListener);

    }
    
    private void bindGameParameters(){
        //gameRef            .addListenerForSingleValueEvent(gameFirstListen);
        gameDice1Ref       .addValueEventListener(gameDice1Listener);
        gameDice2Ref       .addValueEventListener(gameDice2Listener);
        gameAuctionRef     .addValueEventListener(gameAuctionListener);
        gameStateRef       .addValueEventListener(gameStateListener);
        gameCurPlayerRef   .addValueEventListener(gameCurPlayerListener);
        gameBankRef        .addValueEventListener(gameBankListener);
        gamePausedPlayerRef.addValueEventListener(gamePausedPlayerListener);
        gameWinnerRef      .addValueEventListener(gameWinnerListener);
        gameFieldsOwnersRef.addChildEventListener(gameFieldsOwnersListener);
        gamePlayersRef     .addChildEventListener(gamePlayersListener);
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