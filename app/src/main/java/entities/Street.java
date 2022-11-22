package entities;

import enums.FieldTypes;
import enums.PropTypes;

public class Street extends Property {

    public String name; // название
    public int colour; // цветовая группа 1-8
   /* public int houses;    // количество домов: 1-4 - дома; 5 - отель*/
    public int rent;  //рента
    public int full_group_rent;//рента при полном наборе цветовой группы
    public int rent_1_house;//рента с 1 домом
    public int rent_2_house;//рента с 2 домами
    public int rent_3_house;//рента с 3 домами
    public int rent_4_house;//рента с 4 домами
    public int rent_hotel;  //рента с отелем
    public int house_price; // стоимость покупки дома/отеля

    public Street(FieldTypes type,
                  /*Player owner,*/
                  PropTypes type1,
                  String name,
                  int price,
                  int colour,
                  int rent,
                  int full_group_rent,
                  int rent_1_house,
                  int rent_2_house,
                  int rent_3_house,
                  int rent_4_house,
                  int rent_hotel,
                  int house_price,
                  int depositPrice,
                  int redemptionPrice) {
        super(type, /*owner,*/ type1, price, depositPrice, redemptionPrice);
        this.name = name;
        this.colour = colour;
       /* this.houses = 0;*/
        this.rent = rent;
        this.full_group_rent = full_group_rent;
        this.rent_1_house = rent_1_house;
        this.rent_2_house = rent_2_house;
        this.rent_3_house = rent_3_house;
        this.rent_4_house = rent_4_house;
        this.rent_hotel = rent_hotel;
        this.house_price = house_price;
    }

   /* public void addHouse() {
        this.houses++;
    }

    public void reduceHouses() {
        this.houses--;
    }*/
}
