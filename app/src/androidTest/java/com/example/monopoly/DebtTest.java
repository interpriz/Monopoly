package com.example.monopoly;

import static org.junit.Assert.assertEquals;
import static entities.StaticMessages.NOT_AN_OWNER;
import static entities.StaticMessages.SUCCESS;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import entities.Debt;
import entities.Game;
import entities.Player;
import entities.Street;
import services.GameService;
import services.MapService;

@RunWith(AndroidJUnit4.class)
public class DebtTest {

    private Game game;
    private final MapService mapService =MapService.getInstance();
    private GameService gameService;

    private Street gitnayaUl;
    private Player player;


    @Before
    public void setUp() throws Exception {
        game = new Game(4,"God");
        gameService = new GameService(game);
        gameService.enterGame("God");
        for(int i=1; i<game.maxPLayers;i++){
            gameService.enterGame("player_"+i);
        }
        gitnayaUl = (Street) mapService.getPropertyByPosition(1);
        player = game.players.get(0);
    }

    @Test
    public void repayDebt(){
        player.debts.add(new Debt(0,1,200));
        String result  = gameService.repayDebt(player, player.getLastDebt());

        assertEquals(SUCCESS,result);
        assertEquals(1300,player.cash);
        assertEquals(1700,game.players.get(1).cash);
    }
}
