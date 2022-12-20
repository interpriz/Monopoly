package services;

import java.util.ArrayList;
import java.util.stream.Collectors;

import entities.Field;
import entities.Game;
import entities.MunicipalEnterprise;
import entities.Player;
import entities.Property;
import entities.RailwayStation;
import entities.Street;
import enums.FieldTypes;
import static enums.Fields.*;
import enums.PropTypes;


public class MapService {


    public final ArrayList<Field> map = new ArrayList<>();


    public MapService() {
        this.map.add(forward.getValue());
        this.map.add(Zhitnaya_St.getValue());
        this.map.add(publicTreasury.getValue());
        this.map.add(Nagatinskaya_St.getValue());
        this.map.add(incomeTax.getValue());
        this.map.add(Rizhska_RS.getValue());
        this.map.add(Varshavs_shosse.getValue());
        this.map.add(chance.getValue());
        this.map.add(Ul_Ogareva.getValue());
        this.map.add(First_park_St.getValue());
        this.map.add(inPrison.getValue());
        this.map.add(Ul_polyanka.getValue());
        this.map.add(Power_plant.getValue());
        this.map.add(Ul_Sretenka.getValue());
        this.map.add(Rostovska_nab.getValue());
        this.map.add(Kurska_RS.getValue());
        this.map.add(Riazanskiy_prospect.getValue());
        this.map.add(publicTreasury.getValue());
        this.map.add(Ul_Vavilova.getValue());
        this.map.add(Rublevske_shosse.getValue());
        this.map.add(parking.getValue());
        this.map.add(Ul_tverskaya.getValue());
        this.map.add(chance.getValue());
        this.map.add(Ul_pushkinskaya.getValue());
        this.map.add(Mayakovsky_Square.getValue());
        this.map.add(Kazanska_RS.getValue());
        this.map.add(Ul_Gruzinskiy_val.getValue());
        this.map.add(Ul_novinskiy_bulvar.getValue());
        this.map.add(Water_supply.getValue());
        this.map.add(Ul_GSmolenska_ploschad.getValue());
        this.map.add(goToPrison.getValue());
        this.map.add(ul_schuseva.getValue());
        this.map.add(Gogols_Bulvar.getValue());
        this.map.add(publicTreasury.getValue());
        this.map.add(Kutuzov_prospect.getValue());
        this.map.add(Leningradska_RS.getValue());
        this.map.add(chance.getValue());
        this.map.add(Small_bronnaya_Street.getValue());
        this.map.add(superTax.getValue());
        this.map.add(Ul_Arbat.getValue());
    }

    private static MapService INSTANCE;

    public static MapService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MapService();
        }
        return INSTANCE;
    }

    public  ArrayList<Property> getProperties() {
        return map.stream().
                filter(x -> x.getType() == FieldTypes.property)
                .map(x -> (Property) x)
                .collect(Collectors.toCollection(ArrayList::new));
    }


    public  ArrayList<Street> getStreets() {
        return getProperties().stream()
                .filter(x -> x.type == PropTypes.street)
                .map(x -> (Street) x)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public  ArrayList<RailwayStation> getRailwayStations() {
        return getProperties().stream()
                .filter(x -> x.type == PropTypes.station)
                .map(x -> (RailwayStation) x)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public  ArrayList<MunicipalEnterprise> getMunicipalEnterprises() {
        return getProperties().stream()
                .filter(x -> x.type == PropTypes.municipal)
                .map(x -> (MunicipalEnterprise) x)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public  Property getPropertyByPosition(int pos) {
        Field field = getFieldByPosition(pos);
        if (field.getType() == FieldTypes.property)
            return (Property) field;
        else return null;
    }

    public  String getPropertyNameByPosition(int pos) {
        Field field = getFieldByPosition(pos);
        if (field.getType() == FieldTypes.property){
            Property property = (Property) field;
            return getPropertyName(property);
        }
        else return "";
    }

    public  String getPropertyName(Property property) {
            switch (property.type){
                case street:
                    Street street = (Street) property;
                    return street.name;
                case station:
                    RailwayStation station = (RailwayStation) property;
                    return station.name;
                case municipal:
                    MunicipalEnterprise municipal = (MunicipalEnterprise) property;
                    return municipal.name;
                default:
                    return "";
            }
    }



    public Field getFieldByPosition(int pos) {
        return map.get(pos);
    }
}
