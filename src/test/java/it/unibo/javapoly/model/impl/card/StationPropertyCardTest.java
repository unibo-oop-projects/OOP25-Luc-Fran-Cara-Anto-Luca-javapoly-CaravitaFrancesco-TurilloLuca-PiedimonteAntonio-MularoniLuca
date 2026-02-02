package it.unibo.javapoly.model.impl.card;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import it.unibo.javapoly.model.api.RentContext;

class StationPropertyCardTest {

    private static final String ID = "S1";
    private static final String NAME = "Stazione Centrale";
    private static final String DESCRIPTION = "Stazione principale";
    private static final String GROUP = "STATION";

    private static final int PROPERTY_COST = 200;
    private static final List<Integer> STATION_RENTS = List.of(25, 50, 100, 200);

    private StationPropertyCard createStation() {
        return new StationPropertyCard(ID, NAME, DESCRIPTION, PROPERTY_COST, GROUP, STATION_RENTS);
    }

    @Test
    void rentWithOneStation() {
        final StationPropertyCard card = createStation();
        final RentContext ctx = RentContext.forStation(1);
        assertEquals(STATION_RENTS.get(0), card.calculateRent(ctx), "1 station must return first rent value");
    }

    @Test
    void rentWithTwoStations() {
        final StationPropertyCard card = createStation();
        final RentContext ctx = RentContext.forStation(2);
        assertEquals(STATION_RENTS.get(1), card.calculateRent(ctx), "2 stations must return second rent value");
    }

    @Test
    void rentWithFourStations() {
        final StationPropertyCard card = createStation();
        final RentContext ctx = RentContext.forStation(4);
        assertEquals(STATION_RENTS.get(3), card.calculateRent(ctx), "4 stations must return last rent value");
    }
}
