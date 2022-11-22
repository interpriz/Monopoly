package services;

import java.util.Collections;
import java.util.Random;

import static entities.StaticStrings.*;

import androidx.annotation.NonNull;

import entities.Auction;
import entities.Debt;
import entities.Game;
import entities.Offer;
import enums.GameStates;
import enums.OfferStates;
import enums.OfferTypes;
import enums.Participants;
import entities.Player;
import enums.PropTypes;
import entities.Property;
import entities.Street;
import repositories.GameRepository;
import repositories.PlayerRepository;

public class GameService {

    private final Game game;

    private final MapService mapServ;

    private final PlayerRepository playerRepo;
    private final GameRepository gameRepo;

    public GameService(Game game) {
        this.game = game;
        this.gameRepo = new GameRepository(game);
        this.mapServ = MapService.getInstance();
        this.playerRepo = new PlayerRepository(game);
    }

    //бросок кубиков
    public String diceRoll(/*Player player*/) {
        if (game.state != GameStates.onPlay)
            return GAME_NOT_ON_PLAY;
        /*if (!game.isCurrentPLayer(player)) {
            String message = "You are not a current player!";
            return;
        }*/
        Player player = getCurrentPlayer();
        if (player.canRollDice) {
            gameRepo.setDice1(new Random().nextInt(6) + 1);
            gameRepo.setDice2(new Random().nextInt(6) + 1);
            playerRepo.setCanRollDice(player, false);
        } else {
            return "You have already roll dices!";
        }
        return SUCCESS;
    }

    //осуществление хода
    public String makeMove(/*Player playerI*/) {
        if (game.state != GameStates.onPlay)
            return GAME_NOT_ON_PLAY;
        /*if (!game.isCurrentPLayer(player)) {
            String message = "You are not a current player!";
            return;
        }*/
        Player player = getCurrentPlayer();

        if (player.canRollDice) {
            return "You did not roll dices!";
        }
        int bankrupts = 0;//количество банкротов в игре
        Player winner = null;//возможный победитель

        // подсчет числа банкротов
        for (Player playerI : game.players) {
            //если игрок банкрот
            if (playerI.bankrupt) {
                bankrupts++;
            } else winner = playerI;
        }

        //если не все кроме одного банкроты
        if (bankrupts != game.players.size() - 1) {
            //если текущий игрок банкрот, то передать кубики
            if (player.bankrupt) {
                giveDicesToNextPlayer();
            } else {//иначе произвести ход
                //если в процессе ходя игрок не смог заплатить и он не банкрот,
                //то ему записывается долг, который надо погасить, после чего закончить ход endMotion.
                //если игрок устроил аукцион, то он должен завершить ход после окончания аукциона
                //TODO проверить возвращаемое значение на наличие ошибки оплаты
                String result = move(game.dice1, game.dice2, player);
                assert result != null;
                switch (result) {
                    case SUCCESS:
                        endMotion(player);
                        return SUCCESS;

                    case NOT_ENOUGH_MONEY:
                        //проверка на наличие собственности
                        int cnt = (int) mapServ.getProperties().stream().filter(x -> getOwner(x) == player).count();

                        //если у игрока недостаточно денег и нет собственности, то он - банкрот
                        if (cnt == 0) {
                            playerRepo.setBankrupt(player, true);
                            Player recipient = player.getLastOffer()
                                    .recipient;
                            playerRepo.addCach(recipient, player.cash);
                            playerRepo.setCash(player, 0);
                            playerRepo.removeDebt(player, player.getLastDebt());

                            return "You are a bankrupt!";
                        } else
                            return "You have no money, but have property! Sold it!";
                    case AUCTION:
//TODO продумать функционал аукциона (
// можно в этот момент сделать предложение игроку от банка
// на покупку у него собственности за ее стоимость,
// и если он ее не покупает, то запускается аукцион, в котором он учавствовать не может)
                        return BUY_OR_AUCTION;
                    default:
                        return result;
                }
            }
        } else
        //иначе единственный не банкрот - победитель
        {
            gameRepo.setWinner(game.players.indexOf(winner));
            gameRepo.setState(GameStates.onEnd);
            //pay_winner(winner);
        }

        return SUCCESS;

    }

