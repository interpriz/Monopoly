package com.example.monopoly;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import java.util.ArrayList;

import entities.MunicipalEnterprise;
import entities.Property;
import entities.RailwayStation;
import entities.Street;
import enums.Fields;
import services.MapService;

public class MapServiceTest {

    private final MapService mapService = MapService.getInstance();

    @Test
    public void MapService_test(){
        ArrayList<Property> properties = mapService.getProperties();
        assertFalse(properties.isEmpty());

        ArrayList<Street> streets = mapService.getStreets();
        assertFalse(streets.isEmpty());

        ArrayList<RailwayStation> railwayStations = mapService.getRailwayStations();
        assertFalse(railwayStations.isEmpty());

        ArrayList<MunicipalEnterprise> municipals = mapService.getMunicipalEnterprises();
        assertFalse(municipals.isEmpty());

        Property property = mapService.getPropertyByPosition(1);
        assertEquals(Fields.Zhitnaya_St.getValue(),property);

        property = mapService.getPropertyByPosition(0);
        assertNull(property);

    }
}
