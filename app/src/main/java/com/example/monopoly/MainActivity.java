package com.example.monopoly;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.Service;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.monopoly.fragments.Authorisation;
import com.example.monopoly.fragments.EndOfGame;
import com.example.monopoly.fragments.FieldRecBottom;
import com.example.monopoly.fragments.FieldRecLeft;
import com.example.monopoly.fragments.FieldRecRight;
import com.example.monopoly.fragments.FieldRecTop;
import com.example.monopoly.fragments.FieldSquare;
import com.example.monopoly.fragments.OfferFrag;
import com.example.monopoly.fragments.PlayerFrag;
import com.example.monopoly.fragments.SoldBuyHouses;
import com.example.monopoly.fragments.SoldBuyProperty;
import com.example.monopoly.fragments.ViewOffersFrag;
import com.example.monopoly.fragments.DebtsFrag;
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
import java.util.Optional;

import entities.Auction;
import entities.FieldDB;
import entities.Game;
import entities.Offer;
import entities.Player;
import entities.User;
import enums.GameStates;
import enums.OfferStates;
import repositories.FireBaseRepository;
import repositories.GameRepository;
import repositories.PlayerRepository;
import services.GameService;
import services.MapService;
import static entities.StaticMessages.*;

// token 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
public class MainActivity extends AppCompatActivity {

    String text;
    private TextView textView;

    public GameService gameService = new GameService("testGame");

    //объекты логики игры
    //public Game game = gameService.getGame();
    public ArrayList<User> users = new ArrayList<User>();
    private String yourNickname;
    public Player yourPlayer; //TODO для теста




    //private GameRepository gr;
    //private PlayerRepository pr;
    private MapService mapService = MapService.getInstance();

    //объекты взаимодействия с БД
    FirebaseDatabase database = FireBaseRepository.getInstance().getDatabase(); //FirebaseDatabase.getInstance("https://monopoly-b9e36-default-rtdb.europe-west1.firebasedatabase.app/");
    public DatabaseReference usersRef = database.getReference("users");
    DatabaseReference gameRef = gameService.getGameRef();


