package com.example.monopoly;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static entities.StaticMessages.*;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import entities.Game;
import entities.Player;
import enums.GameStates;
import services.GameService;
import services.MapService;

@RunWith(AndroidJUnit4.class)
public class GameTest {

    private Game game;
    private final MapService mapService;
    private GameService gameService;

    private Player organizer;
    private Player player_1;
    private Player player_2;
    private Player player_3;


    public GameTest() {
        mapService = MapService.getInstance();
    }

    @Before
    public void setUp() throws Exception {
        game = new Game(4, "God");
        gameService = new GameService(game);
        gameService.setTest(true);
        organizer = gameService.enterGame("God");
        for (int i = 1; i < game.maxPLayers; i++){
            gameService.enterGame("player_" + i);
        }
        player_1 = game.players.get(1);
        player_2 = game.players.get(2);
        player_3 = game.players.get(3);

    }

    @Test
    public void enterFullGame(){
        Player newPLayer = gameService.enterGame("New_Player");
        assertNull(newPLayer);
    }

    @Test
    public void startGame(){
        String result = gameService.startGame(player_1);
        assertEquals(NOT_ORGANIZER,result);

        result = gameService.startGame(organizer);
        assertEquals(SUCCESS,result);
        assertEquals(GameStates.onPlay,game.state);
        boolean flag1 = game.players.indexOf(organizer)==0;
        boolean flag2 = game.players.indexOf(player_1)==1;
        boolean flag3 = game.players.indexOf(player_2)==2;
        boolean flag4 = game.players.indexOf(player_3)==3;
        assertFalse(flag1 && flag2 && flag3 && flag4);


        result = gameService.startGame(organizer);
        assertEquals(GAME_IS_STARTED,result);
    }

    @Test
    public void firstPlayerRoll(){
        gameService.startGame(organizer);

        //сходил на житную
        gameService.setD1D2(0,1);
        String result = gameService.makeMove(player_1);
        assertEquals(BUY_OR_AUCTION,result);

        //проверка текущего игрока
        Player currentPlayer = gameService.getCurrentPlayer();
        assertEquals(game.players.get(0),currentPlayer);

        //купил житную
        gameService.acceptOffer(currentPlayer.getLastOffer(), currentPlayer);
        assertEquals(game.fieldsOwners.get(1).owner = 0,
                game.players.indexOf(currentPlayer));

        // не может ходить повторно, т.к. не дубль
        result = gameService.makeMove(player_1);
        assertEquals(ALREADY_ROLL,result);

        //передает кубики следующему
        result = gameService.endMotion();
        assertEquals(SUCCESS,result);
        assertEquals(1,game.currentPlayerId);
        assertFalse(currentPlayer.canRollDice);
    }

    public void Roll1(){
        //сходил на житную
        gameService.setD1D2(0,1);
        gameService.makeMove(player_1);
        //купил житную
        gameService.acceptOffer(gameService
                        .getCurrentPlayer()
                        .getLastOffer(),
                gameService.getCurrentPlayer());
        //передает кубики следующему
        gameService.endMotion();
    }

    public void Roll2(){
        //сходил на варшавку
        gameService.setD1D2(3,3);
        gameService.makeMove(player_2);

        // купил варшавку
        gameService.acceptOffer( gameService.getCurrentPlayer().getLastOffer(),
                gameService.getCurrentPlayer());

        //передает кубики следующему (себе)
        gameService.endMotion();
    }

    public void Roll3(){
        //сходил на огарева
        gameService.setD1D2(1,1);
        gameService.makeMove(player_2);

        // купил огарева
        gameService.acceptOffer( gameService.getCurrentPlayer().getLastOffer(),
                gameService.getCurrentPlayer());

        //передает кубики следующему (себе)
        gameService.endMotion();
    }

    public void Roll4(){
        //сходил на первую парковую
        gameService.setD1D2(0,1);
        gameService.makeMove(player_2);

        // купил первую парковую
        gameService.acceptOffer( gameService.getCurrentPlayer().getLastOffer(),
                gameService.getCurrentPlayer());

        //передает кубики следующему
        gameService.endMotion();
    }

    public void Roll5(){

    }

    public void Roll6(){

    }

