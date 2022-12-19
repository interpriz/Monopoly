package entities;

import enums.FieldTypes;
import enums.PropTypes;

public class RailwayStation extends Property {
    public String name;

    public RailwayStation(
            FieldTypes type,
            /*Player owner,*/
            PropTypes type1,
            String name,
            int price,
            int depositPrice,
            int redemptionPrice
            ) {
        super(type,/* owner,*/ type1, price, depositPrice, redemptionPrice);
        this.name = name;
    }
}

