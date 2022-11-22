package repositories;

import java.util.Collections;

import entities.Auction;
import entities.Game;
import entities.Player;
import entities.Street;
import enums.GameStates;
import services.MapService;

public class GameRepository {

    private final Game game;

    public GameRepository(Game game) {
        this.game = game;
    }

    public void setDice1(int i){
        game.dice1 = i;
    }
    public void setDice2(int i){
        game.dice2 = i;
    }

    public void setWinner(int winnerId) {
        game.winnerId = winnerId;
    }

    public void setState(GameStates newState) {
        game.state = newState;
    }

    public void setAuction(Auction newAuction) {
        game.auction = newAuction;
    }

    public void addNewPlayer(Player newPlayer) {
        game.players.add(newPlayer);
    }

    public void mixPLayers() {
        Collections.shuffle(game.players);
    }

    public void setCurrentPlayerID(int i) {
        game.currentPlayerId = i;
    }

    public void setPausedPlayer(int playerId) {
        game.pausedPlayer = playerId;
    }

    public void setNewOwner(int idProperty, int newOwnerId) {
        game.fieldsOwners.get(idProperty).owner = newOwnerId;
    }

    public void addHouse(Street street) {
        game.fieldsOwners.get(
                MapService.getInstance()
                        .map.indexOf(street)
        ).houses++;
    }

    public void reduceHouses(Street street) {
        game.fieldsOwners.get(
                MapService.getInstance()
                        .map.indexOf(street)
        ).houses--;
    }
}
