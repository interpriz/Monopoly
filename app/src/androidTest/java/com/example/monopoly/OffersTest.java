package com.example.monopoly;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static entities.StaticMessages.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import entities.Game;
import entities.Offer;
import entities.Player;
import entities.Property;

import static entities.StaticMessages.SUCCESS;
import static enums.OfferStates.*;
import static enums.OfferTypes.*;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import enums.Participants;
import services.GameService;
import services.MapService;

@RunWith(AndroidJUnit4.class)
public class OffersTest {
    private Game game;
    private final MapService mapService;
    private GameService gameService;

    private Player sender;
    private Player recipient;

    private int senderID;
    private int recipientID;

    private Property senderProperty;
    private Property recipientProperty;

    public OffersTest() {
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
        sender = game.players.get(0);
        recipient = game.players.get(1);

        senderID = 0;
        recipientID = 1;

        senderProperty = mapService.getPropertyByPosition(1);
        game.fieldsOwners.get(1).owner = 0;
        game.fieldsOwners.get(3).owner = 0;

        recipientProperty = mapService.getPropertyByPosition(6);
        game.fieldsOwners.get(6).owner = 1;
        game.fieldsOwners.get(8).owner = 1;
        game.fieldsOwners.get(9).owner = 1;
    }

    //-----------------makeOffer-----------------------
    @Test
    public void makeOffer_buy_success() {

        String result = gameService.makeOffer(
                recipient,
                null,
                buy,
                100,
                recipientProperty,
                sender
        );

        assertEquals(SUCCESS, result);

        assertNull(recipient.getLastOffer().senderProperty());
        assertEquals(buy, recipient.getLastOffer().type);
        assertEquals(100, recipient.getLastOffer().sum);
        assertEquals(recipientProperty, recipient.getLastOffer().recipientProperty());
        assertEquals(senderID, recipient.getLastOffer().senderID);
        assertEquals(newOffer, recipient.getLastOffer().state);
    }

    @Test
    public void makeOffer_buy_NotAnOwner() {
        game.fieldsOwners.get(6).owner = -1;

        String result = gameService.makeOffer(
                recipient,
                null,
                buy,
                100,
                recipientProperty,
                sender
        );

        assertEquals(SOMEBODY_NOT_THE_OWNER, result);
        assertNull(recipient.getLastOffer());
    }

    @Test
    public void makeOffer_buy_noMoney() {

        String result = gameService.makeOffer(
                recipient,
                null,
                buy,
                1600,
                recipientProperty,
                sender
        );

        assertEquals(NOT_ENOUGH_MONEY, result);
        assertNull(recipient.getLastOffer());
    }

    @Test
    public void makeOffer_sold_Success() {

        String result = gameService.makeOffer(
                recipient,
                senderProperty,
                sold,
                100,
                null,
                sender
        );

        assertEquals(SUCCESS, result);

        assertEquals(senderProperty, recipient.getLastOffer().senderProperty());
        assertEquals(sold, recipient.getLastOffer().type);
        assertEquals(100, recipient.getLastOffer().sum);
        assertNull(recipient.getLastOffer().recipientProperty());
        assertEquals(senderID, recipient.getLastOffer().senderID);
        assertEquals(newOffer, recipient.getLastOffer().state);
    }

    @Test
    public void makeOffer_sold_NotAnOwner() {
        game.fieldsOwners.get(1).owner = -1;

        String result = gameService.makeOffer(
                recipient,
                senderProperty,
                sold,
                100,
                null,
                sender
        );

        assertEquals(SOMEBODY_NOT_THE_OWNER, result);
        assertNull(recipient.getLastOffer());
    }

    @Test
    public void makeOffer_sold_StreetWithHouses() {
        game.fieldsOwners.get(1).houses = 0;
        game.fieldsOwners.get(3).houses = 1;


        String result = gameService.makeOffer(
                recipient,
                senderProperty,
                sold,
                100,
                null,
                sender
        );

        assertEquals(CANT_SOLD_BUY_CHANGE_STREET, result);
        assertNull(recipient.getLastOffer());
    }

    @Test
    public void makeOffer_change_NotAnOwner() {
        game.fieldsOwners.get(1).owner = -1;

        String result = gameService.makeOffer(
                recipient,
                senderProperty,
                change,
                100,
                recipientProperty,
                sender
        );

        assertEquals(SOMEBODY_NOT_THE_OWNER, result);
        assertNull(recipient.getLastOffer());
    }

