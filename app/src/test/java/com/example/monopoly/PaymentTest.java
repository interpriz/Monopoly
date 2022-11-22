package com.example.monopoly;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import entities.Game;
import entities.Player;
import static entities.StaticStrings.*;

import services.GameService;
import services.MapService;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class PaymentTest {

    private Player sender;
    private Player recipient;

    private Game game;
    private final MapService mapService;
    private GameService gameService;

    public PaymentTest() {
        mapService = MapService.getInstance();
    }
    @Before
    public void setUp() throws Exception {
        game = new Game(4,"God");
        gameService = new GameService(game);
        gameService.enterGame("God");
        for(int i=1; i<game.maxPLayers;i++){
            gameService.enterGame("player_"+i);
        }
        sender = game.players.get(0);
        recipient = game.players.get(1);
    }

    /*@Before
    public void setUp() throws Exception {
        sender = new Player(100);
        recipient = new Player(100);
    }*/

    @Test
    public void payment_EnoughMoney(){
        String result = gameService.payment(sender,recipient,10);


        assertEquals(SUCCESS,result);
        assertEquals(1490,sender.cash);
        assertEquals(1510,recipient.cash);
        assertTrue(sender.debts.isEmpty());
    }

    @Test
    public void payment_negativeSum(){
        String result = gameService.payment(sender,recipient,-10);

        assertEquals(NEGATIVE_SUM,result);
        assertTrue(sender.debts.isEmpty());
    }

    @Test
    public void payment_NotEnoughMoney(){
        String result = gameService.payment(sender,recipient,1600);

        assertEquals(NOT_ENOUGH_MONEY,result);
        assertEquals(1500,sender.cash);
        assertEquals(1500,recipient.cash);
        assertEquals(1600,sender.getLastDebt().sum);
        assertEquals(sender,sender.getLastDebt().debtor);
        assertEquals(recipient,sender.getLastDebt().recipient);
    }






}