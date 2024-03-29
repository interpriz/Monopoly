package com.example.monopoly;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static entities.StaticMessages.*;
import static enums.OfferStates.newOffer;
import static enums.OfferTypes.buy;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import entities.Game;
import entities.Player;
import entities.Property;
import services.GameService;
import services.MapService;

@RunWith(AndroidJUnit4.class)
public class DepositTest {
    private Game game;
    private final MapService mapService;
    private GameService gameService;

    private Player player;
    private Property property;

    public DepositTest() {
        mapService = MapService.getInstance();
    }

    @Before
    public void setUp() throws Exception {
        game = new Game(4, "God");
        gameService = new GameService(game);
        gameService.enterGame("God");
        for (int i = 1; i < game.maxPLayers; i++) {
            gameService.enterGame("player_" + i);
        }
        player = game.players.get(0);
        property = mapService.getPropertyByPosition(1);
        game.fieldsOwners.get(1).owner = 0;
        game.fieldsOwners.get(3).owner = 0;
    }

    @After
    public void after() throws Exception {
        gameService.setDeposit(property,false);
    }

    @Test
    public void test() {


        boolean result = gameService.getDeposit(property);

        assertEquals(false, result);
    }

    //-----------createDeposit---------------------
    @Test
    public void createDeposit_notAnOwner() {

        String result = gameService.createDeposit(property, game.players.get(1));

        assertEquals(NOT_AN_OWNER, result);
        assertFalse(gameService.getDeposit(property));
    }

    @Test
    public void destroyDeposit_alreadyDeposit() {

        gameService.setDeposit(property,true);
        String result = gameService.createDeposit(property, player);

        assertEquals(ALREADY_DEPOSIT, result);
        assertTrue(gameService.getDeposit(property));
    }

    @Test
    public void createDeposit_StreetWithHouses() {
        game.fieldsOwners.get(1).houses = 0;
        game.fieldsOwners.get(1).houses = 1;

        String result = gameService.createDeposit(property, player);

        assertEquals(CANT_DEPOSIT_STREET_WITH_HOUSES, result);
        assertFalse(gameService.getDeposit(property));
    }

    @Test
    public void createDeposit_Success() {

        String result = gameService.createDeposit(property, player);

        assertEquals(SUCCESS, result);
        assertTrue(gameService.getDeposit(property));
    }

    //------------destroyDeposit------------------

    @Test
    public void destroyDeposit_notAnOwner() {

        gameService.setDeposit(property,true);
        String result = gameService.destroyDeposit(property, game.players.get(1));

        assertEquals(NOT_AN_OWNER, result);
        assertTrue(gameService.getDeposit(property));
    }

    @Test
    public void destroyDeposit_noDeposit() {

        String result = gameService.destroyDeposit(property, player);

        assertEquals(NOT_DEPOSIT, result);
        assertFalse(gameService.getDeposit(property));
    }

    @Test
    public void destroyDeposit_noMoney() {

        gameService.setDeposit(property,true);
        player.cash = property.depositPrice-1;
        String result = gameService.destroyDeposit(property, player);

        assertEquals(NOT_ENOUGH_MONEY, result);
        assertTrue(gameService.getDeposit(property));
    }

    @Test
    public void destroyDeposit_Success() {

        gameService.setDeposit(property,true);
        String result = gameService.destroyDeposit(property, player);

        assertEquals(SUCCESS, result);
        assertEquals(1500- property.redemptionPrice, player.cash);
        assertFalse(gameService.getDeposit(property));
    }

}
