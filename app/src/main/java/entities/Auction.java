package entities;

import java.util.ArrayList;

import services.GameService;

public class Auction {

    public final Property property;

    public int bet;

    public Player winner = null;

    public final Player creator;

    public ArrayList<Player> participants = new ArrayList<>();

    public Auction(Property property, Player creator) {
        this.property = property;
        this.creator = creator;
        this.bet = 0;
    }

    public String makeBet(int newBet, Player player){
        if(player.cash<newBet){
            return "You have not got enough money!";
        }
        if(player == this.creator){
            return "You are creator of auction! You cant take part of it!";
        }

        if(newBet>this.bet){
            bet=newBet;
            winner = player;
            if(!participants.contains(player))
                participants.add(player);
            return StaticStrings.SUCCESS;
        }
        else
            return "Last bet is bigger then yours!";
    }

    public String goOut(Player player){
        if(winner != player && participants.contains(player)){
            participants.remove(player);
            return StaticStrings.SUCCESS;
        }else{
            return "You are the winner, you cant go out!";
        }
    }

}
