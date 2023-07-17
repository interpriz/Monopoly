package com.example.monopoly;

import org.junit.Before;

import entities.Game;
import entities.Player;
import entities.Property;
import services.GameService;
import services.MapService;

public class AuctionTest {

    private Game game;
    private final MapService mapService;
    private GameService gameService;

    private Player player;
    private Property property;

    public AuctionTest() {
        mapService = MapService.getInstance();
    }

    @Before
    public void setUp() throws Exception {
        game = new Game(4, "God");
        gameService = new GameService("testGame1");
        gameService.enterGame("God");
        for (int i = 1; i < game.maxPLayers; i++) {
            gameService.enterGame("player_" + i);
        }
        player = game.players.get(0);

        property = mapService.getPropertyByPosition(1);
        game.fieldsOwners.get(1).owner = 0;
        game.fieldsOwners.get(3).owner = 0;
    }


}
