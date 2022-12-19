package com.example.monopoly;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.monopoly.fragments.FieldRecBottom;
import com.example.monopoly.fragments.FieldRecLeft;
import com.example.monopoly.fragments.FieldRecRight;
import com.example.monopoly.fragments.FieldRecTop;
import com.example.monopoly.fragments.FieldSquare;
import com.example.monopoly.fragments.PlayerFrag;
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
import java.util.stream.Collectors;

import entities.Auction;
import entities.FieldDB;
import entities.Game;
import entities.Offer;
import entities.Player;
import entities.Property;
import enums.GameStates;
import repositories.GameRepository;
import repositories.PlayerRepository;
import services.GameService;
import services.MapService;
import static entities.StaticMessages.*;

// token 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
public class MainActivity extends AppCompatActivity {

    String text;
    private TextView textView;

    //объекты логики игры 
    private Game game;
    private Player yourPlayer;
    private Player currentPlayer; //TODO для теста
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
                }else{
                    yourPlayer = game.players.get(0);
                }

                gameService = new GameService(game);
                //TODO для теста
                currentPlayer = gameService.getCurrentPlayer();
                gameService.setTest(true);
                gr = new GameRepository(game);
                pr = new PlayerRepository(game);

                bindGameParameters();
                //outPutPlayersIFragmentsInfo();
                


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
            ImageView dice1Img = findViewById(R.id.dice1);
            dice1Img.setImageResource(dicesImages.get(game.dice1-1));
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
            ImageView dice2Img = findViewById(R.id.dice2);
            dice2Img.setImageResource(dicesImages.get(game.dice2-1));
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
            ImageButton buttonPlayPause = findViewById(R.id.play_pause);
            switch (game.state){
                case onStart:case onEnd:
                    buttonPlayPause.setBackgroundResource(0);
                    break;
                case onPause:
                    buttonPlayPause.setBackgroundResource(R.drawable.play);
                    break;
                case onPlay:
                    buttonPlayPause.setBackgroundResource(R.drawable.pause);
                    break;

            }
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

            int newCurPlayerId = dataSnapshot.getValue(Integer.class);
            int oldCurPlayerId = game.currentPlayerId;
            Log.d(TAG, "Value is: " + newCurPlayerId);

            PlayerFrag oldPlayerFragment = getPlayerFragByPlayersId(oldCurPlayerId);
            oldPlayerFragment.setFrame(-1);

            PlayerFrag curPlayerFragment = getPlayerFragByPlayersId(newCurPlayerId);
            curPlayerFragment.setFrame(newCurPlayerId);

            game.currentPlayerId = newCurPlayerId;


            if(newCurPlayerId==game.players.indexOf(yourPlayer) && game.state.equals(GameStates.onPlay)){
                Button btn = (Button) findViewById(R.id.moveBtn);
                if (yourPlayer.canRollDice)
                    btn.setText("Сделать ход");
                else
                    btn.setText("Завершить ход");
            }

            //TODO для теста
            currentPlayer= gameService.getCurrentPlayer();



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
            FieldDB field = dataSnapshot.getValue(FieldDB.class);
            int fieldId = Integer.parseInt(dataSnapshot.getKey());
            setHousesOnField(fieldId,field.houses);
            setPlayerFrame(fieldId, field.owner, field.deposit);
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
            setPlayerFrame(fieldId, updatedField.owner, updatedField.deposit);

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

    boolean flag = true;
    ChildEventListener gamePlayersListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
            Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
            Player newPlayer = dataSnapshot.getValue(Player.class);
            boolean isNewPlayer = game.players.stream().noneMatch(x -> x.name.equals(newPlayer.name));
            // A new comment has been added, add it to the displayed list
            if(isNewPlayer){
                game.players.add(newPlayer);
            }else{
                int idPlayer = Integer.parseInt(dataSnapshot.getKey());
                PlayerFrag playerI =  getPlayerFragByPlayersId(idPlayer);
                playerI.setCash(newPlayer.cash);
                playerI.setPlayerName(newPlayer.name);
                playerI.setImage(playersImages.get(idPlayer));
                movePlayerFigure(0, newPlayer.position, idPlayer);
            }
            if(flag && game.players.size()==game.maxPLayers){
                if(yourPlayer.name.equals(game.organizer)){
                    String result = gameService.startGame(yourPlayer);
                }
                flag = false;
            }

            // ...
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
            Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

            // A comment has changed, use the key to determine if we are displaying this
            // comment and if so displayed the changed comment.
            Player updatedPlayer = dataSnapshot.getValue(Player.class);
            int playerId = Integer.parseInt(dataSnapshot.getKey());
            Player oldPlayer = game.players.get(playerId);


            int oldPosition = oldPlayer.position;
            int newPosition = updatedPlayer.position;
            if(oldPosition != newPosition){
                movePlayerFigure(oldPosition,newPosition,playerId);
            }

            PlayerFrag playerFrag = getPlayerFragByPlayersId(playerId);
            playerFrag.setCash(updatedPlayer.cash);

            game.players.set(playerId, updatedPlayer);

            if(updatedPlayer.name.equals(yourPlayer.name)){
                yourPlayer= game.players.get(playerId);
            }

            //TODO для теста
            if(updatedPlayer.name.equals(currentPlayer.name)){
                currentPlayer= game.players.get(playerId);
            }


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
            Player movedField = dataSnapshot.getValue(Player.class);
            String fieldId = dataSnapshot.getKey();
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

    public List<Integer> playersImages = new ArrayList<Integer>(
            Arrays.asList(
                    R.drawable.player_1, R.drawable.player_2, R.drawable.player_3, R.drawable.player_4
            )
    );

    public List<Integer> dicesImages = new ArrayList<Integer>(
            Arrays.asList(
                    R.drawable.dice_1, R.drawable.dice_2, R.drawable.dice_3,
                    R.drawable.dice_4, R.drawable.dice_5, R.drawable.dice_6
            )
    );

    public List<Integer> playersFragsRID = new ArrayList<Integer>(
            Arrays.asList(
                    R.id.player_1, R.id.player_2, R.id.player_3
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

    private void setPlayerFrame(int fieldId, int idPlayer, boolean deposit){
        switch (fieldId) {
            case 1: case 2: case 3: case 4: case 5: case 6: case 7: case 8: case 9:
                FieldRecBottom fragmentB = (FieldRecBottom) getFieldById(fieldId);
                fragmentB.setFramePlayer(idPlayer, deposit);
                break;

            case 11: case 12: case 13: case 14: case 15: case 16: case 17: case 18: case 19:
                FieldRecLeft fragmentL = (FieldRecLeft) getFieldById(fieldId);
                fragmentL.setFramePlayer(idPlayer, deposit);
                break;

            case 21: case 22: case 23: case 24: case 25: case 26: case 27: case 28: case 29:
                FieldRecTop fragmentT = (FieldRecTop) getFieldById(fieldId);
                fragmentT.setFramePlayer(idPlayer, deposit);
                break;

            case 31: case 32: case 33: case 34: case 35: case 36: case 37: case 38: case 39:
                FieldRecRight fragmentR = (FieldRecRight) getFieldById(fieldId);
                fragmentR.setFramePlayer(idPlayer, deposit);
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

    private PlayerFrag getPlayerFragByPlayersId(int idPlayer){
        int playerRID;
        int yourPlayerId = game.players.indexOf(yourPlayer);
        if(idPlayer==yourPlayerId){
            playerRID = R.id.yourPlayer;
        }
        else{
            if(idPlayer<yourPlayerId)
                playerRID = playersFragsRID.get(idPlayer);
            else
                playerRID = playersFragsRID.get(idPlayer-1);
        }
        return (PlayerFrag) getSupportFragmentManager().findFragmentById(playerRID);
    }

    //вывод информации о игроках на экран
    /*private void outPutPlayersIFragmentsInfo(){
        for(Player player: game.players){
            int idPlayer = game.players.indexOf(player);
            PlayerFrag playerFrag =  getPlayerFragByPlayersId(idPlayer);
            playerFrag.setCash(player.cash);
            playerFrag.setPlayerName(player.name);
            playerFrag.setImage(playersImages.get(idPlayer));
        }
    }*/
    
    





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //textView = (TextView) findViewById(R.id.text);

        gameRef.get().addOnCompleteListener(gameFirstListen);

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

    public void pausePLayClick(View view){

        String result = SUCCESS;
        switch (game.state){
            case onPlay:
                result = gameService.pauseGame(yourPlayer);
                break;
            case onPause:
                result = gameService.continueGame(yourPlayer);
                break;

        }
        if(result!=SUCCESS){
            Toast toast = Toast.makeText(getApplicationContext(),
                    result, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void moveClick(View view){
        //TODO для теста
        gameService.setD1D2(game.dice1, game.dice2);
        //Player curPlayer = yourPlayer; //gameService.getCurrentPlayer();

        Button btn = (Button) view;
        String mode = btn.getText().toString();


        switch (mode){
            case"Сделать ход":
                String result = gameService.makeMove(currentPlayer);
                switch (result){
                    case SUCCESS:
                        btn.setText("Завершить ход");
                        break;
                    case BUY_OR_AUCTION:
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Важное сообщение!")
                                .setMessage("Хотите купить собственность или начать аукцион?")
                                .setCancelable(false)
                                .setPositiveButton("Купить",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                Offer bankOffer = currentPlayer.offers.get(currentPlayer.offers.size()-1);
                                                String acceptRes = gameService.acceptOffer(bankOffer, currentPlayer);
                                                dialog.cancel();
                                                showMessage(acceptRes);
                                            }
                                        })
                                .setNegativeButton("Аукцион",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                //gameService.startAuction(currentPlayer.offers.get(0).senderProperty());
                                                dialog.cancel();
                                            }
                                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                        break;
                    default:
                        Toast toast = Toast.makeText(getApplicationContext(),
                                result, Toast.LENGTH_SHORT);
                        toast.show();
                        break;
                }
                btn.setText("Завершить ход");

                break;
            case "Завершить ход":
                String endMotionRes = gameService.endMotion();
                if(endMotionRes.equals(SUCCESS))
                    btn.setText("Сделать ход");
                showMessage(endMotionRes);
                break;
        }

    }

    public void goOutFromJailClick(View view) {
        String result = gameService.goOutFromJail(currentPlayer);
        showMessage(result);
    }

    public void showMessage(String mes){
        if(!mes.equals(SUCCESS)){
            Toast toast = Toast.makeText(getApplicationContext(),
                    mes, Toast.LENGTH_SHORT);
            toast.show();
        }
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