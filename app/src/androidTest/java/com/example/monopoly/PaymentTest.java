package com.example.monopoly;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import entities.Game;
import entities.Player;
import static entities.StaticMessages.*;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import rules.GameInitialiseRule;
import services.GameService;
import services.MapService;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class PaymentTest {

    private Player sender;
    private Player recipient;

    private final MapService mapService= MapService.getInstance();;
    private GameService gameService;

    @Rule
    public final GameInitialiseRule gameInitialiseRule =
            new GameInitialiseRule("testGame1");

    @Before
    public void setUp() throws Exception {
        gameService = new GameService("testGame1");
        sender = gameService.getGame().players.get(0);
        recipient = gameService.getGame().players.get(1);
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
        assertEquals(sender,gameService.getPlayer(sender.getLastDebt().debtorID));
        assertEquals(recipient,gameService.getPlayer(sender.getLastDebt().recipientID));
    }






}