    @Test
    public void secondPlayerRolls(){
        //старт игры
        gameService.startGame(organizer);
        Roll1();

        //-----тест 2ого хода-----------------
        //сходил на варшавку
        gameService.setD1D2(3,3);
        String result = gameService.makeMove(player_2);
        assertEquals(BUY_OR_AUCTION,result);

        //проверка текущего игрока
        Player currentPlayer = gameService.getCurrentPlayer();
        assertEquals(game.players.get(1),currentPlayer);

        // купил варшавку
        gameService.acceptOffer(currentPlayer.getLastOffer(), currentPlayer);
        assertEquals(game.fieldsOwners.get(6).owner = 1,
                game.players.indexOf(currentPlayer));

        //передает кубики следующему (себе)
        result = gameService.endMotion();
        assertEquals(SUCCESS,result);
        assertEquals(1,game.currentPlayerId);
        assertTrue(currentPlayer.canRollDice);

        //----------тест 3его хода(дубль)------------
        //сходил на огарева
        gameService.setD1D2(1,1);
        result = gameService.makeMove(player_2);
        assertEquals(BUY_OR_AUCTION,result);

        //проверка текущего игрока
        assertEquals(game.players.get(1),currentPlayer);

        // купил огарева
        gameService.acceptOffer(currentPlayer.getLastOffer(), currentPlayer);
        assertEquals(game.fieldsOwners.get(8).owner = 1,
                game.players.indexOf(currentPlayer));

        //передает кубики следующему (себе)
        result = gameService.endMotion();
        assertEquals(SUCCESS,result);
        assertEquals(1,game.currentPlayerId);
        assertTrue(currentPlayer.canRollDice);

        //----------тест 4ого хода(дубль)------------
        //сходил на первую парковую
        gameService.setD1D2(0,1);
        result = gameService.makeMove(player_2);
        assertEquals(BUY_OR_AUCTION,result);

        //проверка текущего игрока
        assertEquals(game.players.get(1),currentPlayer);

        // купил первую парковую
        gameService.acceptOffer(currentPlayer.getLastOffer(), currentPlayer);
        assertEquals(game.fieldsOwners.get(9).owner = 1,
                game.players.indexOf(currentPlayer));

        //передает кубики следующему
        result = gameService.endMotion();
        assertEquals(SUCCESS,result);
        assertEquals(2,game.currentPlayerId);
        assertFalse(currentPlayer.canRollDice);
    }

    @Test
    public void thirdPlayerRolls(){
        //старт игры
        gameService.startGame(organizer);
        //---1ый ход-------------------
        Roll1();
        //-----2ой ход-----------------
        Roll2();
        //----------тест 3ий ход(дубль)------------
        Roll3();
        //----------4оый ход(дубль)------------
        Roll4();
        //----------- тест 5ого хода --------------
        //проверка текущего игрока
        assertEquals(game.players.get(2),gameService.getCurrentPlayer());

        //сходил на общественную казну (дубль)
        gameService.setD1D2(1,1);
        String result = gameService.makeMove(player_3);
        assertEquals(SUCCESS,result);
        assertTrue(game.players.get(2).cash!=1500);

        int playerNewCash = game.players.get(2).cash;

        //передает кубики следующему (себе)
        gameService.endMotion();

        //----------- тест 6ого хода --------------
        //проверка текущего игрока
        assertEquals(game.players.get(2),gameService.getCurrentPlayer());

        //сходил на подоходный налог (дубль)
        gameService.setD1D2(1,1);
        result = gameService.makeMove(player_3);
        assertEquals(SUCCESS,result);
        assertEquals(playerNewCash-200, game.players.get(2).cash);
        playerNewCash = game.players.get(2).cash;


        //передает кубики следующему (себе)
        gameService.endMotion();

        //----------- тест 7ого хода --------------
        //проверка текущего игрока
        assertEquals(game.players.get(2),gameService.getCurrentPlayer());
        //сходил на варшавку (3ий дубль - тюрьма)
        gameService.setD1D2(1,1);
        result = gameService.makeMove(player_3);
        assertEquals(SUCCESS,result);
        assertEquals(10, gameService.getCurrentPlayer().position);

        //передает кубики следующему
        gameService.endMotion();

        //проверка текущего игрока
        assertEquals(game.players.get(3),gameService.getCurrentPlayer());
    }

}
