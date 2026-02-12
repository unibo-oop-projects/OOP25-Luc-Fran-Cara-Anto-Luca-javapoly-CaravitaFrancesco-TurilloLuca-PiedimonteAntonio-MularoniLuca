package it.unibo.javapoly.model.player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.javapoly.model.api.Player;
import it.unibo.javapoly.model.api.PlayerObserver;
import it.unibo.javapoly.model.api.PlayerState;
import it.unibo.javapoly.model.api.TokenType;
import it.unibo.javapoly.model.impl.PlayerImpl;

/**
 * Comprehensive test suite for {@link PlayerImpl}.
 */
class PlayerImplTest {

    private static final int DEFAULT_BALANCE = 1_500;
    private static final int CUSTOM_BALANCE = 3_000;
    private static final int FULL_BALANCE = 2_000;
    private static final int PAY_AMOUNT_500 = 500;
    private static final int PAY_AMOUNT_300 = 300;
    private static final int PAY_AMOUNT_200 = 200;
    private static final int PAY_AMOUNT_100 = 100;
    private static final int RECEIVE_AMOUNT_200 = 200;
    private static final int RECEIVE_AMOUNT_300 = 300;
    private static final int RECEIVE_AMOUNT_100 = 100;
    private static final int RECEIVE_AMOUNT_500 = 500;
    private static final int NEGATIVE_AMOUNT_100 = -100;
    private static final int NEGATIVE_AMOUNT_50 = -50;
    private static final int POSITION_3 = 3;
    private static final int POSITION_5 = 5;
    private static final int POSITION_7 = 7;
    private static final int POSITION_10 = 10;
    private static final int POSITION_15 = 15;
    private static final String PLAYER_NAME = "TestPlayer";
    private static final TokenType DEFAULT_TOKEN = TokenType.CAR;
    private static final String CUSTOM_TOKEN_PATH = "/path/to/custom/token.png";
    private static final String FULL_TOKEN_PATH = "/path/to/token.png";
    private static final String NEW_TOKEN_PATH = "/new/path.png";
    private static final String GENERIC_TOKEN_PATH = "/path.png";
    private static final String GENERIC_PLAYER_NAME = "Player";

    private Player player;

    /**
     * Sets up the test fixture.
     */
    @BeforeEach
    void setUp() {
        player = new PlayerImpl(PLAYER_NAME, DEFAULT_TOKEN);
    }

    /**
     * Tests that the default constructor sets the correct name.
     */
    @Test
    void testDefaultConstructorSetsCorrectName() {
        assertEquals(PLAYER_NAME, player.getName());
    }

    /**
     * Tests that the default constructor sets the default balance.
     */
    @Test
    void testDefaultConstructorSetsDefaultBalance() {
        assertEquals(DEFAULT_BALANCE, player.getBalance());
    }

    /**
     * Tests that the default constructor sets the token type.
     */
    @Test
    void testDefaultConstructorSetsTokenType() {
        assertEquals(DEFAULT_TOKEN, player.getTokenType());
    }

    /**
     * Tests that the default constructor sets position to zero.
     */
    @Test
    void testDefaultConstructorSetsPositionToZero() {
        assertEquals(0, player.getCurrentPosition());
    }

    /**
     * Tests that the default constructor creates a token.
     */
    @Test
    void testDefaultConstructorCreatesToken() {
        assertNotNull(player.getToken());
    }

    /**
     * Tests that the default constructor sets the initial state.
     */
    @Test
    void testDefaultConstructorSetsInitialState() {
        assertNotNull(player.getState());
    }

    /**
     * Tests the custom balance constructor.
     */
    @Test
    void testCustomBalanceConstructor() {
        final Player customPlayer = new PlayerImpl(PLAYER_NAME, CUSTOM_BALANCE, DEFAULT_TOKEN, null);
        assertEquals(CUSTOM_BALANCE, customPlayer.getBalance());
    }

    /**
     * Tests the custom token path constructor.
     */
    @Test
    void testCustomTokenPathConstructor() {
        final Player customPlayer = new PlayerImpl(PLAYER_NAME, DEFAULT_TOKEN, CUSTOM_TOKEN_PATH);
        assertEquals(CUSTOM_TOKEN_PATH, customPlayer.getCustomTokenPath());
        assertEquals(DEFAULT_BALANCE, customPlayer.getBalance());
    }