    @Test
    public void makeOffer_change_Success() {

        String result = gameService.makeOffer(
                recipient,
                senderProperty,
                change,
                100,
                recipientProperty,
                sender
        );

        assertEquals(SUCCESS, result);
        assertEquals(senderProperty, recipient.getLastOffer().senderProperty());
        assertEquals(change, recipient.getLastOffer().type);
        assertEquals(100, recipient.getLastOffer().sum);
        assertEquals(recipientProperty, recipient.getLastOffer().recipientProperty());
        assertEquals(senderID, recipient.getLastOffer().senderID);
        assertEquals(newOffer, recipient.getLastOffer().state);
    }

    @Test
    public void makeOffer_change_noMoney() {

        String result = gameService.makeOffer(
                recipient,
                senderProperty,
                change,
                -1600,
                recipientProperty,
                sender
        );

        assertEquals(NOT_ENOUGH_MONEY, result);
        assertNull(recipient.getLastOffer());
    }


    //-----------------rejectOffer-----------------------


    @Test
    public void rejectOffer_Success() {
        Offer offer = new Offer(
                senderID,
                recipientID,
                senderProperty,
                100,
                sold,
                newOffer,
                null
        );

        String result = gameService.rejectOffer(offer, recipient);

        assertEquals(SUCCESS, result);
        assertEquals(rejectOffer, offer.state);
    }

    //тклонить отклоненное или принятое предложение
    @Test
    public void rejectOffer_AcceptedRejected() {
        Offer offer = new Offer(
                senderID,
                recipientID,
                senderProperty,
                100,
                sold,
                rejectOffer,
                null
        );

        String result = gameService.rejectOffer(offer, recipient);

        assertEquals(CANT_REJECT_OFFER, result);

        offer.state = acceptOffer;
        result = gameService.rejectOffer(offer, recipient);

        assertEquals(CANT_REJECT_OFFER, result);
    }

    //-----------------acceptOffer-----------------------

    @Test
    public void acceptOffer_NotAnOwner() {
        Offer offer = new Offer(
                senderID,
               2,
                null,
                100,
                buy,
                newOffer,
                recipientProperty
        );
        recipient.offers.add(offer);

        String result = gameService.acceptOffer(offer, recipient);

        assertEquals(NOT_THE_OFFER_OWNER, result);
    }

    @Test
    public void acceptOffer_AcceptedRejected() {
        Offer offer = new Offer(
                senderID,
                recipientID,
                senderProperty,
                100,
                sold,
                rejectOffer,
                null
        );

        String result = gameService.acceptOffer(offer, recipient);

        assertEquals(CANT_ACCEPT_OFFER, result);

        offer.state = acceptOffer;
        result = gameService.acceptOffer(offer, recipient);

        assertEquals(CANT_ACCEPT_OFFER, result);
    }

    //-----------------acceptBuyOffer-----------------------
    @Test
    public void acceptOffer_buy_NotAnOwner() {
        game.fieldsOwners.get(6).owner = -1;
        Offer offer = new Offer(
                senderID,
                recipientID,
                null,
                100,
                buy,
                newOffer,
                recipientProperty
        );

        String result = gameService.acceptOffer(offer, recipient);

        assertEquals(SOMEBODY_NOT_THE_OWNER, result);
        assertEquals(-1, game.fieldsOwners.get(6).owner);
    }

    @Test
    public void acceptOffer_buy_StreetWithHouses() {
        game.fieldsOwners.get(6).houses = 0;
        game.fieldsOwners.get(8).houses = 1;
        game.fieldsOwners.get(9).houses = 1;
        Offer offer = new Offer(
                senderID,
                recipientID,
                null,
                100,
                buy,
                newOffer,
                recipientProperty
        );

        String result = gameService.acceptOffer(offer, recipient);

        assertEquals(CANT_SOLD_STREET_WITH_HOUSES, result);
        assertEquals(1, game.fieldsOwners.get(6).owner);
    }

    @Test
    public void acceptOffer_buy_noMoney() {
        Offer offer = new Offer(
                senderID,
                recipientID,
                null,
                1600,
                buy,
                newOffer,
                recipientProperty
        );

        String result = gameService.acceptOffer(offer, recipient);

        assertEquals(SENDER_NOT_ENOUGH_MONEY, result);
        assertEquals(1, game.fieldsOwners.get(6).owner);
    }

    @Test
    public void acceptOffer_buy_success(){
        Offer offer = new Offer(
                senderID,
                recipientID,
                null,
                100,
                buy,
                newOffer,
                recipientProperty
        );

        String result = gameService.acceptOffer(offer, recipient);

        assertEquals(SUCCESS, result);
        assertEquals(0, game.fieldsOwners.get(6).owner);
        assertEquals(1600, recipient.cash);
        assertEquals(1400, sender.cash);
    }