    //движение на выпавшее число на костях
    private String move(int dice1, int dice2, Player player) {
        //логика попадания в тюрьму за 3 дубля подряд
        if (dice1 == dice2) {
            playerRepo.increaseDoubles(player);
            //логика выхода из тюрьмы при выпадении дубля
            if (player.jailMove > 0) {
                playerRepo.setJailMove(player, 0);
            }
            //отправляйтесь в тюрьму!
            if (player.doubles == 3) {
                playerRepo.setPosition(player, 10);
                playerRepo.setJailMove(player, 4);// 4, т.к. ниже по коду произойдет -1
                playerRepo.setDoubles(player, 0);
            }

        } else
            // прерывание серии дублей выпадением не дубля
            playerRepo.setDoubles(player, 0);

        // если игрок не в тюрьме
        if (player.jailMove == 0) {

            //если игрок прошел через поле вперед, то выплатить ему 200
            if (dice1 + dice2 + player.position > 40) payment(game.bank, player, 200);

            //подсчет текущей позиции игрока как старая позиция + число на кубиках % 40
            int newPosition = (dice1 + dice2 + player.position) % 40;
            playerRepo.setPosition(player, newPosition);
            switch (mapServ.getFieldByPosition(newPosition).getType()) {
                case forward:
                    //поле вперед
                    return payment(game.bank, player, 200);
                case publicTreasury:
                    //общественная казна
                    return (new Random().nextInt(2) == 0) ?
                            payment(game.bank, player, new Random().nextInt(200) + 1) :
                            payment(player, game.bank, new Random().nextInt(200) + 1);
                case chance:
                    //шанс
                    return (new Random().nextInt(2) == 0) ?
                            payment(game.bank, player, new Random().nextInt(200) + 1) :
                            payment(player, game.bank, new Random().nextInt(200) + 1);
                case incomeTax:
                    //подоходный налог
                    return payment(player, game.bank, 200);
                case superTax:
                    //сверхналог
                    return payment(player, game.bank, 100);
                case inPrison:
                case parking:
                    //посещение тюрьмы и стоянка
                    return SUCCESS;
                case goToPrison:
                    //идти в тюрьму
                    playerRepo.setPosition(player, 10);
                    playerRepo.setJailMove(player, 3);
                    playerRepo.setDoubles(player, 0);
                    return SUCCESS;
                case property:
                    //комунальное предприятие
                    //станция
                    //улица
                    //если владелец - банк
                    Property property = mapServ.getPropertyByPosition(player.position);
                    if (getOwner(property) == game.bank) {
                        //TODO продумать функционал аукциона
                        makeOffer(player, property, OfferTypes.sold, property.price, null, game.bank);
                        //game.auction = new Auction(property, player);
                        /*payment(player, game.bank, property.price);
                        property.owner = player;*/
                        return AUCTION;
                    }
                    //если владелец - другой игрок
                    else if (getOwner(property) != player) {
                        return payRent(property, dice1 + dice2);
                    }
                    break;
            }
        } else {
            //логика выхода из тюрьмы через 3 хода после не выпадения дубля
            // игрок платит 50 и идет на количество выпавших очков кубиков
            if (player.jailMove == 1) {
                payment(player, game.bank, 50);
                playerRepo.reduceJailMove(player);
                move(dice1, dice2, player);
            }
            // если не последний ход в тюрьме
            else playerRepo.reduceJailMove(player);
            return SUCCESS;
        }
        return ERROR;
    }

    //окончание хода - передача кубиков следующему игроку или повторный бросок
    private String endMotion(Player player) {
        if (!isCurrentPLayer(player)) {
            return "You are not a current player!";
        }
        if (game.state != GameStates.onPlay)
            return GAME_NOT_ON_PLAY;

        if (!player.hasDebts()) {
            if (player.doubles == 0) {
                giveDicesToNextPlayer();
            } else playerRepo.setCanRollDice(player, true);
        } else
            return "You have debts!";
        return SUCCESS;
    }

