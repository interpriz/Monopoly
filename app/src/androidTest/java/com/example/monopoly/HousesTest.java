package com.example.monopoly;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static entities.StaticMessages.*;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import entities.Game;
import entities.Player;
import entities.Street;
import services.GameService;
import services.MapService;

@RunWith(AndroidJUnit4.class)
public class HousesTest {

    private Game game;
    private final MapService mapService;
    private GameService gameService;

    private Street gitnayaUl;
    private Player player;

    public HousesTest() {
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
        gitnayaUl = (Street) mapService.getPropertyByPosition(1);
        player = game.players.get(0);
    }

    //игрок - не владелец улицы
    @Test
    public void house_buy_NotAnOwner(){

        String result  = gameService.buyHouse(gitnayaUl, player);

        assertEquals(NOT_AN_OWNER,result);
        assertEquals(0,gameService.getHouses(gitnayaUl));
    }

    //у игрока нет полной цветовой группы
    @Test
    public void house_buy_NotAWholeGroup(){
        game.fieldsOwners.get(1).owner=0;

        String result  = gameService.buyHouse(gitnayaUl, player);

        assertEquals(NOT_A_FULL_GROUP,result);
        assertEquals(0,gameService.getHouses(gitnayaUl));
    }

    // успешная покупка дома
    @Test
    public void house_buy_Success(){
        game.fieldsOwners.get(1).owner=0;
        game.fieldsOwners.get(3).owner=0;

        String result  = gameService.buyHouse(gitnayaUl, player);

        assertEquals(SUCCESS,result);
        assertEquals(1,gameService.getHouses(gitnayaUl));
    }

    // игрока нет денег
    @Test
    public void house_buy_NoMoney(){
        game.fieldsOwners.get(1).owner=0;
        game.fieldsOwners.get(3).owner=0;
        player.cash= gitnayaUl.house_price-1;

        String result  = gameService.buyHouse(gitnayaUl, player);

        assertEquals(NOT_ENOUGH_MONEY,result);
        assertEquals(0,gameService.getHouses(gitnayaUl));
    }

    //равномерное расппределение домов
    @Test
    public void house_buy_NotEqualHouses(){
        game.fieldsOwners.get(1).owner=0;
        game.fieldsOwners.get(3).owner=0;
        game.fieldsOwners.get(1).houses = 2;
        game.fieldsOwners.get(3).houses = 1;

        String result  = gameService.buyHouse(gitnayaUl, player);

        assertEquals(NOT_AN_EQUAL_NUMBER_OF_HOUSES,result);
        assertEquals(2,gameService.getHouses(gitnayaUl));
    }

    //покупка сверх отеля
    @Test
    public void house_buy_6House(){
        game.fieldsOwners.get(1).owner=0;
        game.fieldsOwners.get(3).owner=0;
        game.fieldsOwners.get(1).houses = 5;
        game.fieldsOwners.get(3).houses = 5;

        String result  = gameService.buyHouse(gitnayaUl, player);

        assertEquals(HOTEL_ALREADY,result);
        assertEquals(5,gameService.getHouses(gitnayaUl));
    }

    //продажа дома, которого нет
    @Test
    public void house_sold_NoHouse(){
        game.fieldsOwners.get(1).owner=0;
        game.fieldsOwners.get(3).owner=0;

        String result  = gameService.soldHouse(gitnayaUl, player);

        assertEquals(NO_HOUSES,result);
        assertEquals(0,gameService.getHouses(gitnayaUl));
    }

    //равномерное распределение домов
    @Test
    public void house_sold_NotEqualHouses(){
        game.fieldsOwners.get(1).owner=0;
        game.fieldsOwners.get(3).owner=0;
        game.fieldsOwners.get(1).houses = 1;
        game.fieldsOwners.get(3).houses = 2;

        String result  = gameService.soldHouse(gitnayaUl, player);

        assertEquals(NOT_AN_EQUAL_NUMBER_OF_HOUSES,result);
        assertEquals(1,gameService.getHouses(gitnayaUl));
    }

    // успешная продажа
    @Test
    public void house_sold_Success(){
        game.fieldsOwners.get(1).owner=0;
        game.fieldsOwners.get(3).owner=0;
        game.fieldsOwners.get(1).houses = 1;
        game.fieldsOwners.get(3).houses = 1;

        String result  = gameService.soldHouse(gitnayaUl, player);

        assertEquals(SUCCESS,result);
        assertEquals(0,gameService.getHouses(gitnayaUl));
    }

    //продажа дома с не своей улицы
    @Test
    public void house_sold_NotAnOwner(){

        String result  = gameService.soldHouse(gitnayaUl, player);

        assertEquals(NOT_AN_OWNER,result);
        assertEquals(0,gameService.getHouses(gitnayaUl));
    }

}
