package com.example.monopoly;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static entities.StaticStrings.*;

import org.junit.Before;
import org.junit.Test;

import entities.Game;
import entities.Offer;
import entities.Player;
import entities.Property;
import entities.Street;

import static entities.StaticStrings.SUCCESS;
import static enums.OfferStates.*;
import static enums.OfferTypes.*;
import services.GameService;
import services.MapService;

public class OffersTest {
    private Game game;
    private final MapService mapService;
    private GameService gameService;

    private Player sender;
    private Player recipient;
    private Property senderProperty;
    private Property recipientProperty;

    public OffersTest() {
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

    @Test
    public void makeOffer_buy_success(){
        recipientProperty = mapService.getPropertyByPosition(1);
        game.fieldsOwners.get(1).owner=1;

        String result  = gameService.makeOffer(
                recipient,
                null,
                buy,
                100,
                recipientProperty,
                sender
        );

        assertEquals(SUCCESS,result);

        assertNull(recipient.getLastOffer().senderProperty);
        assertEquals(buy,recipient.getLastOffer().type);
        assertEquals(100,recipient.getLastOffer().sum);
        assertEquals(recipientProperty,recipient.getLastOffer().recipientProperty);
        assertEquals(sender,recipient.getLastOffer().sender);
        assertEquals(newOffer,recipient.getLastOffer().state);
    }

    @Test
    public void makeOffer_buy_NotAnOwner(){
        recipientProperty = mapService.getPropertyByPosition(1);

        String result  = gameService.makeOffer(
                recipient,
                null,
                buy,
                100,
                recipientProperty,
                sender
        );

        assertEquals(SOMEBODY_NOT_THE_OWNER,result);
        assertNull(recipient.getLastOffer());
    }

    @Test
    public void makeOffer_sold_Success(){
        senderProperty = mapService.getPropertyByPosition(1);
        game.fieldsOwners.get(1).owner=0;

        String result  = gameService.makeOffer(
                recipient,
                senderProperty,
                sold,
                100,
                null,
                sender
        );

        assertEquals(SUCCESS,result);

        assertEquals(senderProperty,recipient.getLastOffer().senderProperty);
        assertEquals(sold,recipient.getLastOffer().type);
        assertEquals(100,recipient.getLastOffer().sum);
        assertNull(recipient.getLastOffer().recipientProperty);
        assertEquals(sender,recipient.getLastOffer().sender);
        assertEquals(newOffer,recipient.getLastOffer().state);
    }

    @Test
    public void makeOffer_sold_NotAnOwner(){
        senderProperty = mapService.getPropertyByPosition(1);

        String result  = gameService.makeOffer(
                recipient,
                senderProperty,
                sold,
                100,
                null,
                sender
        );

        assertEquals(SOMEBODY_NOT_THE_OWNER,result);
        assertNull(recipient.getLastOffer());
    }

    @Test
    public void makeOffer_sold_StreetWithHouses(){
        senderProperty = mapService.getPropertyByPosition(1);
        game.fieldsOwners.get(1).owner=0;
        game.fieldsOwners.get(3).owner=0;
        game.fieldsOwners.get(1).houses=0;
        game.fieldsOwners.get(3).houses=1;


        String result  = gameService.makeOffer(
                recipient,
                senderProperty,
                sold,
                100,
                null,
                sender
        );

        assertEquals(CANT_SOLD_BUY_CHANGE_STREET,result);
        assertNull(recipient.getLastOffer());
    }

    @Test
    public void rejectOffer_Success(){
        senderProperty = mapService.getPropertyByPosition(1);
        Offer offer = new Offer(
                sender,
                recipient,
                senderProperty,
                100,
                sold,
                newOffer,
                null
        );
        recipient.offers.add(offer);

        String result  = gameService.rejectOffer(offer, recipient);

        assertEquals(SUCCESS,result);
        assertEquals(rejectOffer,recipient.getLastOffer().state);
    }

    //тклонить отклоненное или принятое предложение
    @Test
    public void rejectOffer_AcceptedRejected(){
        senderProperty = mapService.getPropertyByPosition(1);
        Offer offer = new Offer(
                sender,
                recipient,
                senderProperty,
                100,
                sold,
                rejectOffer,
                null
        );
        recipient.offers.add(offer);

        String result  = gameService.rejectOffer(offer, recipient);

        assertEquals(CANT_REJECT_OFFER,result);
        assertEquals(rejectOffer,recipient.getLastOffer().state);

        recipient.getLastOffer().state = acceptOffer;
        result  = gameService.rejectOffer(offer, recipient);

        assertEquals(CANT_REJECT_OFFER,result);
        assertEquals(acceptOffer,recipient.getLastOffer().state);
    }

    @Test
    public void acceptOffer_buy_NotAnOwner(){
        /*recipientProperty = mapService.getPropertyByPosition(1);
        game.fieldsOwners.get(1).owner=1;

        String result  = gameService.makeOffer(
                recipient,
                null,
                buy,
                100,
                recipientProperty,
                sender
        );

        assertEquals(SUCCESS,result);*/
    }



}