    // оплата ренты при попадании на поле
    public String payRent(Property property, int dices) {
        Player currentPlayer = getCurrentPlayer();

        switch (property.type) {
            case street:
                Street street = (Street) property;
                // подсчет числа улиц в данной цветовой группе у владельца данной улицы
                int cnt = (int) mapServ.getStreets().stream()
                        .filter(x -> x.colour == street.colour && getOwner(x) == getOwner(property))
                        .count();
                if (!street.deposit) {//если улица не заложена

                    if (cnt == 3 || (cnt == 2 && (street.colour == 1 || street.colour == 8))) {
                        switch (getHouses(street)) {
                            case 1:
                                return payment(currentPlayer, getOwner(property), street.rent_1_house);
                            case 2:
                                return payment(currentPlayer, getOwner(property), street.rent_2_house);
                            case 3:
                                return payment(currentPlayer, getOwner(property), street.rent_3_house);
                            case 4:
                                return payment(currentPlayer, getOwner(property), street.rent_4_house);
                            case 5:
                                return payment(currentPlayer, getOwner(property), street.rent_hotel);
                            case 0:
                                return payment(currentPlayer, getOwner(property), street.full_group_rent);
                            default:
                                return "ERROR unacceptable number of houses";
                        }
                    } else {
                        return payment(currentPlayer, getOwner(property), street.rent);
                    }
                }
                break;
            case station:
                // подсчет числа станций в собственности владельца текущей станции
                int cnt1 = (int) mapServ.getRailwayStations().stream()
                        .filter(x -> getOwner(x) == getOwner(property))
                        .count();
                switch (cnt1) {
                    case 1:
                        return payment(currentPlayer, getOwner(property), 25);
                    case 2:
                        return payment(currentPlayer, getOwner(property), 50);
                    case 3:
                        return payment(currentPlayer, getOwner(property), 100);
                    case 4:
                        return payment(currentPlayer, getOwner(property), 200);
                    default:
                        return "ERROR unacceptable number of stations";
                }
            case municipal:
                // подсчет числа предприятий в собственности владельца текущего предприятия
                int cnt2 = (int) mapServ.getMunicipalEnterprises().stream()
                        .filter(x -> getOwner(x) == getOwner(property))
                        .count();
                switch (cnt2) {
                    case 1:
                        return payment(currentPlayer, getOwner(property), 4 * dices);
                    case 2:
                        return payment(currentPlayer, getOwner(property), 10 * dices);
                    default:
                        return "ERROR unacceptable number of municipal";
                }
        }
        return ERROR;
    }

    // перевод денег от отправителя(sender) к получателю(recipient)
    public String payment(Player sender, Player recipient, int sum) {
        if (sum < 0)
            return NEGATIVE_SUM;

        //если у отправителя недостаточно денег для оплаты
        if (sender.cash < sum) {
            playerRepo.addDebt(sender, new Debt(sender, recipient, sum));
            //sender.addDebt(new Debt(sender, recipient, sum));
            return NOT_ENOUGH_MONEY;
        }
        // иначе перевод денег от sender к recipient
        playerRepo.reduceCash(sender, sum);
        playerRepo.addCach(recipient, sum);
        return SUCCESS;
    }

    // покупка дома на улицу
    public String buyHouse(Street street, Player buyer) {
        if (getOwner(street) != buyer) {
            return NOT_AN_OWNER;
        }

        if (getHouses(street) == 5) {
            return HOTEL_ALREADY;
        }

        if (street.house_price > buyer.cash) {
            return NOT_ENOUGH_MONEY;
        }

        int cnt = 0; // подсчет количества улиц этой цветовой группы у владельца
        int col = 0;   // подсчет числа улиц в группе, с количеством домов меньшим чем на выбранной улице

        for (Street streetI : mapServ.getStreets()) {
            if (streetI.colour == street.colour
                    && getOwner(streetI) == buyer) {
                cnt++;
                //логика равномерного распределения домов
                if (getHouses(streetI) < getHouses(street)) col++;
            }
        }

        if (col != 0) {
            return NOT_AN_EQUAL_NUMBER_OF_HOUSES;
        }

        if (
                (cnt < 3 && street.colour > 1 && street.colour < 8) ||
                        (cnt < 2 && (street.colour == 1 || street.colour == 8))
        ) {
            return NOT_A_FULL_GROUP;
        }
        payment(buyer, game.bank, street.house_price);
        gameRepo.addHouse(street);
        return SUCCESS;
    }

