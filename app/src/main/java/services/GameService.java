package services;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import static entities.StaticMessages.*;

import com.google.firebase.database.DatabaseReference;

import entities.Auction;
import entities.Debt;
import entities.Game;
import entities.Offer;
import entities.StaticMessages;
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

    //private final Game game;

    private final MapService mapServ;

    private final PlayerRepository playerRepo;
    private final GameRepository gameRepo;

    private boolean test;
    private int d1;
    private int d2;

    public void setTest(Boolean test) {
        this.test = test;
    }

    public void setD1D2(int d1, int d2) {
        this.d1 = d1;
        this.d2 = d2;
    }


    public GameService(String gameName) {
        //this.game = game;
        this.gameRepo = GameRepository.getInstance(gameName);
        this.mapServ = MapService.getInstance();
        this.playerRepo = PlayerRepository.getInstance(gameName);
        this.test = false;
    }
    
    public Game getGame(){
        return gameRepo.getGame();
    }

    public Player gameInitialise(String yourNickname){
        //gameRepo.addGameFirsListen();
        int a = 0;
        if (getGame() == null) {
            gameRepo.setGame(new Game(2, yourNickname));
            //gameRef.setValue(game);
            //gameService = new GameService(game);
            return enterGame(yourNickname);
            //gameService.enterGame("Sasha");
            //gameService.enterGame("Sveta");
            //gameService.enterGame("Lola");
        } else {
            //yourPlayer = game().players.get(0);

            Optional<Player> player = getGame().players.stream().filter(x -> x.name.equals(yourNickname)).findFirst();


            if (player.isPresent()) {
                return  player.get();
                //gameService = new GameService(game);
            } else {
                if (getGame().state == GameStates.onStart) {
                    //gameService = new GameService(game);
                    return enterGame(yourNickname);
                } else {
                    return null;
                }
            }
        }
        //TODO для теста
        //currentPlayer = gameService.getCurrentPlayer();
        //gameService.setTest(true);
    }

    public DatabaseReference getGameRef(){
        return gameRepo.DBGameReference;
    }

    //войти в игру
    public Player enterGame(String name) {
        boolean unicName = getGame().players.stream().noneMatch(x -> x.name.equals(name));
        if (unicName && name!="BANK") {
            Player newPlayer = new Player(1500, name);
            if (getGame().players.size() < getGame().maxPLayers) {
                gameRepo.addNewPlayer(newPlayer);
                return newPlayer;
            }
        }
        return null;
    }

    //начать игру
    public String startGame(Player player) {

        if (!player.name.equals(getGame().organizer))
            return NOT_ORGANIZER;

        if (getGame().state == GameStates.onStart) {
            gameRepo.setState(GameStates.onPlay);
            //случайное перемешивание игроков
            //TODO подумать о синхронизации данного действия
            //gameRepo.mixPLayers();
            playerRepo.setCanRollDice(getGame().players.get(0), true);
        } else
            return GAME_IS_STARTED;

        return SUCCESS;
    }

    //бросок кубиков
    private String diceRoll(/*Player player*/) {
        /*if (!game().isCurrentPLayer(player)) {
            String message = "You are not a current player!";
            return;
        }*/
        Player player = getCurrentPlayer();
        if (player.canRollDice) {
            if(!test){
                gameRepo.setDice1(new Random().nextInt(6) + 1);
                gameRepo.setDice2(new Random().nextInt(6) + 1);
            }else{
                gameRepo.setDice1(d1);
                gameRepo.setDice2(d2);
            }

            playerRepo.setCanRollDice(player, false);
        } else {
            return ALREADY_ROLL;
        }
        return SUCCESS;
    }

    //осуществление хода
    public String makeMove(Player player) {
        if (getGame().state != GameStates.onPlay)
            return GAME_NOT_ON_PLAY;

        if (getCurrentPlayer() != player) {
            return NOT_CURRENT_PLAYER;
        }
        //проверка на повторный бросок кубиков
        String result = diceRoll();
        if(result != SUCCESS)
            return result;


        //Player player = getCurrentPlayer();

        int bankrupts = 0;//количество банкротов в игре
        Player winner = null;//возможный победитель

        // подсчет числа банкротов
        for (Player playerI : getGame().players) {
            //если игрок банкрот
            if (playerI.bankrupt) {
                bankrupts++;
            } else winner = playerI;
        }

        //если не все кроме одного банкроты
        if (bankrupts != getGame().players.size() - 1) {
            //если текущий игрок банкрот, то передать кубики
            if (player.bankrupt) {
                giveDicesToNextPlayer();
            } else {//иначе произвести ход
                //если в процессе ходя игрок не смог заплатить и он не банкрот,
                //то ему записывается долг, который надо погасить, после чего закончить ход endMotion.
                //если игрок устроил аукцион, то он должен завершить ход после окончания аукциона
                //TODO проверить возвращаемое значение на наличие ошибки оплаты
                result = move(getGame().dice1, getGame().dice2, player);
                assert result != null;
                switch (result) {
                    case SUCCESS:
                        //endMotion();
                        return SUCCESS;

                    case NOT_ENOUGH_MONEY:
                        return bankruptCheck(player);
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
            gameRepo.setWinner(getGame().players.indexOf(winner));
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
            if (dice1 + dice2 + player.position > 40) payment(getGame().bank, player, 200);

            //подсчет текущей позиции игрока как старая позиция + число на кубиках % 40
            int newPosition = (dice1 + dice2 + player.position) % 40;
            playerRepo.setPosition(player, newPosition);
            switch (mapServ.getFieldByPosition(newPosition).getType()) {
                case forward:
                    //поле вперед
                    return payment(getGame().bank, player, 200);
                case publicTreasury:
                    //общественная казна
                    return (new Random().nextInt(2) == 0) ?
                            payment(getGame().bank, player, new Random().nextInt(200) + 1) :
                            payment(player, getGame().bank, new Random().nextInt(200) + 1);
                case chance:
                    //шанс
                    return (new Random().nextInt(2) == 0) ?
                            payment(getGame().bank, player, new Random().nextInt(200) + 1) :
                            payment(player, getGame().bank, new Random().nextInt(200) + 1);
                case incomeTax:
                    //подоходный налог
                    return payment(player, getGame().bank, 200);
                case superTax:
                    //сверхналог
                    return payment(player, getGame().bank, 100);
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
                    Property property = mapServ.getPropertyByPosition(newPosition);
                    if (getOwner(property) == getGame().bank) {
                        //TODO продумать функционал аукциона
                        makeOffer(player, property, OfferTypes.sold, property.price, null, getGame().bank);
                        //game().auction = new Auction(property, player);
                        /*payment(player, game().bank, property.price);
                        property.owner = player;*/
                        return AUCTION;
                    }
                    //если владелец - другой игрок
                    else if (getOwner(property) != player) {
                        return payRent(property, dice1 + dice2);
                    } else return SUCCESS;
            }
        } else {
            //логика выхода из тюрьмы через 3 хода после не выпадения дубля
            // игрок платит 50 и идет на количество выпавших очков кубиков
            if (player.jailMove == 1) {
                playerRepo.reduceJailMove(player);

                String result = payment(player, getGame().bank, 50);
                switch (result){
                    case NOT_ENOUGH_MONEY:
                        String res = bankruptCheck(player);
                        if(!res.equals(BANKRUPT)){
                            res = move(dice1, dice2, player);
                            return res;
                            //break;
                        }else
                            return res;
                    case SUCCESS:
                        String res1 =  move(dice1, dice2, player);
                        return res1;
                        //break;
                }
            }
            // если не последний ход в тюрьме
            else {
                playerRepo.reduceJailMove(player);
                return SUCCESS;
            }
        }
        return ERROR;
    }

    //окончание хода - передача кубиков следующему игроку или повторный бросок
    public String endMotion(/*Player player*/) {
        /*if (!isCurrentPLayer(player)) {
            return "You are not a current player!";
        }*/
        Player player = getCurrentPlayer();
        if (getGame().state != GameStates.onPlay)
            return GAME_NOT_ON_PLAY;

        if (!player.hasDebts()) {
            if (player.doubles == 0) {
                giveDicesToNextPlayer();
            } else playerRepo.setCanRollDice(player, true);
        } else
            return HAVE_DEBTS;

        return SUCCESS;
    }

    // оплата ренты при попадании на поле
    private String payRent(Property property, int dices) {
        Player currentPlayer = getCurrentPlayer();

        switch (property.type) {
            case street:
                Street street = (Street) property;
                // подсчет числа улиц в данной цветовой группе у владельца данной улицы
                int cnt = (int) mapServ.getStreets().stream()
                        .filter(x -> x.colour == street.colour && getOwner(x) == getOwner(property))
                        .count();
                if (!getDeposit(street)) {//если улица не заложена

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
                                return ERROR_HOUSES_NUMBER;
                        }
                    } else {
                        return payment(currentPlayer, getOwner(property), street.rent);
                    }
                } else
                    return SUCCESS;
            case station:
                // подсчет числа станций в собственности владельца текущей станции
                if(!getDeposit(property)){
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
                            return ERROR_STATIONS_NUMBER;
                    }
                }else
                    return  SUCCESS;
            case municipal:
                if(!getDeposit(property)) {
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
                            return ERROR_MUNICIPALS_NUMBER;
                    }
                }else
                    return  SUCCESS;

        }
        return ERROR;
    }

    // перевод денег от отправителя(sender) к получателю(recipient)
    public String payment(Player sender, Player recipient, int sum) {
        if (sum < 0)
            return NEGATIVE_SUM;

        //если у отправителя недостаточно денег для оплаты
        if (sender.cash < sum) {
            playerRepo.addDebt(sender,
                    new Debt(
                            getGame().players.indexOf(sender),
                            getGame().players.indexOf(recipient),
                            sum));
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
        payment(buyer, getGame().bank, street.house_price);
        gameRepo.addHouse(street);
        return SUCCESS;
    }

    // продажа дома с улицы street
    public String soldHouse(Street street, Player seller) {
        if (getOwner(street) != seller) {
            return NOT_THE_PROPERTY_OWNER;
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
            return NOT_AN_EQUAL_NUMBER_OF_HOUSES;
        }

        payment(getGame().bank, seller, street.house_price / 2);
        gameRepo.reduceHouses(street);
        return SUCCESS;
    }

    // выкуп заложенной собственности prop игроком player
    private String buyDepositProperty(Property property, Player player) {
        if (getDeposit(property)) {
            //оплатить 10% от ее залоговой стоимости = стоимость выкупа - залоговая стоимость или
            if (!player.repayment) {
                /*if (player.cash < property.tenPercent) {
                    return "You has not enough money to pay 10% of properties deposit price!";
                }*/
                return payment(player, getGame().bank, property.tenPercent);
            } else {//выкупить заложенную собственность
                /*if (player.cash < property.redemptionPrice) {
                    return "You has not enough money to pay properties redemptionPrice!";
                }*/
                String result = payment(player, getGame().bank, property.redemptionPrice);
                if (result.equals(SUCCESS)) {
                    setDeposit(property, false);
                }
                return result;
            }
        } else {
            return NOT_DEPOSIT;
        }
    }

    //заложить собственность prop
    public String createDeposit(Property property, Player player) {
        if (getOwner(property) != player) {
            return NOT_AN_OWNER;
        }

        if (getDeposit(property))
            return ALREADY_DEPOSIT;

        //проверка отсутствия домов в группе отправляющего предложение
        // (иначе нельзя производить операции с выбранной улицей)
        Street street = getStreet(property);
        if (isHousesInGroup(street))
            return CANT_DEPOSIT_STREET_WITH_HOUSES;

        String result = payment(getGame().bank, player, property.depositPrice);
        if (result.equals(SUCCESS))
            setDeposit(property,true);
        return result;
    }

    //выкупить заложенную собственность
    public String destroyDeposit(Property property, Player player) {
        if (getOwner(property) != player) {
            return NOT_AN_OWNER;
        }

        if (!getDeposit(property))
            return NOT_DEPOSIT;

        if (player.cash < property.redemptionPrice) {
            return NOT_ENOUGH_MONEY;
        }

        String result = payment(player, getGame().bank, property.redemptionPrice);
        if (result.equals(SUCCESS))
            setDeposit(property,false);
        return result;
    }

    // создать предложение игроку под номером recipient
    public String makeOffer(Player recipient,
                            Property senderProperty,
                            OfferTypes offerType,
                            int sum,
                            Property recipientProperty,
                            Player player) {
        //Валидация
        if(offerType == OfferTypes.buy)
            if(recipientProperty==null)
                return RECIPIENT_EMPTY_PROPERTY;
        if(offerType == OfferTypes.sold)
            if(senderProperty==null)
                return SENDER_EMPTY_PROPERTY;
        if(offerType == OfferTypes.change)
            if(senderProperty==null && recipientProperty==null)
                return SENDER_RECIPIENT_EMPTY_PROPERTY;

        boolean flag1 = (offerType == OfferTypes.buy && getOwner(recipientProperty) == recipient);
        boolean flag2 = (offerType == OfferTypes.sold && getOwner(senderProperty) == player);
        boolean flag3 = (offerType == OfferTypes.change && getOwner(recipientProperty) == recipient && getOwner(senderProperty) == player);
        if (!(flag1 || flag2 || flag3)) {
            return SOMEBODY_NOT_THE_OWNER;
        }


        //проверка отсутствия домов в группе отправляющего предложение
        // (иначе нельзя производить операции с выбранной улицей)
        if (senderProperty != null) {
            Street street = getStreet(senderProperty);
            if (isHousesInGroup(street))
                return CANT_SOLD_BUY_CHANGE_STREET;
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
                        getPlayerId(player),
                        getPlayerId(recipient),
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

        if (offer.recipientID != getPlayerId(player))
            return NOT_THE_OFFER_OWNER;

        if (offer.state == OfferStates.rejectOffer || offer.state == OfferStates.acceptOffer) {
            return CANT_REJECT_OFFER;
        }
        playerRepo.setOfferState(player, offer,OfferStates.rejectOffer);
        return SUCCESS;
    }

    // принять предложение
    public String acceptOffer(Offer offer, Player player) {

        if (offer.recipientID != getPlayerId(player))
            return NOT_THE_OFFER_OWNER;
        if (offer.state != OfferStates.newOffer)
            return CANT_ACCEPT_OFFER;

        // новый владелец заложенной собственности должен либо сразу выкупить собственность
        // либо заплатить 10% от ее залоговой стоимости и оставить ее в залоге()

        //у принимающего предложение хотят купить собственность
        boolean sold = offer.type == OfferTypes.buy
                && getOwner(offer.recipientProperty()) == player;
        //принимающему предложение хотят продать собственность
        boolean buy = offer.type == OfferTypes.sold
                && getOwner(offer.senderProperty()) == getPlayer(offer.senderID);
        //обмен собственностями
        boolean change = offer.type == OfferTypes.change
                && getOwner(offer.senderProperty()) == getPlayer(offer.senderID)
                && getOwner(offer.recipientProperty()) == player;

        if (!(sold || buy || change)) {
            return SOMEBODY_NOT_THE_OWNER;
        }

        switch (offer.type) {
            case sold:
                //проверка отсутствия домов на собственности sender в группе
                // (иначе нельзя производить операции с выбранной улицей)
                Street street = getStreet(offer.senderProperty());
                if (isHousesInGroup(street))
                    return CANT_BUY_STREET_WITH_HOUSES;

                //проверка на наличие денег у получателя предложения
                if (player.cash < getFullOfferSum(offer, Participants.recipient)) {
                    return NOT_ENOUGH_MONEY;
                } else {
                    payment(player, getPlayer(offer.senderID), offer.sum);
                    //если собственность не заложена
                    if (getDeposit(offer.senderProperty())) {
                        buyDepositProperty(offer.senderProperty(), player);
                    }
                    setOwner(offer.senderProperty(), player);
                }
                break;

            case buy:
                //проверка отсутствия домов на собственности recipient в группе
                // (иначе нельзя производить операции с выбранной улицей)
                Street street1 = getStreet(offer.recipientProperty());
                if (isHousesInGroup(street1))
                    return CANT_SOLD_STREET_WITH_HOUSES;

                //проверка на наличие денег у отправителя предложения
                if (getPlayer(offer.senderID).cash < getFullOfferSum(offer, Participants.sender)) {
                    return SENDER_NOT_ENOUGH_MONEY;
                } else {
                    payment(getPlayer(offer.senderID), player, offer.sum);
                    //если собственность не заложена
                    if (getDeposit(offer.recipientProperty())) {
                        buyDepositProperty(offer.recipientProperty(), getPlayer(offer.senderID));
                    }
                    setOwner(offer.recipientProperty(), getPlayer(offer.senderID));
                }
                break;

            case change:
                //проверка отсутствия домов на собственности recipient и sender в группе
                // (иначе нельзя производить операции с выбранными улицами)
                Street recipientStreet = getStreet(offer.recipientProperty());
                Street senderStreet = getStreet(offer.senderProperty());

                if (isHousesInGroup(senderStreet)
                        || isHousesInGroup(recipientStreet)) {
                    return CANT_CHANGE_STREET_WITH_HOUSES;
                }

                //проверка на наличие денег у получателя предложения
                if (player.cash < getFullOfferSum(offer, Participants.recipient)) {
                    return NOT_ENOUGH_MONEY;
                } else {
                    if (getDeposit(offer.senderProperty())) {
                        buyDepositProperty(offer.senderProperty(), player);
                    }
                }

                //проверка на наличие денег у отправителя предложения
                if (getPlayer(offer.senderID).cash < getFullOfferSum(offer,Participants.sender)) {
                    return SENDER_NOT_ENOUGH_MONEY;
                } else {
                    if (getDeposit(offer.recipientProperty())) {
                        buyDepositProperty(offer.recipientProperty(), getPlayer(offer.senderID));
                    }
                }

                if (offer.sum > 0)
                    payment(player, getPlayer(offer.senderID), offer.sum);
                else
                    payment(getPlayer(offer.senderID), player, -offer.sum);


                setOwner(offer.recipientProperty(), getPlayer(offer.senderID));
                setOwner(offer.senderProperty(), player);
                break;
        }
        playerRepo.setOfferState(player, offer, OfferStates.acceptOffer);
        return SUCCESS;
    }

    //выплатить долг
    public String repayDebt(Player player, Debt debt) {
        if (debt.debtorID == getGame().players.indexOf(player))
            if (player.cash >= debt.sum){
                String result = payment(
                        getGame().players.get(debt.debtorID),
                        getPlayer(debt.recipientID),
                        debt.sum);
                if (result.equals(SUCCESS))
                    playerRepo.removeDebt(player, debt);

                return result;
            }
            else
                return bankruptCheck(player);
        return INCORRECT_RECIPIENT;
    }

    private String bankruptCheck(Player player){
        //проверка на наличие собственности
        int cnt = (int) mapServ.getProperties().stream().filter(x -> getOwner(x) == player).count();

        //если у игрока недостаточно денег и нет собственности, то он - банкрот
        if (cnt == 0) {
            playerRepo.setBankrupt(player, true);
            Player recipient = getPlayer(player.getLastDebt().recipientID);
            playerRepo.addCach(recipient, player.cash);
            playerRepo.setCash(player, 0);
            playerRepo.removeDebt(player, player.getLastDebt());

            return BANKRUPT;
        } else
            return SOLD_PROPERTY;
    }

    //начать аукцион
    public void startAuction(Property property) {
        gameRepo.setAuction(new Auction(property));
        gameRepo.setState(GameStates.onPause);
    }

    //сделать ставку на аукционе
    public String makeBetAuction(int newBet, Player player){
        if(player.cash<newBet){
            return NOT_ENOUGH_MONEY;
        }
        Auction auction= getGame().auction;
        if(auction==null)
            return NO_AUCTION;

        int idPlayer = getGame().players.indexOf(player);

        if(newBet>auction.bet){
            gameRepo.setAuctionBet(newBet);
            gameRepo.setAuctionWinner(idPlayer);
            if(!auction.participants.contains(idPlayer))
                gameRepo.setAuctionNewParticipant(idPlayer);
            return StaticMessages.SUCCESS;
        }
        else
            return SMALL_BET;
    }

    // выйти из аукциона
    public String goOutFromAuction(Player player){
        Auction auction= getGame().auction;
        if(auction==null)
            return NO_AUCTION;

        int idPlayer = getGame().players.indexOf(player);

        if(auction.winner != idPlayer && auction.participants.contains(idPlayer)){
            gameRepo.setAuctionRemovePlayer(idPlayer);
            return StaticMessages.SUCCESS;
        }else{
            return WINNER_CANT_GO_OUT;
        }
    }

    //закончить аукцион
    public Player endAuction() {
        Auction auction = getGame().auction;
        if (auction.participants.size() == 1 &&
                auction.participants.get(0) == auction.winner) {
            payment(getGame().players.get(auction.winner), getGame().bank, auction.bet);
            setOwner(auction.property, getGame().players.get(auction.winner));
            gameRepo.setState(GameStates.onPlay);
            gameRepo.setAuction(null);
            return getGame().players.get(auction.winner);
        } else {
            return null;
        }
    }

    public String goOutFromJail(Player player){
        if(getGame().currentPlayerId!= getPlayerId(player)){
            return NOT_CURRENT_PLAYER;
        }

        if(player.jailMove==0)
            return NOT_IN_JAIL;

        if(player.cash<50)
            return NOT_ENOUGH_MONEY;



        payment(player, getGame().bank, 50);
        playerRepo.setJailMove(player,0);

        return SUCCESS;
    }





    public Player getCurrentPlayer() {
        return getGame().players.get(getGame().currentPlayerId);
    }

    public boolean isCurrentPLayer(Player player) {
        return getGame().currentPlayerId == getGame().players.indexOf(player);
    }

    private void giveDicesToNextPlayer() {
        playerRepo.setCanRollDice(getCurrentPlayer(), false);
        int i = getGame().currentPlayerId;
        do {
            if (i == getGame().players.size() - 1) {
                i=0;
                //gameRepo.setCurrentPlayerID(0);
            } else
                i++;
                //gameRepo.setCurrentPlayerID(i++);
        } while (getPlayer(i).bankrupt);
        gameRepo.setCurrentPlayerID(i);
        playerRepo.setCanRollDice(getPlayer(i), true);
    }

    public String pauseGame(Player player) {
        if (getGame().state == GameStates.onPlay) {
            gameRepo.setState(GameStates.onPause);
            gameRepo.setPausedPlayer(getGame().players.indexOf(player));
            return SUCCESS;
        }
        return ALREADY_PAUSED;
    }

    public String continueGame(Player player) {
        if (getGame().pausedPlayer == getGame().players.indexOf(player)) {
            gameRepo.setState(GameStates.onPlay);
            gameRepo.setPausedPlayer(-1);
            return SUCCESS;
        }
        return CANT_CONTINUE;
    }

    public Player getOwner(Property property) {
        int idOwner = getGame().fieldsOwners
                .get(MapService.getInstance().map.indexOf(property))
                .owner;
        return idOwner == -1 ? getGame().bank : getGame().players.get(idOwner);
    }

    public void setOwner(Property property, Player newOwner) {
        int idProperty = MapService.getInstance().map.indexOf(property);
        int newOwnerId = getGame().players.indexOf(newOwner);
        gameRepo.setNewOwner(idProperty, newOwnerId);
    }

    public boolean getDeposit(Property property){
        int idProp =  mapServ.map.indexOf(property);
        return getGame().fieldsOwners.get(idProp).deposit;
    }

    public void setDeposit(Property property, boolean newDeposit){
        int idProp =  mapServ.map.indexOf(property);
        gameRepo.setNewDeposit(idProp, newDeposit);
    }

    public int getHouses(Street street) {
        return getGame().fieldsOwners.get(
                MapService.getInstance()
                        .map.indexOf(street)
        ).houses;
    }

    public ArrayList<Property> getPlayersProperty(int idPlayer){
        ArrayList<Integer> listPropertyIds = (ArrayList<Integer>) getGame().fieldsOwners
                .stream().filter(x->x.owner==idPlayer)
                .map(y-> getGame().fieldsOwners.indexOf(y)).collect(Collectors.toList());

        ArrayList<Property> listProperty = (ArrayList<Property>) listPropertyIds.stream()
                .map(x-> MapService.getInstance()
                        .getPropertyByPosition(x))
                .collect(Collectors.toList());

        return listProperty;
    }

    public ArrayList<String> getPlayersOffersStrings(Player player){
        ArrayList<String> offersStr = new ArrayList<>();
        ArrayList<Offer> playerOffers = (ArrayList<Offer>) player.offers.stream()
                .filter(x->x.state.equals(OfferStates.newOffer))
                .collect(Collectors.toList());
        for(Offer offer : playerOffers){
            String senderPropName = offer.senderPropertyID!=-1 ? mapServ.getPropertyNameByPosition(offer.senderPropertyID):"";
            String recipientPropName = offer.recipientPropertyID!=-1 ? mapServ.getPropertyNameByPosition(offer.recipientPropertyID):"";


            String offerStr =
            "Отправитель: "+ getPlayer(offer.senderID).name +
                    " хочет ";
            switch (offer.type) {
                case buy:
                    offerStr+= "купить собственность " + recipientPropName + " за " + offer.sum + "$";
                    break;
                case sold:
                    offerStr+="продать собственность " + senderPropName + " за " + offer.sum + "$";
                    break;
                case change:
                    offerStr+="обменять свою собственность " + senderPropName + " на " + recipientPropName + " c " +
                            (offer.sum > 0 ? "вашей доплатой в "+offer.sum+ "$" : "доплатой отправителя "+offer.sum*-1)+ "$";
                    break;
            }offersStr.add(offerStr);
        }
        return  offersStr;
    }

    public ArrayList<String> getPlayersDebtsStrings(Player player) {
        ArrayList<String> debtsStr = new ArrayList<>();
        ArrayList<Debt> playersDebts = player.debts;
        for(Debt debt: playersDebts){
            String str = "Вы должны "+ debt.sum +"$";
            if(debt.recipientID==-1){
                str+= " банку!";
            }else{
                str+= " игроку "+ getPlayer(debt.recipientID).name +"!";
            }
            debtsStr.add(str);
        }
        return debtsStr;
    }

    private boolean isHousesInGroup(Street street) {
        if (street != null) {
            for (Street streetI : mapServ.getStreets()) {
                if (streetI.colour == street.colour
                        && getOwner(streetI) == getOwner(street)
                        && getHouses(streetI) > 0)
                    return true;
            }
        }
        return false;
    }

    public Street getStreet(Property prop){
        if(prop.type==PropTypes.street){
            return (Street) prop;
        }else return null;
    }

    public Player getPlayer(int id){
        if(id==-1)
            return getGame().bank;
        return getGame().players.get(id);
    }

    public int getPlayerId(Player player){
        if(getGame().bank.equals(player))
            return -1;
        return getGame().players.indexOf(player);
    }

    public int getFullOfferSum(Offer offer, Participants player) {
        Player sender = getPlayer(offer.senderID);
        Player recipient = getPlayer(offer.recipientID);
        switch (offer.type) {
            case buy:
                switch (player) {
                    case sender:
                        return
                                offer.sum + (getDeposit(offer.recipientProperty()) ?
                                        (sender.repayment ?
                                                offer.recipientProperty().redemptionPrice :
                                                offer.recipientProperty().tenPercent)
                                        : 0);
                    case recipient:
                        return 0;
                }

            case sold:
                switch (player) {
                    case sender:
                        return 0;
                    case recipient:
                        return
                                offer.sum + (getDeposit(offer.senderProperty()) ?
                                        (recipient.repayment ?
                                                offer.senderProperty().redemptionPrice :
                                                offer.senderProperty().tenPercent)
                                        : 0);

                }
                ;
            case change:
                switch (player) {
                    case sender:
                        return
                                (offer.sum < 0 ? -1 * offer.sum : 0) + (getDeposit(offer.recipientProperty()) ?
                                        (sender.repayment ?
                                                offer.recipientProperty().redemptionPrice :
                                                offer.recipientProperty().tenPercent)
                                        : 0);
                    case recipient:
                        return
                                (offer.sum > 0 ? offer.sum : 0) + (getDeposit(offer.senderProperty()) ?
                                        (recipient.repayment ?
                                                offer.senderProperty().redemptionPrice :
                                                offer.senderProperty().tenPercent)
                                        : 0);
                }
        }
        return -1;
    }

    public String deleteGame(Player player){
        if(player.name.equals(getGame().organizer)){
            gameRepo.deleteGame();
        }
        return SUCCESS;
    }



}
