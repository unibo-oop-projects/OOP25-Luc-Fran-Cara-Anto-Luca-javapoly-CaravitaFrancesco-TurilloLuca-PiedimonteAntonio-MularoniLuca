package it.unibo.javapoly.model.impl.property;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import it.unibo.javapoly.model.api.RentContext;
import it.unibo.javapoly.model.api.property.PropertyGroup;
import it.unibo.javapoly.model.impl.Card.AbstractPropertyCard;


class PropertyImplTest {

    private static final int FIXED_RENT = 777;
    private static final int NUM_SEVEN = 7;
    private static final PropertyGroup GROUP_BLUE = PropertyGroup.DARK_BLUE;
    private static final int PURCHASE_PRICE = 180;
    private static final String BANK = "Bank";
    private static final String OWNER_BOB = "bob";

    private static AbstractPropertyCard testCard(final PropertyGroup group, final int purchasePrice) {
        return new AbstractPropertyCard("c1", "Test", "desc", purchasePrice, group) {
            @Override
            public int calculateRent(final RentContext ctx) {
                return FIXED_RENT;
            }
        };
    }

    @Test
    void gettersReturnExpectedValuesAndGetRentDelegatesToCard() {
        final AbstractPropertyCard card = testCard(GROUP_BLUE, PURCHASE_PRICE);
        final PropertyImpl p = new PropertyImpl("prop1", 7, card);

        assertEquals("prop1", p.getId());
        assertEquals(NUM_SEVEN, p.getPosition());
        assertSame(card, p.getCard());
        assertEquals(PURCHASE_PRICE, p.getPurchasePrice());

        final PropertyStateImpl view = (PropertyStateImpl) p.getState();
        assertEquals(BANK, view.getOwnerId());
        assertEquals(0, view.getHouses());

        final int rent = p.getRent(null);
        assertEquals(FIXED_RENT, rent);
    }

    @Test
    void assignOwnerAndClearOwnerModifyStateCorrectly() {
        final AbstractPropertyCard card = testCard(GROUP_BLUE, 100);
        final PropertyImpl p = new PropertyImpl("p2", 3, card);

        assertEquals(BANK, p.getState().getOwnerId());

        final boolean assigned = p.assignOwner("player-1");
        assertTrue(assigned);
        final PropertyStateImpl stateAfter = (PropertyStateImpl) p.getState();
        assertEquals("player-1", stateAfter.getOwnerId());
        assertTrue(stateAfter.isOwnedByPlayer());

        assertFalse(p.assignOwner("player-2"));

        p.clearOwner();
        assertEquals(BANK, p.getState().getOwnerId());
    }

    @Test
    void buildHouseWhenOwnerBuildsThenHouseAdded() {
        final AbstractPropertyCard card = testCard(GROUP_BLUE, 50);
        final PropertyImpl p = new PropertyImpl("p3", 4, card);

        assertTrue(p.assignOwner("own-1"));
        assertTrue(p.buildHouse("own-1"));
        assertEquals(1, p.getState().getHouses());
    }

    @Test
    void buildHouseByNonOwnerThrows() {
        final AbstractPropertyCard card = testCard(GROUP_BLUE, 60);
        final PropertyImpl p = new PropertyImpl("p4", 5, card);

        assertTrue(p.assignOwner("owner-x"));
        assertThrows(IllegalStateException.class, () -> p.buildHouse("other-player"));
    }

    @Test
    void destroyHouseBehaviour() {
        final AbstractPropertyCard card = testCard(GROUP_BLUE, 120);
        final PropertyImpl p = new PropertyImpl("p5", 9, card);

        assertTrue(p.assignOwner(OWNER_BOB));
        assertTrue(p.buildHouse(OWNER_BOB));
        assertEquals(1, p.getState().getHouses());

        assertTrue(p.destroyHouse(OWNER_BOB));
        assertEquals(0, p.getState().getHouses());

        assertFalse(p.destroyHouse(OWNER_BOB));
    }
}