    /**
     * Tests the full constructor with a custom token path.
     */
    @Test
    void testFullConstructorWithCustomTokenPath() {
        final Player customPlayer = new PlayerImpl(PLAYER_NAME, FULL_BALANCE, TokenType.CUSTOM, FULL_TOKEN_PATH);
        assertEquals(PLAYER_NAME, customPlayer.getName());
        assertEquals(FULL_BALANCE, customPlayer.getBalance());
        assertEquals(TokenType.CUSTOM, customPlayer.getTokenType());
        assertEquals(FULL_TOKEN_PATH, customPlayer.getCustomTokenPath());
    }

    /**
     * Tests that the constructor throws on null name.
     */
    @Test
    void testConstructorThrowsOnNullName() {
        assertThrows(NullPointerException.class, () -> new PlayerImpl(null, DEFAULT_TOKEN));
    }

    /**
     * Tests that the constructor throws on null token type.
     */
    @Test
    void testConstructorThrowsOnNullTokenType() {
        assertThrows(NullPointerException.class, () -> new PlayerImpl(PLAYER_NAME, null));
    }

    /**
     * Tests that the constructor throws on blank name.
     */
    @Test
    void testConstructorThrowsOnBlankName() {
        assertThrows(IllegalArgumentException.class, () -> new PlayerImpl("   ", DEFAULT_TOKEN));
    }

