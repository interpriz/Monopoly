package repositories;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;

import entities.Auction;
import entities.Game;
import entities.Player;
import entities.Street;
import enums.GameStates;
import services.MapService;

public class GameRepository {

    private final Game game;

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://monopoly-b9e36-default-rtdb.europe-west1.firebasedatabase.app/");
    DatabaseReference myRef1 = database.getReference("testGame");

    public GameRepository(Game game) {
        this.game = game;
    }

    public void setDice1(int i){
        game.dice1 = i;
        myRef1.child("dice1").setValue(i);
    }
    public void setDice2(int i){
        game.dice2 = i;
        myRef1.child("dice2").setValue(i);
    }

    public void setWinner(int winnerId) {
        game.winnerId = winnerId;
        myRef1.child("winnerId").setValue(winnerId);
    }

    public void setState(GameStates newState) {
        game.state = newState;
        myRef1.child("state").setValue(newState);
    }

    public void setAuction(Auction newAuction) {
        game.auction = newAuction;
        myRef1.child("auction").setValue(newAuction);
    }

    public void setAuctionBet(int newBet) {
        game.auction.bet=newBet;
        myRef1.child("auction").child("bet").setValue(newBet);
    }

    public void setAuctionWinner(int idWinner) {
        game.auction.winner = idWinner;
        myRef1.child("auction").child("winner").setValue(idWinner);
    }

    public void setAuctionNewParticipant(int idPlayer) {

        myRef1.child("auction")
                .child("participants")
                .child(Integer.toString(game.auction.participants.size())).setValue(idPlayer);
        game.auction.participants.add(idPlayer);
    }

    public void setAuctionRemovePlayer(int idPlayer) {
        myRef1.child("auction")
                .child("participants")
                .child(Integer.toString(game.auction.participants.indexOf(idPlayer)))
                .removeValue();
        game.auction.participants.remove(idPlayer);

    }

    public void addNewPlayer(Player newPlayer) {
        myRef1.child("players").child(Integer.toString(game.players.size())).setValue(newPlayer);
        game.players.add(newPlayer);
    }

    public void mixPLayers() {
        /*ArrayList<Player> buffer = new ArrayList<>();
        for(Player player: game.players){
            buffer.add(player);
        }*/
        Collections.shuffle(game.players);
        myRef1.child("players").setValue(game.players);
        /*for(int i=0; i< game.players.size(); i++){
            myRef1.child("players").child(i+"").setValue(game.players.get(i));
        }*/
        //game.players=buffer;

    }

    public void setCurrentPlayerID(int i) {
        //game.currentPlayerId = i;
        myRef1.child("currentPlayerId").setValue(i);
    }

    public void setPausedPlayer(int playerId) {
        game.pausedPlayer = playerId;
        myRef1.child("pausedPlayer").setValue(playerId);
    }

    public void setNewOwner(int idProperty, int newOwnerId) {
        game.fieldsOwners.get(idProperty).owner = newOwnerId;
        myRef1.child("fieldsOwners").child(String.valueOf(idProperty)).child("owner").setValue(newOwnerId);
    }

    public void setNewDeposit(int idProperty, boolean newDeposit) {
        game.fieldsOwners.get(idProperty).deposit = newDeposit;
        myRef1.child("fieldsOwners").child(String.valueOf(idProperty)).child("deposit").setValue(newDeposit);
    }



    public void addHouse(Street street) {
        int idProperty = MapService.getInstance()
                .map.indexOf(street);

        game.fieldsOwners.get(
                idProperty
        ).houses++;

        myRef1.child("fieldsOwners")
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

        myRef1.child("fieldsOwners")
                .child(String.valueOf(idProperty))
                .child("houses").setValue(
                        game.fieldsOwners
                                .get(idProperty).houses
                );
    }

    public void deleteGame(){
        myRef1.removeValue();
    }


}
