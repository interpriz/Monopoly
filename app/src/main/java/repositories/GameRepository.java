package repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Collections;

import entities.Auction;
import entities.Game;
import entities.Player;
import entities.Street;
import enums.GameStates;
import services.MapService;

public class GameRepository implements OnCompleteListener<DataSnapshot> {

    private /*final*/ Game game;

    //FirebaseDatabase database = FirebaseDatabase.getInstance("https://monopoly-b9e36-default-rtdb.europe-west1.firebasedatabase.app/");
    public DatabaseReference DBGameReference;

    /*public GameRepository(Game game) {
        this.game = game;
    }*/

    private volatile static GameRepository sGameRepository;

    public static synchronized GameRepository getInstance(String gameName) {
        if(sGameRepository == null) {
            synchronized (FireBaseRepository.class) {
                sGameRepository = new GameRepository(gameName);
            }
        }
        return sGameRepository;
    }



    private GameRepository(String gameName) {
        DBGameReference = FireBaseRepository.getInstance()
                .getDatabase().getReference(gameName); //"testGame"
        addGameFirsListen();
    }

    public void addGameFirsListen() {
        DBGameReference.get().addOnCompleteListener(this);
    }

    //TODO добавить исключение и сделать проброс для выполнения bindGameParameters
    //первичное считавание игры (если ее нет, то создается новая)
    @Override
    public void onComplete(@NonNull Task<DataSnapshot> task) {
        if (!task.isSuccessful()) {
            Log.e("firebase", "Error getting data", task.getException());
        } else {
            Log.d("firebase", String.valueOf(task.getResult().getValue()));
            DataSnapshot ds = task.getResult();
            game = ds.getValue(Game.class);
        }
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        game = game;
        DBGameReference.setValue(game);
    }

    public void setDice1(int i) {
        game.dice1 = i;
        DBGameReference.child("dice1").setValue(i);
    }

    public void setDice2(int i) {
        game.dice2 = i;
        DBGameReference.child("dice2").setValue(i);
    }

    public void setWinner(int winnerId) {
        game.winnerId = winnerId;
        DBGameReference.child("winnerId").setValue(winnerId);
    }

    public void setState(GameStates newState) {
        game.state = newState;
        DBGameReference.child("state").setValue(newState);
    }

    public void setAuction(Auction newAuction) {
        game.auction = newAuction;
        DBGameReference.child("auction").setValue(newAuction);
    }

    public void setAuctionBet(int newBet) {
        game.auction.bet = newBet;
        DBGameReference.child("auction").child("bet").setValue(newBet);
    }

    public void setAuctionWinner(int idWinner) {
        game.auction.winner = idWinner;
        DBGameReference.child("auction").child("winner").setValue(idWinner);
    }

    public void setAuctionNewParticipant(int idPlayer) {

        DBGameReference.child("auction")
                .child("participants")
                .child(Integer.toString(game.auction.participants.size())).setValue(idPlayer);
        game.auction.participants.add(idPlayer);
    }

    public void setAuctionRemovePlayer(int idPlayer) {
        DBGameReference.child("auction")
                .child("participants")
                .child(Integer.toString(game.auction.participants.indexOf(idPlayer)))
                .removeValue();
        game.auction.participants.remove(idPlayer);

    }

    public void addNewPlayer(Player newPlayer) {
        DBGameReference.child("players")
                .child(Integer.toString(game.players.size())).setValue(newPlayer);
        game.players.add(newPlayer);
    }

    public void mixPLayers() {
        /*ArrayList<Player> buffer = new ArrayList<>();
        for(Player player: game.players){
            buffer.add(player);
        }*/
        Collections.shuffle(game.players);
        DBGameReference.child("players").setValue(game.players);
        /*for(int i=0; i< game.players.size(); i++){
            myRef1.child("players").child(i+"").setValue(game.players.get(i));
        }*/
        //game.players=buffer;

    }

    public void setCurrentPlayerID(int i) {
        //game.currentPlayerId = i;
        DBGameReference.child("currentPlayerId").setValue(i);
    }

    public void setPausedPlayer(int playerId) {
        game.pausedPlayer = playerId;
        DBGameReference.child("pausedPlayer").setValue(playerId);
    }

    public void setNewOwner(int idProperty, int newOwnerId) {
        game.fieldsOwners.get(idProperty).owner = newOwnerId;
        DBGameReference.child("fieldsOwners").child(String.valueOf(idProperty)).child("owner").setValue(newOwnerId);
    }

    public void setNewDeposit(int idProperty, boolean newDeposit) {
        game.fieldsOwners.get(idProperty).deposit = newDeposit;
        DBGameReference.child("fieldsOwners").child(String.valueOf(idProperty)).child("deposit").setValue(newDeposit);
    }


    public void addHouse(Street street) {
        int idProperty = MapService.getInstance()
                .map.indexOf(street);

        game.fieldsOwners.get(
                idProperty
        ).houses++;

        DBGameReference.child("fieldsOwners")
                .child(String.valueOf(idProperty))
                .child("houses").setValue(
                        game.fieldsOwners
                                .get(idProperty).houses
                );
    }

    public void reduceHouses(Street street) {
        int idProperty = MapService.getInstance()
                .map.indexOf(street);

        game.fieldsOwners.get(
                idProperty
        ).houses--;

        DBGameReference.child("fieldsOwners")
                .child(String.valueOf(idProperty))
                .child("houses").setValue(
                        game.fieldsOwners
                                .get(idProperty).houses
                );
    }

    public void deleteGame() {
        DBGameReference.removeValue();
    }





}
