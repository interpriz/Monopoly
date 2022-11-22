package enums;

import entities.Field;
import entities.MunicipalEnterprise;
import entities.RailwayStation;
import entities.Street;

public enum Fields {
    forward(new Field(FieldTypes.forward)),
    publicTreasury(new Field(FieldTypes.publicTreasury)),
    chance(new Field(FieldTypes.chance)),
    goToPrison(new Field(FieldTypes.goToPrison)),
    parking(new Field(FieldTypes.parking)),
    inPrison(new Field(FieldTypes.inPrison)),
    incomeTax(new Field(FieldTypes.incomeTax)),
    superTax(new Field(FieldTypes.superTax)),
    Zhitnaya_St             (new Street(FieldTypes.property, /*null,*/ PropTypes.street, "Житная_Ул."            ,60 ,1,2 ,4  ,10 ,30 ,90  ,160 ,250 ,50 ,30 ,33 )),
    Nagatinskaya_St         (new Street(FieldTypes.property, /*null,*/ PropTypes.street, "Нагатинская_Ул."       ,60 ,1,4 ,8  ,20 ,60 ,180 ,320 ,450 ,50 ,30 ,33 )),
    Varshavs_shosse         (new Street(FieldTypes.property, /*null,*/ PropTypes.street, "Варшавское_шоссе"      ,100,2,6 ,12 ,30 ,90 ,270 ,400 ,550 ,50 ,50 ,55 )),
    Ul_Ogareva              (new Street(FieldTypes.property, /*null,*/ PropTypes.street, "Ул.Огарева"            ,100,2,6 ,12 ,30 ,90 ,270 ,400 ,550 ,50 ,50 ,55 )),
    First_park_St           (new Street(FieldTypes.property, /*null,*/ PropTypes.street, "Первая_Парковая_Ул."   ,120,2,8 ,16 ,40 ,100,300 ,450 ,600 ,50 ,60 ,66 )),
    Ul_polyanka             (new Street(FieldTypes.property, /*null,*/ PropTypes.street, "Ул.Полянка"            ,140,3,10,20 ,50 ,150,450 ,625 ,750 ,100,70 ,77 )),
    Ul_Sretenka             (new Street(FieldTypes.property, /*null,*/ PropTypes.street, "Ул.Сретенка"           ,140,3,10,20 ,50 ,150,450 ,625 ,750 ,100,70 ,77 )),
    Rostovska_nab           (new Street(FieldTypes.property, /*null,*/ PropTypes.street, "Ростовская_наб"        ,160,3,12,24 ,60 ,180,500 ,700 ,900 ,100,80 ,88 )),
    Riazanskiy_prospect     (new Street(FieldTypes.property, /*null,*/ PropTypes.street, "Рязанский_проспект"    ,180,4,14,28 ,70 ,200,550 ,750 ,950 ,100,90 ,99 )),
    Ul_Vavilova             (new Street(FieldTypes.property, /*null,*/ PropTypes.street, "Ул.Вавилова"           ,180,4,14,28 ,70 ,200,550 ,750 ,950 ,100,90 ,99 )),
    Rublevske_shosse        (new Street(FieldTypes.property, /*null,*/ PropTypes.street, "Рублевское_шоссе"      ,200,4,16,32 ,80 ,220,600 ,800 ,1000,100,100,110)),
    Ul_tverskaya            (new Street(FieldTypes.property, /*null,*/ PropTypes.street, "Ул.Тверская"           ,220,5,18,36 ,90 ,250,700 ,875 ,1050,150,110,121)),
    Ul_pushkinskaya         (new Street(FieldTypes.property, /*null,*/ PropTypes.street, "Ул.Пушкинская"         ,220,5,18,36 ,90 ,250,700 ,875 ,1050,150,110,121)),
    Mayakovsky_Square       (new Street(FieldTypes.property, /*null,*/ PropTypes.street, "Площадь_Маяковского"   ,240,5,20,40 ,100,300,750 ,925 ,1100,150,120,132)),
    Ul_Gruzinskiy_val       (new Street(FieldTypes.property, /*null,*/ PropTypes.street, "Ул.Грузинский_вал"     ,260,6,22,44 ,110,330,800 ,975 ,1150,150,130,143)),
    Ul_novinskiy_bulvar     (new Street(FieldTypes.property, /*null,*/ PropTypes.street, "Ул.Новинский_бульвар"  ,260,6,22,44 ,110,330,800 ,975 ,1150,150,130,143)),
    Ul_GSmolenska_ploschad  (new Street(FieldTypes.property, /*null,*/ PropTypes.street, "Ул.ГСмоленская_рлощадь",280,6,24,48 ,120,360,850 ,1025,1200,150,140,154)),
    ul_schuseva             (new Street(FieldTypes.property, /*null,*/ PropTypes.street, "Ул.Щусева"             ,300,7,26,52 ,130,390,900 ,1100,1275,200,150,165)),
    Gogols_Bulvar           (new Street(FieldTypes.property, /*null,*/ PropTypes.street, "Гоголевский_бульвар"   ,300,7,26,52 ,130,390,900 ,1100,1275,200,150,165)),
    Kutuzov_prospect        (new Street(FieldTypes.property, /*null,*/ PropTypes.street, "Кутузовский_проспект"  ,320,7,28,56 ,150,450,1000,1200,1400,200,160,176)),
    Small_bronnaya_Street   (new Street(FieldTypes.property, /*null,*/ PropTypes.street, "Ул.Малая_Бронная"      ,350,8,35,70 ,175,500,1100,1300,1500,200,175,193)),
    Ul_Arbat                (new Street(FieldTypes.property, /*null,*/ PropTypes.street, "Ул.Арбат"              ,400,8,50,100,200,600,1400,1700,2000,200,200,220)),

    Rizhska_RS              (new RailwayStation(FieldTypes.property, /*null,*/ PropTypes.station, "Рижская_ЖД"        , 200, 100, 110)),
    Kurska_RS               (new RailwayStation(FieldTypes.property, /*null,*/ PropTypes.station, "Курская_ЖД"        , 200, 100, 110)),
    Kazanska_RS             (new RailwayStation(FieldTypes.property, /*null,*/ PropTypes.station, "Казанская_ЖД"      , 200, 100, 110)),
    Leningradska_RS         (new RailwayStation(FieldTypes.property, /*null,*/ PropTypes.station, "Ленинградская_ЖД"  , 200, 100, 110)),

    Power_plant  (new MunicipalEnterprise(FieldTypes.property, /*null,*/ PropTypes.municipal,"Электростанция"    , 150, 75 , 83)),
    Water_supply (new MunicipalEnterprise(FieldTypes.property, /*null,*/ PropTypes.municipal,"Водопровод"        , 150, 75 , 83));

    private Field value;

    private Fields(Field value) {
        this.value = value;
    }


    public Field getValue(){
        return value;
    }
}
