package com.example.monopoly;

import static org.junit.Assert.assertEquals;
import static entities.StaticMessages.NOT_AN_OWNER;
import static entities.StaticMessages.SUCCESS;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import entities.Debt;
import entities.Game;
import entities.Player;
import entities.Street;
import rules.GameInitialiseRule;
import services.GameService;
import services.MapService;

@RunWith(AndroidJUnit4.class)
public class DebtTest {
    private final MapService mapService =MapService.getInstance();
    private GameService gameService;

    private Player player;

    @Rule
    public final GameInitialiseRule gameInitialiseRule =
            new GameInitialiseRule("testGame1");


    @Before
    public void setUp() throws Exception {
        gameService = new GameService("testGame1");
        player = gameService.getGame().players.get(0);
    }

    @Test
    public void repayDebt(){
        player.debts.add(new Debt(0,1,200));
        String result  = gameService.repayDebt(player, player.getLastDebt());

        assertEquals(SUCCESS,result);
        assertEquals(1300,player.cash);
        assertEquals(1700,gameService.getGame().players.get(1).cash);
    }
}
