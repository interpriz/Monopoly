package repositories;

import entities.Debt;
import entities.Game;
import entities.Offer;
import entities.Player;

//TODO все сеттеры перевести в сервис и там прописывать запись в БД

public class PlayerRepository {

    private final Game game;

    public PlayerRepository(Game game) {
        this.game = game;
    }

    public void addDebt(Player player, Debt newDebt){
        player.debts.add(newDebt);
    }

    public void removeDebt(Player player, Debt debt){
        player.debts.remove(debt);
    }

    public void setPosition(Player player, int position) {
        player.position = position;
    }

    public void setCash(Player player, int cash) {
        player.cash = cash;
    }

    public void addCach(Player player, int sum) {
        player.cash+=sum;
    }

    public void reduceCash(Player player, int sum) {
        player.cash-=sum;
    }

    public void setJailMove(Player player, int jailMove) {
        player.jailMove = jailMove;
    }

    public void reduceJailMove(Player player) {
        player.jailMove--;
    }


    public void setDoubles(Player player, int doubles) {
        player.doubles = doubles;
    }

    public void increaseDoubles(Player player) {
        player.doubles++;
    }

    public void setBankrupt(Player player, boolean bankrupt) {
        player.bankrupt = bankrupt;
    }

    public void setCanRollDice(Player player, boolean canRollDice) {
        player.canRollDice = canRollDice;
    }

    public void addOffer(Player player, Offer offer) {
        player.offers.add(offer);
    }



}
