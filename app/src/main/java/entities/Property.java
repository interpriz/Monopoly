package entities;

import enums.FieldTypes;
import enums.PropTypes;

public class Property extends Field{
    //public Player owner;     // номер владельца (игрока в списке)
    public PropTypes type;    // f_types: //1 - street;  //2 - station; //3 - municipal;
    public int price;     // стоимость собственности
    public boolean deposit;   // находится ли собственность в залоге?
    public int depositPrice;     //залоговая стоимость
    public int redemptionPrice;  //стоимость выкупа
    public int tenPercent;      //10% от залоговой стоимости

    public Property(FieldTypes type, /*Player owner,*/ PropTypes type1, int price, int depositPrice, int redemptionPrice) {
        super(type);
        //this.owner = owner;
        this.type = type1;
        this.price = price;
        this.deposit = false;
        this.depositPrice = depositPrice;
        this.redemptionPrice = redemptionPrice;
        this.tenPercent = depositPrice*10/100;
    }

    /*public int getNUmberOfHouses(){
        if (type == PropTypes.street) {
            Street street = (Street) this;
            return street.houses;
        }
        return 0;
    }*/


}