    //-----------------acceptSoldOffer-----------------------
    @Test
    public void acceptOffer_sold_NotAnOwner() {
        game.fieldsOwners.get(1).owner = -1;
        Offer offer = new Offer(
                senderID,
                recipientID,
                senderProperty,
                100,
                sold,
                newOffer,
                null
        );

        String result = gameService.acceptOffer(offer, recipient);

        assertEquals(SOMEBODY_NOT_THE_OWNER, result);
        assertEquals(-1, game.fieldsOwners.get(1).owner);
    }

    @Test
    public void acceptOffer_sold_StreetWithHouses() {
        game.fieldsOwners.get(1).houses = 0;
        game.fieldsOwners.get(3).houses = 1;
        Offer offer = new Offer(
                senderID,
                recipientID,
                senderProperty,
                100,
                sold,
                newOffer,
                null
        );

        String result = gameService.acceptOffer(offer, recipient);

        assertEquals(CANT_BUY_STREET_WITH_HOUSES, result);
        assertEquals(0, game.fieldsOwners.get(1).owner);
    }

    @Test
    public void acceptOffer_sold_noMoney() {
        Offer offer = new Offer(
                senderID,
                recipientID,
                senderProperty,
                1600,
                sold,
                newOffer,
                null
        );

        String result = gameService.acceptOffer(offer, recipient);

        assertEquals(NOT_ENOUGH_MONEY, result);
        assertEquals(0, game.fieldsOwners.get(1).owner);
    }

    @Test
    public void acceptOffer_sold_success(){
        Offer offer = new Offer(
                senderID,
                recipientID,
                senderProperty,
                100,
                sold,
                newOffer,
                null
        );
        String result = gameService.acceptOffer(offer, recipient);

        assertEquals(SUCCESS, result);
        assertEquals(1, game.fieldsOwners.get(1).owner);
        assertEquals(1400, recipient.cash);
        assertEquals(1600, sender.cash);
    }

    //-----------------acceptChangeOffer-----------------------

    @Test
    public void acceptOffer_change_NotAnOwner() {
        game.fieldsOwners.get(1).owner = -1;
        //game.fieldsOwners.get(6).owner = -1;
        Offer offer = new Offer(
                senderID,
                recipientID,
                senderProperty,
                100,
                change,
                newOffer,
                recipientProperty
        );

        String result = gameService.acceptOffer(offer, recipient);

        assertEquals(SOMEBODY_NOT_THE_OWNER, result);
        assertEquals(-1, game.fieldsOwners.get(1).owner);
        assertEquals(1, game.fieldsOwners.get(6).owner);
    }

    @Test
    public void acceptOffer_change_StreetWithHouses() {
        //game.fieldsOwners.get(1).houses = 0;
        //game.fieldsOwners.get(3).houses = 1;

        game.fieldsOwners.get(6).houses = 0;
        game.fieldsOwners.get(8).houses = 1;
        game.fieldsOwners.get(9).houses = 1;
        Offer offer = new Offer(
                senderID,
                recipientID,
                senderProperty,
                100,
                change,
                newOffer,
                recipientProperty
        );

        String result = gameService.acceptOffer(offer, recipient);

        assertEquals(CANT_CHANGE_STREET_WITH_HOUSES, result);
        assertEquals(0, game.fieldsOwners.get(1).owner);
        assertEquals(1, game.fieldsOwners.get(6).owner);
    }

    @Test
    public void acceptOffer_change_noMoney() {
        Offer offer = new Offer(
                senderID,
                recipientID,
                senderProperty,
                1600,
                change,
                newOffer,
                recipientProperty
        );

        String result = gameService.acceptOffer(offer, recipient);

        assertEquals(NOT_ENOUGH_MONEY, result);
        assertEquals(0, game.fieldsOwners.get(1).owner);
        assertEquals(1, game.fieldsOwners.get(6).owner);

        offer.sum = -1600;
        result = gameService.acceptOffer(offer, recipient);

        assertEquals(SENDER_NOT_ENOUGH_MONEY, result);
        assertEquals(0, game.fieldsOwners.get(1).owner);
        assertEquals(1, game.fieldsOwners.get(6).owner);

    }

    @Test
    public void acceptOffer_change_successPosSum(){
        Offer offer = new Offer(
                senderID,
                recipientID,
                senderProperty,
                100,
                change,
                newOffer,
                recipientProperty
        );
        String result = gameService.acceptOffer(offer, recipient);

        assertEquals(SUCCESS, result);
        assertEquals(1, game.fieldsOwners.get(1).owner);
        assertEquals(0, game.fieldsOwners.get(6).owner);
        assertEquals(1400, recipient.cash);
        assertEquals(1600, sender.cash);
    }

