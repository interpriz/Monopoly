package entities;

import java.util.ArrayList;
import java.util.UUID;

import enums.GameStates;
import services.MapService;

import static enums.GameStates.*;


public class Game {

    //public Long id;
    public String organizer; //имя организатора

    public int maxPLayers; //максимальное количество игроков

    public int dice1 = 1; //число на первом кубике
    public int dice2 = 1; //число на втором кубике

    public Auction auction = null;
    public GameStates state = onStart;

    public int currentPlayerId =0;
    public Player bank;
    public int pausedPlayer = -1;

    public int winnerId = -1;
    // индекс поля в списке map -> номер игрока в списке players
    public ArrayList<FieldDB> fieldsOwners = new ArrayList<>();
    public ArrayList<Player> players = new ArrayList<>();

    public Game(){
        this.maxPLayers = 4;
        this.bank = new Player(21100, "BANK");
        for(int i=0;i<40;i++){
            fieldsOwners.add(new FieldDB(-1,0));
        }
    }

    public Game(int maxPLayers, String organizer) {
        //id = UUID.randomUUID();
        this.organizer = organizer;
        this.maxPLayers = maxPLayers;
        this.bank = new Player(21100, "BANK");
        for(int i=0;i<40;i++){
            fieldsOwners.add(new FieldDB(-1,0));
        }
    }
/*
    public Player enterGame(){
        Player newPlayer = new Player(1500);
        if(players.size()<maxPLayers){
            players.add(newPlayer);
            return newPlayer;
        }else
            return null;
    }

    public void startGame(){
        state = onPlay;
        //случайное перемешивание игроков
        Collections.shuffle(players);
        players.get(0).setCanRollDice(true);
    }

    public Player getCurrentPlayer(){
        return players.get(currentPlayerId);
    }

    public boolean isCurrentPLayer(Player player){
        return currentPlayerId==players.indexOf(player);
    }

    public void giveDicesToNextPlayer(){
        getCurrentPlayer().setCanRollDice(false);
        do {
            if (currentPlayerId == players.size() - 1) {
                currentPlayerId = 0;
            } else
                currentPlayerId++;
        } while (getCurrentPlayer().isBankrupt());
        getCurrentPlayer().setCanRollDice(true);
    }

    public String pauseGame(Player player){
        if(state == onPlay){
            state = onPause;
            pausedPlayer = players.indexOf(player);
            return SUCCESS;
        }
        return "Game is already paused!";
    }
    public String continueGame(Player player){
        if(pausedPlayer == players.indexOf(player)){
            state = onPlay;
            pausedPlayer = -1;
            return SUCCESS;
        }
        return "You didn't pause the game!";
    }

    public Player getOwner(Property property) {
        int idOwner = fieldsOwners
                .get(MapService.getInstance().map.indexOf(property))
                .owner;
        return idOwner==-1 ? bank : players.get(idOwner);
    }

    public void setOwner(Property property, Player newOwner){
        int idProperty = MapService.getInstance().map.indexOf(property);
        int newOwnerId = players.indexOf(newOwner);
        fieldsOwners.get(idProperty).owner=newOwnerId;
    }

    public void addHouse(Street street) {
        fieldsOwners.get(
                MapService.getInstance()
                        .map.indexOf(street)
        ).houses++;
    }

    public void reduceHouses(Street street) {
        fieldsOwners.get(
                MapService.getInstance()
                        .map.indexOf(street)
        ).houses--;
    }

    public int getHouses(Street street) {
        return fieldsOwners.get(
                MapService.getInstance()
                        .map.indexOf(street)
        ).houses;
    }*/

}