    OnCompleteListener usersFirstListen = new OnCompleteListener<DataSnapshot>() {
        @Override
        public void onComplete(@NonNull Task<DataSnapshot> task) {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                Log.d("firebase", String.valueOf(task.getResult().getValue()));
                DataSnapshot ds = task.getResult();
                for (DataSnapshot childSnapshot: ds.getChildren()) {
                    User u = childSnapshot.getValue(User.class);
                    users.add(u);
                }
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container_view, authorisation)
                        .commit();
            }
        }
    };




    ValueEventListener gameDice1Listener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            gameService.getGame().dice1 = dataSnapshot.getValue(Integer.class);
            Log.d(TAG, "Dice1 value is: " + gameService.getGame().dice1);
            ImageView dice1Img = findViewById(R.id.dice1);
            dice1Img.setImageResource(dicesImages.get(gameService.getGame().dice1-1));
        }

        @Override
        public void onCancelled(DatabaseError error) {
            // Failed to read value
            Log.w(TAG, "Failed to read Dice1 value.", error.toException());
        }
    };
    ValueEventListener gameDice2Listener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            gameService.getGame().dice2 = dataSnapshot.getValue(Integer.class);
            Log.d(TAG, "Dice2 value is: " + gameService.getGame().dice2);
            ImageView dice2Img = findViewById(R.id.dice2);
            dice2Img.setImageResource(dicesImages.get(gameService.getGame().dice2-1));
        }

        @Override
        public void onCancelled(DatabaseError error) {
            // Failed to read value
            Log.w(TAG, "Failed to read Dice2 value.", error.toException());
        }
    };
    ValueEventListener gameAuctionListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            gameService.getGame().auction = dataSnapshot.getValue(Auction.class);
            Log.d(TAG, "Auction value is: " + gameService.getGame().auction);
        }

        @Override
        public void onCancelled(DatabaseError error) {
            // Failed to read value
            Log.w(TAG, "Failed to read Auction value.", error.toException());
        }
    };

    ValueEventListener gameStateListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            gameService.getGame().state = dataSnapshot.getValue(GameStates.class);
            Log.d(TAG, "Game state value is: " + gameService.getGame().state);
            ImageButton buttonPlayPause = findViewById(R.id.play_pause);
            switch (gameService.getGame().state){
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
            Log.w(TAG, "Failed to read GameState value.", error.toException());
        }
    };
    ValueEventListener gameCurPlayerListener = new ValueEventListener() {
        @SuppressLint("ResourceAsColor")
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.

            int newCurPlayerId = dataSnapshot.getValue(Integer.class);
            int oldCurPlayerId = gameService.getGame().currentPlayerId;
            Log.d(TAG, "CurrentPlayerId value is: " + newCurPlayerId);

            if(!gameService.getPlayer(oldCurPlayerId).bankrupt){
                PlayerFrag oldPlayerFragment = getPlayerFragByPlayersId(oldCurPlayerId);
                oldPlayerFragment.setFrame(-1);
            }

            PlayerFrag curPlayerFragment = getPlayerFragByPlayersId(newCurPlayerId);
            curPlayerFragment.setFrame(newCurPlayerId);

            gameService.getGame().currentPlayerId = newCurPlayerId;

            Player currentPlayer= gameService.getCurrentPlayer(); //TODO для теста

            Button btn = (Button) findViewById(R.id.moveBtn);
            if(newCurPlayerId==gameService.getGame().players.indexOf(yourPlayer)) {
                btn.setVisibility(View.VISIBLE);
            }else{
                btn.setVisibility(View.INVISIBLE);
            }

            // для работы с одного стройства
            /*if(newCurPlayerId==gameService.getGame().players.indexOf(yourPlayer) && gameService.getGame().state.equals(GameStates.onPlay)){
                Button btn = (Button) findViewById(R.id.moveBtn);
                if (yourPlayer.canRollDice)
                    btn.setText("Сделать ход");
                else
                    btn.setText("Завершить ход");
            }*/

        }

        @Override
        public void onCancelled(DatabaseError error) {
            // Failed to read value
            Log.w(TAG, "Failed to read CurrentPlayerId value.", error.toException());
        }
    };
    ValueEventListener gameBankListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            gameService.getGame().bank = dataSnapshot.getValue(Player.class);
            Log.d(TAG, "Bank value is: " + gameService.getGame().bank);
        }

        @Override
        public void onCancelled(DatabaseError error) {
            // Failed to read value
            Log.w(TAG, "Failed to read Bank value.", error.toException());
        }
    };
    ValueEventListener gamePausedPlayerListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            gameService.getGame().pausedPlayer = dataSnapshot.getValue(Integer.class);
            Log.d(TAG, "PausedPLayerId value is: " + gameService.getGame().pausedPlayer);
        }

        @Override
        public void onCancelled(DatabaseError error) {
            // Failed to read value
            Log.w(TAG, "Failed to read PausedPLayerId value.", error.toException());
        }
    };
    ValueEventListener gameWinnerListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            gameService.getGame().winnerId = dataSnapshot.getValue(Integer.class);
            if ( gameService.getGame().winnerId!=-1)
                showWinOrLoose(gameService.getGame().winnerId, true);
            Log.d(TAG, "WinnerId value is: " + gameService.getGame().winnerId);
        }

        @Override
        public void onCancelled(DatabaseError error) {
            // Failed to read value
            Log.w(TAG, "Failed to read WinnerId value.", error.toException());
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
            //gameService.getGame().fieldsOwners.add(field);

            // ...
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
            Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

            // A comment has changed, use the key to determine if we are displaying this
            // comment and if so displayed the changed comment.
            FieldDB updatedField = dataSnapshot.getValue(FieldDB.class);
            int fieldId = Integer.parseInt(dataSnapshot.getKey());
            gameService.getGame().fieldsOwners.set(fieldId, updatedField);
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

            Player newPlayer = dataSnapshot.getValue(Player.class);
            boolean isNewPlayer = gameService.getGame().players.stream().noneMatch(x -> x.name.equals(newPlayer.name));
            // A new comment has been added, add it to the displayed list
            if(isNewPlayer){
                gameService.getGame().players.add(newPlayer);
            }else{

            }

            int idPlayer = Integer.parseInt(dataSnapshot.getKey());
            PlayerFrag playerI =  getPlayerFragByPlayersId(idPlayer);
            playerI.setCash(newPlayer.cash);
            playerI.setPlayerName(newPlayer.name);
            playerI.setImage(playersImages.get(idPlayer));
            movePlayerFigure(0, newPlayer.position, idPlayer);
            if(newPlayer.bankrupt){
                playerI.setBankrupt();
            }

            playerI.setJail(newPlayer.jailMove);
            playerI.setOffers((int) newPlayer.offers.stream().filter(x->x.state== OfferStates.newOffer).count());


            if(flag && gameService.getGame().players.size()==gameService.getGame().maxPLayers){
                if(yourPlayer.name.equals(gameService.getGame().organizer)){
                    String result = gameService.startGame(yourPlayer);
                }
                gameRef.child("currentPlayerId").addValueEventListener(gameCurPlayerListener);
                flag = false;
            }

            if(yourPlayer.name.equals(newPlayer.name)) {
                Button btn = (Button) findViewById(R.id.moveBtn);
                if (!yourPlayer.canRollDice && gameService.getCurrentPlayer().name.equals(yourPlayer.name))
                    btn.setText("Завершить ход");
            }

            Log.d(TAG, "Player added:" + newPlayer.name);

            // ...
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {


            // A comment has changed, use the key to determine if we are displaying this
            // comment and if so displayed the changed comment.
            Player updatedPlayer = dataSnapshot.getValue(Player.class);
            int playerId = Integer.parseInt(dataSnapshot.getKey());
            Player oldPlayer = gameService.getGame().players.get(playerId);


            int oldPosition = oldPlayer.position;
            int newPosition = updatedPlayer.position;
            if(oldPosition != newPosition){
                movePlayerFigure(oldPosition,newPosition,playerId);
            }

            PlayerFrag playerFrag = getPlayerFragByPlayersId(playerId);
            playerFrag.setCash(updatedPlayer.cash);

            if(updatedPlayer.bankrupt && !oldPlayer.bankrupt){
                playerFrag.setBankrupt();
                if (updatedPlayer.name.equals(yourPlayer.name))
                    showWinOrLoose(playerId, false);
            }

            playerFrag.setJail(updatedPlayer.jailMove);

            playerFrag.setJail(updatedPlayer.jailMove);
            playerFrag.setOffers((int) updatedPlayer.offers.stream().filter(x->x.state== OfferStates.newOffer).count());

            gameService.getGame().players.set(playerId, updatedPlayer);

            /*if(updatedPlayer.name.equals(yourPlayer.name)){
                yourPlayer = gameService.getGame().players.get(playerId);
            }*/

            //TODO для теста
            if(updatedPlayer.name.equals(yourPlayer.name)){
                yourPlayer = gameService.getGame().players.get(playerId);

                Button btn = (Button) findViewById(R.id.moveBtn);
                if (!yourPlayer.canRollDice && gameService.getCurrentPlayer().name.equals(yourPlayer.name))
                    btn.setText("Завершить ход");
                else
                    btn.setText("Сделать ход");
            }

            Log.d(TAG, "Player changed:" + updatedPlayer.name);

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

    LinearLayout opponents;
    LinearLayout you;
    GridLayout buttons;



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
        int yourPlayerId = gameService.getGame().players.indexOf(yourPlayer);
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

    private void bindGameParameters() {
        //gameRef            .addListenerForSingleValueEvent(gameFirstListen);
        gameRef.child("dice1").addValueEventListener(gameDice1Listener);
        gameRef.child("dice2").addValueEventListener(gameDice2Listener);
        gameRef.child("auction").addValueEventListener(gameAuctionListener);
        gameRef.child("state").addValueEventListener(gameStateListener);
        //gameCurPlayerRef.addValueEventListener(gameCurPlayerListener);
        gameRef.child("bank").addValueEventListener(gameBankListener);
        gameRef.child("pausedPlayer").addValueEventListener(gamePausedPlayerListener);
        gameRef.child("winnerId").addValueEventListener(gameWinnerListener);
        gameRef.child("fieldsOwners").addChildEventListener(gameFieldsOwnersListener);
        gameRef.child("players").addChildEventListener(gamePlayersListener);

        opponents.setVisibility(View.VISIBLE);
        you.setVisibility(View.VISIBLE);
        buttons.setVisibility(View.VISIBLE);
    }


    public Fragment authorisation = new Authorisation();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        opponents = findViewById(R.id.opponents);
        you = findViewById(R.id.you);
        buttons = findViewById(R.id.buttons);

        opponents.setVisibility(View.INVISIBLE);
        you.setVisibility(View.INVISIBLE);
        buttons.setVisibility(View.INVISIBLE);

        usersRef.get().addOnCompleteListener(usersFirstListen);


    }

    public void start(String name){
        yourNickname = name;

        //gameRef.get().addOnCompleteListener(gameFirstListen);

        yourPlayer = gameService.gameInitialise(yourNickname);
        if (yourPlayer==null){
            showMessage("Игра уже идет!");

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container_view, Authorisation.class, null)
                    .commit();
        }else{
            getSupportFragmentManager().beginTransaction()
                    .remove(authorisation)
                    .commit();
            bindGameParameters();
        }


    }









    public void pausePLayClick(View view){

        String result = SUCCESS;
        switch (gameService.getGame().state){
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
        //gameService.setD1D2(gameService.getGame().dice1, gameService.getGame().dice2);
        //Player curPlayer = yourPlayer; //gameService.getCurrentPlayer();

        Button btn = (Button) view;
        String mode = btn.getText().toString();


        switch (mode){
            case"Сделать ход":
                String result = gameService.makeMove(yourPlayer);
                switch (result){
                    case SUCCESS:
                        btn.setText("Завершить ход");
                        break;
                    case BUY_OR_AUCTION:
                        int sum = yourPlayer.getLastOffer().sum;
                        String propertyName = mapService.getPropertyNameByPosition(yourPlayer.getLastOffer().senderPropertyID);

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Важное сообщение!")
                                .setMessage("Хотите купить собственность \""+propertyName+"\" за "+sum+"$ или начать аукцион?")
                                .setCancelable(false)
                                .setPositiveButton("Купить",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                Offer bankOffer = yourPlayer.offers.get(yourPlayer.offers.size()-1);
                                                String acceptRes = gameService.acceptOffer(bankOffer, yourPlayer);
                                                dialog.cancel();
                                                showMessage(acceptRes);
                                            }
                                        })
                                .setNegativeButton("Аукцион",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                //gameService.startAuction(currentPlayer.offers.get(0).senderProperty());
                                                gameService.rejectOffer(yourPlayer.getLastOffer(), yourPlayer);
                                                dialog.cancel();
                                            }
                                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                        btn.setText("Завершить ход");
                        break;
                    default:
                        Toast toast = Toast.makeText(getApplicationContext(),
                                result, Toast.LENGTH_SHORT);
                        toast.show();
                        break;
                }

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
        String result = gameService.goOutFromJail(yourPlayer);
        showMessage(result);
    }

    public void depositBuyClick(View view) {
        getSupportFragmentManager().popBackStack();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container_view, SoldBuyProperty.class, null)
                .commit();


    }

    public void buySoldHouseClick(View view) {
        getSupportFragmentManager().popBackStack();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container_view, SoldBuyHouses.class, null)
                .commit();

    }



    public void offerClick(String playerName) {
        if(playerName.equals(yourPlayer.name)){
            getSupportFragmentManager().popBackStack();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container_view, ViewOffersFrag.class, null)
                    .commit();
        }else{
            Player player = gameService.getGame().players.stream().filter(x->x.name.equals(playerName)).findFirst().get();
            int idPlayer = gameService.getGame().players.indexOf(player);
            Bundle args = new Bundle();
            args.putInt("idPlayer", idPlayer);
            getSupportFragmentManager().popBackStack();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container_view, OfferFrag.class, args)
                    .commit();
        }


    }

    public void debtClick(View view) {
        getSupportFragmentManager().popBackStack();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container_view, DebtsFrag.class, null)
                .commit();
    }


    public void showMessage(String mes){

        /*if(!mes.equals(SUCCESS)){

        }*/
        Toast toast = Toast.makeText(getApplicationContext(),
                mes, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void showWinOrLoose(int idPlayer, boolean win){
        Bundle args = new Bundle();
        args.putInt("idPlayer", idPlayer);
        args.putBoolean("win", win);
        getSupportFragmentManager().popBackStack();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container_view, EndOfGame.class, args)
                .commit();
        /*try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        getSupportFragmentManager().popBackStack();*/

    }



}