    @Test
    public void acceptOffer_change_successNegSum(){
        Offer offer = new Offer(
                senderID,
                recipientID,
                senderProperty,
                -100,
                change,
                newOffer,
                recipientProperty
        );
        String result = gameService.acceptOffer(offer, recipient);

        assertEquals(SUCCESS, result);
        assertEquals(1, game.fieldsOwners.get(1).owner);
        assertEquals(0, game.fieldsOwners.get(6).owner);
        assertEquals(1600, recipient.cash);
        assertEquals(1400, sender.cash);
    }

    //-----------------getFullSumOffer-----------------------

    @Test
    public void getFullSumOffer_buy(){
        Offer offer = new Offer(
                senderID,
                recipientID,
                null,
                100,
                buy,
                newOffer,
                recipientProperty
        );
        int result = gameService.getFullOfferSum(offer,Participants.recipient);
        assertEquals(0, result);

        //если собственность в залоге
        recipientProperty.deposit=true;

        sender.repayment=true;
        result = gameService.getFullOfferSum(offer,Participants.sender);
        assertEquals(100+recipientProperty.redemptionPrice, result);

        sender.repayment=false;
        result = gameService.getFullOfferSum(offer,Participants.sender);
        assertEquals(100+recipientProperty.tenPercent, result);

        //если собственность не в залоге
        recipientProperty.deposit=false;

        sender.repayment=true;
        result = gameService.getFullOfferSum(offer,Participants.sender);
        assertEquals(100, result);

        sender.repayment=false;
        result = gameService.getFullOfferSum(offer,Participants.sender);
        assertEquals(100, result);

    }

    @Test
    public void getFullSumOffer_sold(){
        Offer offer = new Offer(
                senderID,
                recipientID,
                senderProperty,
                100,
                sold,
                newOffer,
                null
        );
        int result = gameService.getFullOfferSum(offer,Participants.sender);
        assertEquals(0, result);

        //если собственность в залоге
        senderProperty.deposit=true;

        recipient.repayment=true;
        result = gameService.getFullOfferSum(offer,Participants.recipient);
        assertEquals(100+senderProperty.redemptionPrice, result);

        recipient.repayment=false;
        result = gameService.getFullOfferSum(offer,Participants.recipient);
        assertEquals(100+senderProperty.tenPercent, result);

        //если собственность не в залоге
        senderProperty.deposit=false;

        recipient.repayment=true;
        result = gameService.getFullOfferSum(offer,Participants.recipient);
        assertEquals(100, result);

        recipient.repayment=false;
        result = gameService.getFullOfferSum(offer,Participants.recipient);
        assertEquals(100, result);

    }

    @Test
    public void getFullSumOffer_change(){
        Offer offer = new Offer(
                senderID,
                recipientID,
                senderProperty,
                -100,
                change,
                newOffer,
                recipientProperty
        );
        //---------------- для отправителя----------------------
        //если собственность в залоге
        recipientProperty.deposit=true;

        sender.repayment=true;
        int result = gameService.getFullOfferSum(offer,Participants.sender);
        assertEquals(100+recipientProperty.redemptionPrice, result);

        sender.repayment=false;
        result = gameService.getFullOfferSum(offer,Participants.sender);
        assertEquals(100+recipientProperty.tenPercent, result);

        //если собственность не в залоге
        recipientProperty.deposit=false;

        sender.repayment=true;
        result = gameService.getFullOfferSum(offer,Participants.sender);
        assertEquals(100, result);

        sender.repayment=false;
        result = gameService.getFullOfferSum(offer,Participants.sender);
        assertEquals(100, result);

        //---------------- для получателя--------------------
        //если собственность в залоге
        senderProperty.deposit=true;

        recipient.repayment=true;
        result = gameService.getFullOfferSum(offer,Participants.recipient);
        assertEquals(senderProperty.redemptionPrice, result);

        recipient.repayment=false;
        result = gameService.getFullOfferSum(offer,Participants.recipient);
        assertEquals(senderProperty.tenPercent, result);

        //если собственность не в залоге
        senderProperty.deposit=false;

        recipient.repayment=true;
        result = gameService.getFullOfferSum(offer,Participants.recipient);
        assertEquals(0, result);

        recipient.repayment=false;
        result = gameService.getFullOfferSum(offer,Participants.recipient);
        assertEquals(0, result);

    }


}
