package repositories;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import entities.Debt;
import entities.Game;
import entities.Offer;
import entities.Player;
import enums.OfferStates;

//TODO все сеттеры перевести в сервис и там прописывать запись в БД

public class PlayerRepository {

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://monopoly-b9e36-default-rtdb.europe-west1.firebasedatabase.app/");
    DatabaseReference myRef1 = database.getReference("testGame");

    private final Game game;

    public PlayerRepository(Game game) {
        this.game = game;
    }

    public void addDebt(Player player, Debt newDebt){

        myRef1.child("players")
                .child(Integer.toString(game.players.indexOf(player)))
                .child("debts")
                .child(Integer.toString(player.debts.size()))
                .setValue(newDebt);
        player.debts.add(newDebt);
    }

    public void removeDebt(Player player, Debt debt){
        /*myRef1.child("players")
                .child(Integer.toString(game.players.indexOf(player)))
                .child("debts")
                .child(Integer.toString(player.debts.indexOf(debt)))
                .removeValue();*/
        player.debts.remove(debt);
        myRef1.child("players")
                .child(Integer.toString(game.players.indexOf(player)))
                .child("debts")
                .setValue(player.debts);
    }

    public void setPosition(Player player, int position) {
        //player.position = position;
        myRef1.child("players")
                .child(Integer.toString(game.players.indexOf(player)))
                .child("position")
                .setValue(position);
    }

    public void setCash(Player player, int cash) {
        player.cash = cash;
        if(player!=game.bank){
            myRef1.child("players")
                    .child(Integer.toString(game.players.indexOf(player)))
                    .child("cash")
                    .setValue(cash);
        }else{
            myRef1.child("bank")
                    .child("cash")
                    .setValue(cash);
        }

    }

    public void addCach(Player player, int sum) {
        int newsum = player.cash+sum;
        setCash(player, newsum);
    }

    public void reduceCash(Player player, int sum) {
        int newsum = player.cash-sum;
        setCash(player, newsum);
    }

    public void setJailMove(Player player, int jailMove) {
        player.jailMove = jailMove;
        myRef1.child("players")
                .child(Integer.toString(game.players.indexOf(player)))
                .child("jailMove")
                .setValue(jailMove);
    }

    public void reduceJailMove(Player player) {
        int newJailMove = player.jailMove-1;
        setJailMove(player, newJailMove);
    }


    public void setDoubles(Player player, int doubles) {
        player.doubles = doubles;
        myRef1.child("players")
                .child(Integer.toString(game.players.indexOf(player)))
                .child("doubles")
                .setValue(player.doubles);
    }

    public void increaseDoubles(Player player) {
        int newDoubles = player.doubles+1;
        setDoubles(player, newDoubles);
    }

    public void setBankrupt(Player player, boolean bankrupt) {
        //player.bankrupt = bankrupt;
        myRef1.child("players")
                .child(Integer.toString(game.players.indexOf(player)))
                .child("bankrupt")
                .setValue(bankrupt);
    }

    public void setCanRollDice(Player player, boolean canRollDice) {
        player.canRollDice = canRollDice;
        myRef1.child("players")
                .child(Integer.toString(game.players.indexOf(player)))
                .child("canRollDice")
                .setValue(canRollDice);
    }

    public void addOffer(Player player, Offer offer) {
        if(player!=game.bank){
            myRef1.child("players")
                    .child(Integer.toString(game.players.indexOf(player)))
                    .child("offers")
                    .child(Integer.toString(player.offers.size()))
                    .setValue(offer);
            player.offers.add(offer);
        }
    }

    public void setOfferState(Player player, Offer offer, OfferStates newState) {
        if(player!=game.bank){
            myRef1.child("players")
                    .child(Integer.toString(game.players.indexOf(player)))
                    .child("offers")
                    .child(Integer.toString(player.offers.indexOf(offer)))
                    .child("state")
                    .setValue(newState);
            offer.state = newState;
        }
    }

    /*public void removeOffer(Player player, Offer offer) {
        if(player.offers.indexOf(offer)!=-1){
            myRef1.child("players")
                    .child(Integer.toString(game.players.indexOf(player)))
                    .child("offers")
                    .child(Integer.toString(player.offers.indexOf(offer)))
                    .removeValue();
            player.offers.remove(offer);
        }
    }*/



}