    // продажа дома с улицы street
    public String soldHouse(Street street, Player seller) {
        if (getOwner(street) != seller) {
            return NOT_THE_OWNER;
        }

        if (getHouses(street) == 0) {
            return NO_HOUSES;
        }

        //подсчет числа улиц в группе, с количеством домов большим чем на выбранной улице
        int col = 0;
        for (Street streetI : mapServ.getStreets()) {
            if (streetI.colour == street.colour
                    && getOwner(streetI) == seller) {
                if (getHouses(streetI) > getHouses(street)) col++;
            }
        }

        //логика равномерного распределения домов
        if (col != 0) {
            return "There are not an equal number of houses on the streets!";
        }

        payment(game.bank, seller, street.house_price / 2);
        gameRepo.reduceHouses(street);
        return SUCCESS;
    }

    // выкуп заложенной собственности prop игроком player
    private String buyDepositProperty(Property property, Player player) {
        if (property.deposit) {
            //оплатить 10% от ее залоговой стоимости = стоимость выкупа - залоговая стоимость или
            if (!player.repayment) {
                if (player.cash < property.tenPercent) {
                    return "You has not enough money to pay 10% of properties deposit price!";
                }
                return payment(player, game.bank, property.tenPercent);
            } else {//выкупить заложенную собственность
                if (player.cash < property.redemptionPrice) {
                    return "You has not enough money to pay properties redemptionPrice!";
                }
                String result = payment(player, game.bank, property.redemptionPrice);
                if (result.equals(SUCCESS)) {
                    property.deposit = false;
                }
                return result;
            }
        } else {
            return "Property is not deposit!";
        }
    }

    //заложить собственность prop
    public String createDeposit(Property property, Player player) {
        if (getOwner(property) != player) {
            return "You are not an owner of this property!";
        }

        if (property.type == PropTypes.street) {
            //street
            Street street = (Street) property;
            int cnth = 0;
            for (Street streetI : mapServ.getStreets()) {
                if (streetI.colour == street.colour
                        && getOwner(streetI) == player) cnth += getHouses(streetI);
            }
            if (cnth > 0) {
                return "You can not deposit the street from group with houses!";
            }
        }
        String result = payment(game.bank, player, property.depositPrice);
        if (result.equals(SUCCESS))
            property.deposit = true;
        return result;
    }

    //выкупить заложенную собственность
    public String destroyDeposit(Property property, Player player) {
        if (getOwner(property) != player) {
            return "You are not an owner of this property!";
        }

        if (player.cash < property.redemptionPrice) {
            return NOT_ENOUGH_MONEY;
        }

        String result = payment(player, game.bank, property.redemptionPrice);
        if (result.equals(SUCCESS))
            property.deposit = false;
        return result;
    }

    // создать предложение игроку под номером recipient
    public String makeOffer(Player recipient,
                            Property senderProperty,
                            OfferTypes offerType,
                            int sum,
                            Property recipientProperty,
                            Player player) {
        /*
        Валидация
        if(offerType == OfferTypes.buy)
            if(recipientProperty==null)
                return ERROR;
        if(offerType == OfferTypes.sold)
            if(senderProperty==null)
                return ERROR;*/

        boolean flag1 = (offerType == OfferTypes.buy && getOwner(recipientProperty) == recipient);
        boolean flag2 = (offerType == OfferTypes.sold && getOwner(senderProperty) == player);
        boolean flag3 = (offerType == OfferTypes.change && getOwner(recipientProperty) == recipient && getOwner(senderProperty) == player);
        if (!(flag1 || flag2 || flag3)) {
            return SOMEBODY_NOT_THE_OWNER;
        }


        //проверка отсутствия домов в группе отправляющего предложение
        // (иначе нельзя производить операции с выбранной улицей)
        if (senderProperty != null) {
            Street street = senderProperty.getStreet();
            if (street != null) {
                for (Street streetI : mapServ.getStreets()) {
                    if (streetI.colour == street.colour
                            && getOwner(streetI) == player
                            && getHouses(streetI) > 0) {
                        return CANT_SOLD_BUY_CHANGE_STREET;
                    }
                }
            }
        }

        switch (offerType) {
            case change:
                /*//проверка отсутствия домов на собственности recipient в группе(иначе нельзя производить операции с выбранной улицей)
                if(recipientProperty!=null){
                    Street street = recipientProperty.getStreet();
                    if (street!=null) {
                        for (Street streetI : mapServ.getStreets()) {
                            if (streetI.colour == street.colour
                                    && getOwner(streetI) == player && getHouses(streetI) > 0) {
                                return "You can not change the street from group with houses!";
                            }
                        }
                    }
                }*/
                if (sum < 0) {
                    if (player.cash < -sum) {
                        return NOT_ENOUGH_MONEY;
                    }
                }
                break;
            case buy:
                if (sum < 0) {
                    return NEGATIVE_SUM;
                }
                if (player.cash < sum) {
                    return NOT_ENOUGH_MONEY;
                }
            case sold:
                if (sum < 0) {
                    return NEGATIVE_SUM;
                }
                break;
        }
        playerRepo.addOffer(recipient,
                new Offer(
                        player,
                        recipient,
                        senderProperty,
                        sum,
                        offerType,
                        OfferStates.newOffer,
                        recipientProperty
                )
        );
        return SUCCESS;
    }

