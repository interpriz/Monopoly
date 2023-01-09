package entities;

import java.util.ArrayList;
import java.util.UUID;

public class Player {
    public String name;
    public ArrayList<Offer> offers = new ArrayList<>();
    public int position;  // позиция игрока на поле
    public int cash;      // деньги игрока
    public int jailMove; // количество ходов в тюрьме
    public int doubles;   // количество выпавших дублей
    //выкупать заложенную собственность(true) или оплатить 10% от ее залоговой стоимости(false)?
    public boolean repayment;
    public boolean bankrupt;  // игрок банкрот?
    public boolean canRollDice; // можно бросать кубик? взводится перед броском игрока
    public ArrayList<Debt> debts = new ArrayList<>();
    //public ArrayList<FieldDB> tests = new ArrayList<>();
    //public Auction tests = null;

    public Player() {
    }

    public Player(int cash, String name) {
        this.position = 0;
        this.cash = cash;
        this.jailMove = 0;
        this.doubles = 0;
        this.repayment = false;
        this.bankrupt = false;
        this.canRollDice = false;
        this.name = name;
        //tests.add(new FieldDB(0,0));
        //tests.add(new FieldDB(1,1));
    }

    public Debt getLastDebt(){
        if(!debts.isEmpty()){
            return debts.get(debts.size()-1);
        }else return null;
    }

    public Offer getLastOffer(){
        if(!offers.isEmpty()){
            return offers.get(offers.size()-1);
        }else return null;
    }

    public boolean hasDebts(){
        return debts.size()>0;
    }

}