    /**
     * Tests that the constructor throws on empty name.
     */
    @Test
    void testConstructorThrowsOnEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> new PlayerImpl("", DEFAULT_TOKEN));
    }

    /**
     * Tests that the constructor throws on negative balance.
     */
    @Test
    void testConstructorThrowsOnNegativeBalance() {
        assertThrows(IllegalArgumentException.class, () -> new PlayerImpl(PLAYER_NAME, -1, DEFAULT_TOKEN, null));
    }

    /**
     * Tests that the constructor accepts zero balance.
     */
    @Test
    void testConstructorAcceptsZeroBalance() {
        final Player zeroBalancePlayer = new PlayerImpl(PLAYER_NAME, 0, DEFAULT_TOKEN, null);
        assertEquals(0, zeroBalancePlayer.getBalance());
    }

    /**
     * Tests that move updates position.
     */
    @Test
    void testMoveUpdatesPosition() {
        player.move(POSITION_5);
        assertEquals(POSITION_5, player.getCurrentPosition());
    }

    /**
     * Tests moving to multiple positions.
     */
    @Test
    void testMoveToMultiplePositions() {
        player.move(POSITION_3);
        assertEquals(POSITION_3, player.getCurrentPosition());
        player.move(POSITION_10);
        assertEquals(POSITION_10, player.getCurrentPosition());
    }

    /**
     * Tests that move throws on negative position.
     */
    @Test
    void testMoveThrowsOnNegativePosition() {
        assertThrows(IllegalArgumentException.class, () -> player.move(-1));
    }

    /**
     * Tests moving to zero.
     */
    @Test
    void testMoveToZero() {
        player.move(POSITION_5);
        player.move(0);
        assertEquals(0, player.getCurrentPosition());
    }

    /**
     * Tests setPosition.
     */
    @Test
    void testSetPosition() {
        player.setPosition(POSITION_15);
        assertEquals(POSITION_15, player.getCurrentPosition());
    }

    /**
     * Tests tryToPay succeeds when sufficient funds.
     */
    @Test
    void testTryToPaySucceedsWhenSufficientFunds() {
        assertTrue(player.tryToPay(PAY_AMOUNT_500));
        assertEquals(DEFAULT_BALANCE - PAY_AMOUNT_500, player.getBalance());
    }

    /**
     * Tests tryToPay fails when insufficient funds.
     */
    @Test
    void testTryToPayFailsWhenInsufficientFunds() {
        assertFalse(player.tryToPay(DEFAULT_BALANCE + 1));
        assertEquals(DEFAULT_BALANCE, player.getBalance());
    }

    /**
     * Tests tryToPay with exact balance.
     */
    @Test
    void testTryToPayExactBalance() {
        assertTrue(player.tryToPay(DEFAULT_BALANCE));
        assertEquals(0, player.getBalance());
    }

    /**
     * Tests tryToPay with zero amount.
     */
    @Test
    void testTryToPayZeroAmount() {
        assertTrue(player.tryToPay(0));
        assertEquals(DEFAULT_BALANCE, player.getBalance());
    }

    /**
     * Tests tryToPay throws on negative amount.
     */
    @Test
    void testTryToPayThrowsOnNegativeAmount() {
        assertThrows(IllegalArgumentException.class, () -> player.tryToPay(NEGATIVE_AMOUNT_100));
    }

    /**
     * Tests receiveMoney.
     */
    @Test
    void testReceiveMoney() {
        player.receiveMoney(RECEIVE_AMOUNT_200);
        assertEquals(DEFAULT_BALANCE + RECEIVE_AMOUNT_200, player.getBalance());
    }

    /**
     * Tests receiving zero money.
     */
    @Test
    void testReceiveZeroMoney() {
        player.receiveMoney(0);
        assertEquals(DEFAULT_BALANCE, player.getBalance());
    }

    /**
     * Tests receiveMoney throws on negative amount.
     */
    @Test
    void testReceiveMoneyThrowsOnNegativeAmount() {
        assertThrows(IllegalArgumentException.class, () -> player.receiveMoney(NEGATIVE_AMOUNT_50));
    }

    /**
     * Tests multiple transactions.
     */
    @Test
    void testMultipleTransactions() {
        player.receiveMoney(RECEIVE_AMOUNT_500);
        player.tryToPay(PAY_AMOUNT_300);
        player.receiveMoney(RECEIVE_AMOUNT_100);
        player.tryToPay(PAY_AMOUNT_200);
        assertEquals(
                DEFAULT_BALANCE + RECEIVE_AMOUNT_500 - PAY_AMOUNT_300 + RECEIVE_AMOUNT_100 - PAY_AMOUNT_200,
                player.getBalance());
    }

    /**
     * Tests that setState changes state.
     */
    @Test
    void testSetStateChangesState() {
        final PlayerState originalState = player.getState();
        assertNotNull(originalState);
        player.setState(originalState);
        assertEquals(originalState, player.getState());
    }

    /**
     * Tests that setState throws on null.
     */
    @Test
    void testSetStateThrowsOnNull() {
        assertThrows(NullPointerException.class, () -> player.setState(null));
    }

    /**
     * Tests setCustomTokenPath.
     */
    @Test
    void testSetCustomTokenPath() {
        player.setCustomTokenPath(NEW_TOKEN_PATH);
        assertEquals(NEW_TOKEN_PATH, player.getCustomTokenPath());
    }

    /**
     * Tests that the custom token path default is null.
     */
    @Test
    void testCustomTokenPathDefaultIsNull() {
        final Player p = new PlayerImpl(PLAYER_NAME, DEFAULT_TOKEN);
        assertNull(p.getCustomTokenPath());
    }

    /**
     * Tests that observer is notified on move.
     */
    @Test
    void testObserverNotifiedOnMove() {
        final boolean[] notified = {false};
        final int[] capturedOld = {-1};
        final int[] capturedNew = {-1};

        player.addObserver(new TestObserver() {
            @Override
            public void onPlayerMoved(final Player p, final int oldPos, final int newPos) {
                notified[0] = true;
                capturedOld[0] = oldPos;
                capturedNew[0] = newPos;
            }
        });

        player.move(POSITION_7);
        assertTrue(notified[0], "Observer should be notified on move");
        assertEquals(0, capturedOld[0]);
        assertEquals(POSITION_7, capturedNew[0]);
    }

    /**
     * Tests that observer is notified on payment.
     */
    @Test
    void testObserverNotifiedOnPayment() {
        final boolean[] notified = {false};
        final int[] capturedBalance = {-1};

        player.addObserver(new TestObserver() {
            @Override
            public void onBalanceChanged(final Player p, final int newBalance) {
                notified[0] = true;
                capturedBalance[0] = newBalance;
            }
        });

        player.tryToPay(PAY_AMOUNT_100);
        assertTrue(notified[0], "Observer should be notified on payment");
        assertEquals(DEFAULT_BALANCE - PAY_AMOUNT_100, capturedBalance[0]);
    }

    /**
     * Tests that observer is not notified on failed payment.
     */
    @Test
    void testObserverNotNotifiedOnFailedPayment() {
        final boolean[] notified = {false};

        player.addObserver(new TestObserver() {
            @Override
            public void onBalanceChanged(final Player p, final int newBalance) {
                notified[0] = true;
            }
        });

        player.tryToPay(DEFAULT_BALANCE + 1);
        assertFalse(notified[0], "Observer should not be notified on failed payment");
    }

    /**
     * Tests that observer is notified on receiving money.
     */
    @Test
    void testObserverNotifiedOnReceiveMoney() {
        final boolean[] notified = {false};
        final int[] capturedBalance = {-1};

        player.addObserver(new TestObserver() {
            @Override
            public void onBalanceChanged(final Player p, final int newBalance) {
                notified[0] = true;
                capturedBalance[0] = newBalance;
            }
        });

        player.receiveMoney(RECEIVE_AMOUNT_300);
        assertTrue(notified[0], "Observer should be notified when receiving money");
        assertEquals(DEFAULT_BALANCE + RECEIVE_AMOUNT_300, capturedBalance[0]);
    }

    /**
     * Tests that removing an observer stops notifications.
     */
    @Test
    void testRemoveObserverStopsNotifications() {
        final boolean[] notified = {false};

        final PlayerObserver observer = new TestObserver() {
            @Override
            public void onPlayerMoved(final Player p, final int oldPos, final int newPos) {
                notified[0] = true;
            }
        };

        player.addObserver(observer);
        player.removeObserver(observer);
        player.move(POSITION_5);
        assertFalse(notified[0], "Removed observer should not be notified");
    }

    /**
     * Tests that multiple observers are notified.
     */
    @Test
    void testMultipleObserversNotified() {
        final boolean[] notified1 = {false};
        final boolean[] notified2 = {false};

        player.addObserver(new TestObserver() {
            @Override
            public void onPlayerMoved(final Player p, final int oldPos, final int newPos) {
                notified1[0] = true;
            }
        });

        player.addObserver(new TestObserver() {
            @Override
            public void onPlayerMoved(final Player p, final int oldPos, final int newPos) {
                notified2[0] = true;
            }
        });

        player.move(POSITION_3);
        assertTrue(notified1[0], "First observer should be notified");
        assertTrue(notified2[0], "Second observer should be notified");
    }

    /**
     * Tests that adding a null observer throws.
     */
    @Test
    void testAddNullObserverThrows() {
        assertThrows(NullPointerException.class, () -> player.addObserver(null));
    }

    /**
     * Tests that removing a null observer throws.
     */
    @Test
    void testRemoveNullObserverThrows() {
        assertThrows(NullPointerException.class, () -> player.removeObserver(null));
    }

    /**
     * Tests that playTurn throws on negative destination.
     */
    @Test
    void testPlayTurnThrowsOnNegativeDestination() {
        assertThrows(IllegalArgumentException.class, () -> player.playTurn(-1, false));
    }

    /**
     * Tests playTurn with a valid destination.
     */
    @Test
    void testPlayTurnWithValidDestination() {
        player.playTurn(POSITION_5, false);
    }

    /**
     * Tests playTurn with a double.
     */
    @Test
    void testPlayTurnWithDouble() {
        player.playTurn(POSITION_10, true);
    }

    /**
     * Tests that toString contains the player name.
     */
    @Test
    void testToStringContainsName() {
        final String result = player.toString();
        assertTrue(result.contains(PLAYER_NAME), "toString should contain the player's name");
    }

    /**
     * Tests that toString contains the balance.
     */
    @Test
    void testToStringContainsBalance() {
        final String result = player.toString();
        assertTrue(result.contains(String.valueOf(DEFAULT_BALANCE)),
                "toString should contain the balance");
    }

    /**
     * Tests different token types.
     */
    @Test
    void testDifferentTokenTypes() {
        for (final TokenType type : TokenType.values()) {
            if (type == TokenType.CUSTOM) {
                final Player p = new PlayerImpl(GENERIC_PLAYER_NAME, type, GENERIC_TOKEN_PATH);
                assertEquals(type, p.getTokenType());
            } else {
                final Player p = new PlayerImpl(GENERIC_PLAYER_NAME, type);
                assertEquals(type, p.getTokenType());
                assertNotNull(p.getToken());
            }
        }
    }

    /**
     * A no-op implementation of {@link PlayerObserver} for testing purposes.
     * Subclasses override specific methods to capture notifications.
     */
    private static class TestObserver implements PlayerObserver {
        @Override
        public void onPlayerMoved(final Player player, final int oldPosition, final int newPosition) {
            // No-op by default
        }

        @Override
        public void onBalanceChanged(final Player player, final int newBalance) {
            // No-op by default
        }

        @Override
        public void onStateChanged(final Player player, final PlayerState oldState, final PlayerState newState) {
            // No-op by default
        }
    }
}
