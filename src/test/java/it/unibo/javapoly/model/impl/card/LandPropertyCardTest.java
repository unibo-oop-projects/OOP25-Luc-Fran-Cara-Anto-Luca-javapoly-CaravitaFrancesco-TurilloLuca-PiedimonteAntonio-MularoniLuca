package it.unibo.javapoly.model.impl.card;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import it.unibo.javapoly.model.api.RentContext;
import it.unibo.javapoly.model.api.property.PropertyGroup;

class LandPropertyCardTest {

    private static final String ID = "L1";
    private static final String NAME = "Via Roma";
    private static final String DESCRIPTION = "Bella strada centrale";
    private static final PropertyGroup GROUP = PropertyGroup.DARK_BLUE;

    private static final int PROPERTY_COST = 100;
    private static final int BASE_RENT = 10;
    private static final List<Integer> HOUSE_RENTS = List.of(30, 160, 250);
    private static final int HOTEL_RENT = 900;
    private static final int HOUSE_PRICE = 50;
    private static final int HOTEL_PRICE = 250;

    @Test
    void baseRentWhenNoHouses() {
        final LandPropertyCard card = new LandPropertyCard(
            ID,
            NAME,
            DESCRIPTION,
            PROPERTY_COST,
            GROUP,
            BASE_RENT,
            HOUSE_RENTS,
            HOTEL_RENT,
            HOUSE_PRICE,
            HOTEL_PRICE
        );

        final RentContext ctx = RentContext.forLand(0, false);
        assertEquals(BASE_RENT, card.calculateRent(ctx), "calculateRent with 0 houses -> base rent");
    }

    @Test
    void houseRentsWhenOneOrTwoHouses() {
        final LandPropertyCard card = new LandPropertyCard(
            ID,
            NAME,
            DESCRIPTION,
            PROPERTY_COST,
            GROUP,
            BASE_RENT,
            HOUSE_RENTS,
            HOTEL_RENT,
            HOUSE_PRICE,
            HOTEL_PRICE
        );

        final RentContext ctx1 = RentContext.forLand(1, false);
        final RentContext ctx2 = RentContext.forLand(2, false);

        assertEquals(HOUSE_RENTS.get(0), card.calculateRent(ctx1), "1 house rent must match");
        assertEquals(HOUSE_RENTS.get(1), card.calculateRent(ctx2), "2 houses rent must match");
    }

    @Test
    void hotelRentWhenHotelPresent() {
        final LandPropertyCard card = new LandPropertyCard(
            ID,
            NAME,
            DESCRIPTION,
            PROPERTY_COST,
            GROUP,
            BASE_RENT,
            HOUSE_RENTS,
            HOTEL_RENT,
            HOUSE_PRICE,
            HOTEL_PRICE
        );

        final int hotelIndicator = HOUSE_RENTS.size() + 1; // houses count that represents hotel
        final RentContext hotelCtx = RentContext.forLand(hotelIndicator, false);
        assertEquals(HOTEL_RENT, card.calculateRent(hotelCtx), "hotel rent must match the hotel value");
    }
}