    // отклонить предложение игрока
    public String rejectOffer(Offer offer, Player player) {
        if (offer.state == OfferStates.rejectOffer || offer.state == OfferStates.acceptOffer) {
            return CANT_REJECT_OFFER;
        }
        offer.state = OfferStates.rejectOffer;
        return SUCCESS;
    }

    // принять предложение
    public String acceptOffer(Offer offer, Player player) {
        // новый владелец заложенной собственности должен либо сразу выкупить собственность
        // либо заплатить 10% от ее залоговой стоимости и оставить ее в залоге()

        //у принимающего предложение хотят купить собственность
        boolean sold = offer.type == OfferTypes.buy
                && getOwner(offer.recipientProperty) == player;
        //принимающему предложение хотят продать собственность
        boolean buy = offer.type == OfferTypes.sold
                && getOwner(offer.senderProperty) == offer.sender;
        //обмен собственностями
        boolean change = offer.type == OfferTypes.change
                && getOwner(offer.senderProperty) == offer.sender
                && getOwner(offer.recipientProperty) == player;

        if (!(sold || buy || change)) {
            return SOMEBODY_NOT_THE_OWNER;
        }

        switch (offer.type) {
            case sold:
                Street street = offer.recipientProperty.getStreet();
                if (street != null)
                    if (getHouses(street) > 0) {
                        return "You can not sold the street with houses!";
                    }

                if (offer.sender.cash < offer.getFullSum(Participants.sender)) {
                    return "Sender has not enough money to buy your property!";
                } else {
                    payment(offer.sender, player, offer.sum);
                    //если собственность не заложена
                    if (offer.recipientProperty.deposit) {
                        buyDepositProperty(offer.recipientProperty, player);
                    }
                    setOwner(offer.recipientProperty, offer.sender);
                }
                break;

            case buy:
                Street street1 = offer.recipientProperty.getStreet();
                if (street1 != null)
                    if (getHouses(street1) > 0) {
                        return "You can not buy the street with houses!";
                    }

                if (player.cash < offer.getFullSum(Participants.recipient)) {
                    return "You has not enough money to buy senders property!";
                } else {
                    payment(player, offer.sender, offer.sum);
                    //если собственность не заложена
                    if (offer.senderProperty.deposit) {
                        buyDepositProperty(offer.senderProperty, offer.sender);
                    }
                    setOwner(offer.senderProperty, player);
                }
                break;

            case change:
                boolean senderStreetHasHouses = false;
                Street recipientStreet = offer.recipientProperty.getStreet();
                if (recipientStreet != null)
                    senderStreetHasHouses = getHouses(recipientStreet) > 0;

                boolean recipientStreetHasHouses = false;
                Street senderStreet = offer.senderProperty.getStreet();
                if (senderStreet != null)
                    recipientStreetHasHouses = getHouses(senderStreet) > 0;

                if (senderStreetHasHouses || recipientStreetHasHouses) {
                    return "You can not change streets with houses!";
                }

                if (player.cash < offer.getFullSum(Participants.recipient)) {
                    return "You has not enough money to pay for senders property!";
                } else {
                    if (offer.senderProperty.deposit) {
                        buyDepositProperty(offer.senderProperty, player);
                    }
                }

                if (offer.sender.cash < offer.getFullSum(Participants.sender)) {
                    return "Offers sender has not enough money to pay for your property!";
                } else {
                    if (offer.recipientProperty.deposit) {
                        buyDepositProperty(offer.recipientProperty, offer.sender);
                    }
                }

                if (offer.sum > 0)
                    payment(player, offer.sender, offer.sum);
                else
                    payment(offer.sender, player, offer.sum);


                setOwner(offer.recipientProperty, offer.sender);
                setOwner(offer.senderProperty, player);
                break;
        }
        offer.state = OfferStates.acceptOffer;
        return SUCCESS;
    }

