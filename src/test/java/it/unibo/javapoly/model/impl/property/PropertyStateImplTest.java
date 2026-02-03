package it.unibo.javapoly.model.impl.property;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PropertyStateImplTest {

    private static final int PURCHASE_PRICE = 250;
    private static final int HOTEL_CAP = 5;
    private static final int SOME_PRICE = 120;

    @Test
    void initialStateHasBankOwnerZeroHousesAndPurchasePriceSet() {
        final PropertyStateImpl s = new PropertyStateImpl(PURCHASE_PRICE);
        assertEquals("Bank", s.getOwnerId());
        assertEquals(0, s.getHouses());
        assertEquals(PURCHASE_PRICE, s.getPurchasePrice());
        assertFalse(s.isOwnedByPlayer());
    }

    @Test
    void copyConstructorCreatesIndependentCopy() {
        final PropertyStateImpl original = new PropertyStateImpl(300);
        original.setNewOwnerID("p1");
        original.setHouse(2);

        final PropertyStateImpl copy = new PropertyStateImpl(original);
        // content copied
        assertEquals(original.getOwnerId(), copy.getOwnerId());
        assertEquals(original.getHouses(), copy.getHouses());
        assertEquals(original.getPurchasePrice(), copy.getPurchasePrice());

        // mutate original -> copy remains unchanged
        original.setHouse(4);
        original.setNewOwnerID("p2");
        assertNotEquals(original.getOwnerId(), copy.getOwnerId());
        assertNotEquals(original.getHouses(), copy.getHouses());
    }

    @Test
    void setNewOwnerBankIsNewOwnerAndIsOwnedByPlayer() {
        final PropertyStateImpl s = new PropertyStateImpl(150);
        s.setNewOwnerID("player-123");
        assertEquals("player-123", s.getOwnerId());
        assertTrue(s.isOwnedByPlayer());

        s.bankIsNewOwnerID();
        assertEquals("Bank", s.getOwnerId());
        assertFalse(s.isOwnedByPlayer());
        // bankIsNewOwnerID should reset houses to 0 as implemented
        assertEquals(0, s.getHouses());
    }

    @Test
    void setHouseAcceptsValidValuesCapsAtHotelAndRejectsNegative() {
        final PropertyStateImpl s = new PropertyStateImpl(SOME_PRICE);
        s.setHouse(3);
        assertEquals(3, s.getHouses());

        // values >= HOTEL_CAP are capped
        s.setHouse(HOTEL_CAP + 10);
        assertEquals(HOTEL_CAP, s.getHouses(), "houses should be capped to HOTEL_CAP");

        // negative -> IllegalArgumentException
        final PropertyStateImpl s2 = new PropertyStateImpl(10);
        assertThrows(IllegalArgumentException.class, () -> s2.setHouse(-1));
    }

    @Test
    void addHouseAndRemoveHouseBehaviourWithBounds() {
        final PropertyStateImpl s = new PropertyStateImpl(200);
        // initial 0
        assertFalse(s.removeHouse(), "cannot remove when houses == 0");
        assertTrue(s.addHouse(), "adding first house succeeds");
        assertEquals(1, s.getHouses());

        // add up to hotel
        s.setHouse(HOTEL_CAP - 1);
        assertTrue(s.addHouse(), "adding HOTEL_CAP should produce hotel / be allowed");
        assertEquals(HOTEL_CAP, s.getHouses());
        // now at max, addHouse returns false
        assertFalse(s.addHouse(), "cannot add beyond hotel");

        // remove house reduces count
        assertTrue(s.removeHouse());
        assertEquals(HOTEL_CAP - 1, s.getHouses());
    }
}
