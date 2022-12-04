package repositories;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import entities.Debt;
import entities.Game;
import entities.Offer;
import entities.Player;

//TODO все сеттеры перевести в сервис и там прописывать запись в БД

public class PlayerRepository {

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://monopoly-b9e36-default-rtdb.europe-west1.firebasedatabase.app/");
    DatabaseReference myRef1 = database.getReference("testGame");

    private final Game game;

    public PlayerRepository(Game game) {
        this.game = game;
    }

    public void addDebt(Player player, Debt newDebt){
        player.debts.add(newDebt);
        myRef1.child("players")
                .child(Integer.toString(game.players.indexOf(player)))
                .child("debts")
                .child(Integer.toString(player.debts.size()-1))
                .setValue(newDebt);
    }

    public void removeDebt(Player player, Debt debt){
        myRef1.child("players")
                .child(Integer.toString(game.players.indexOf(player)))
                .child("debts")
                .child(Integer.toString(player.debts.indexOf(debt)))
                .removeValue();
        player.debts.remove(debt);
    }

    public void setPosition(Player player, int position) {
        player.position = position;
        myRef1.child("players")
                .child(Integer.toString(game.players.indexOf(player)))
                .child("position")
                .setValue(position);
    }

    public void setCash(Player player, int cash) {
        player.cash = cash;
        myRef1.child("players")
                .child(Integer.toString(game.players.indexOf(player)))
                .child("cash")
                .setValue(cash);
    }

    public void addCach(Player player, int sum) {
        player.cash+=sum;
        myRef1.child("players")
                .child(Integer.toString(game.players.indexOf(player)))
                .child("cash")
                .setValue(player.cash);
    }

    public void reduceCash(Player player, int sum) {
        player.cash-=sum;
        myRef1.child("players")
                .child(Integer.toString(game.players.indexOf(player)))
                .child("cash")
                .setValue(player.cash);
    }

    public void setJailMove(Player player, int jailMove) {
        player.jailMove = jailMove;
        myRef1.child("players")
                .child(Integer.toString(game.players.indexOf(player)))
                .child("jailMove")
                .setValue(jailMove);
    }

    public void reduceJailMove(Player player) {
        player.jailMove--;
        myRef1.child("players")
                .child(Integer.toString(game.players.indexOf(player)))
                .child("jailMove")
                .setValue(player.jailMove);
    }


    public void setDoubles(Player player, int doubles) {
        player.doubles = doubles;
        myRef1.child("players")
                .child(Integer.toString(game.players.indexOf(player)))
                .child("doubles")
                .setValue(player.doubles);
    }

    public void increaseDoubles(Player player) {
        player.doubles++;
        myRef1.child("players")
                .child(Integer.toString(game.players.indexOf(player)))
                .child("doubles")
                .setValue(player.doubles);
    }

    public void setBankrupt(Player player, boolean bankrupt) {
        player.bankrupt = bankrupt;
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
        player.offers.add(offer);
        myRef1.child("players")
                .child(Integer.toString(game.players.indexOf(player)))
                .child("offers")
                .child(Integer.toString(player.offers.size()-1))
                .setValue(offer);
    }

    public void removeOffer(Player player, Offer offer) {
        if(player.offers.indexOf(offer)!=-1){
            myRef1.child("players")
                    .child(Integer.toString(game.players.indexOf(player)))
                    .child("offers")
                    .child(Integer.toString(player.offers.indexOf(offer)))
                    .removeValue();
            player.offers.remove(offer);
        }
    }



}
