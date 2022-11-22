package com.example.monopoly;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static entities.StaticStrings.SUCCESS;

import org.junit.Test;

import entities.Game;
import services.GameService;
import services.MapService;

public class GameTest {

    private Game game = new Game(4,"God");
    private final MapService mapService;

    public GameTest() {
        for(int i=0; i<4;i++){
            //gameService.enterGame();
        }
        mapService = MapService.getInstance();
    }
    /*@Before
    public void setUp() throws Exception {
        sender = new Player(100);
        recipient = new Player(100);
    }*/

    @Test
    public void newTest(){

    }
}