    //выплатить долг
    public String repayDebt(Player player, Debt debt) {
        if (debt.debtor.equals(player))
            return payment(debt.debtor, debt.recipient, debt.sum);
        return "Incorrect recipient!";
    }

    //закончить аукцион
    public Player endAuction() {
        Auction auction = game.auction;
        if (auction.participants.size() == 1 &&
                auction.participants.get(0) == auction.winner) {
            payment(auction.winner, game.bank, auction.property.price);
            setOwner(auction.property, auction.winner);
            gameRepo.setState(GameStates.onPlay);
            gameRepo.setAuction(null);
            return auction.winner;
        } else {
            return null;
        }
    }

    public void startAuction(Auction auction) {
        gameRepo.setAuction(auction);
        gameRepo.setState(GameStates.onPause);
    }

    public Player enterGame(String name) {
        boolean unicName = game.players.stream().noneMatch(x -> x.name == name);
        if (unicName) {
            Player newPlayer = new Player(1500, name);
            if (game.players.size() < game.maxPLayers) {
                gameRepo.addNewPlayer(newPlayer);
                return newPlayer;
            }
        }
        return null;
    }

    public String startGame(Player player) {

        if (player.name != game.organizer)
            return "You are not the organizer";

        if (game.state == GameStates.onStart) {
            gameRepo.setState(GameStates.onPlay);
            //случайное перемешивание игроков
            //TODO подумать о синхронизации данного действия
            gameRepo.mixPLayers();
            playerRepo.setCanRollDice(game.players.get(0), true);
        } else
            return "Game is already started!";

        return SUCCESS;
    }

    public Player getCurrentPlayer() {
        return game.players.get(game.currentPlayerId);
    }

    public boolean isCurrentPLayer(Player player) {
        return game.currentPlayerId == game.players.indexOf(player);
    }

    public void giveDicesToNextPlayer() {
        playerRepo.setCanRollDice(getCurrentPlayer(), false);
        do {
            if (game.currentPlayerId == game.players.size() - 1) {
                gameRepo.setCurrentPlayerID(0);
            } else
                gameRepo.setCurrentPlayerID(game.currentPlayerId++);
        } while (getCurrentPlayer().bankrupt);
        playerRepo.setCanRollDice(getCurrentPlayer(), true);
    }

    public String pauseGame(Player player) {
        if (game.state == GameStates.onPlay) {
            gameRepo.setState(GameStates.onPause);
            gameRepo.setPausedPlayer(game.players.indexOf(player));
            return SUCCESS;
        }
        return "Game is already paused!";
    }

    public String continueGame(Player player) {
        if (game.pausedPlayer == game.players.indexOf(player)) {
            gameRepo.setState(GameStates.onPlay);
            gameRepo.setPausedPlayer(-1);
            return SUCCESS;
        }
        return "You didn't pause the game!";
    }

    public Player getOwner(Property property) {
        int idOwner = game.fieldsOwners
                .get(MapService.getInstance().map.indexOf(property))
                .owner;
        return idOwner == -1 ? game.bank : game.players.get(idOwner);
    }

    public void setOwner(Property property, Player newOwner) {
        int idProperty = MapService.getInstance().map.indexOf(property);
        int newOwnerId = game.players.indexOf(newOwner);
        gameRepo.setNewOwner(idProperty, newOwnerId);
    }

    public int getHouses(Street street) {
        return game.fieldsOwners.get(
                MapService.getInstance()
                        .map.indexOf(street)
        ).houses;
    }
}
