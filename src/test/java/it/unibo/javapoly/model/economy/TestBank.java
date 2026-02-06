package it.unibo.javapoly.model.economy;

import it.unibo.javapoly.model.api.TokenType;
import it.unibo.javapoly.model.api.economy.Bank;
import it.unibo.javapoly.model.api.Player;
import it.unibo.javapoly.model.impl.PlayerImpl;
import it.unibo.javapoly.model.impl.economy.BankImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestBank {

    private static final int INITIAL_BALANCE = 1500;
    private static final int DEPOSIT_AMOUNT = 200;
    private static final int WITHDRAW_AMOUNT = 100;
    private static final int TRANSFER_AMOUNT = 300;

    private Bank bank;
    private Player player1;
    private Player player2;

    /**
     * Sets up one Bank instance and two Player instances before each test,
     * two Player instances has same initial balance but different type of token.
     */
    @BeforeEach
    void setUp() {
        this.bank = new BankImpl();
        this.player1 = new PlayerImpl("Mario", INITIAL_BALANCE, TokenType.CAR);
        this.player2 = new PlayerImpl("Luigi", INITIAL_BALANCE, TokenType.DOG);
    }

    /**
     * Tests the deposit of positive amount on a player.
     */
    @Test
    void testDepositMoneySuccessfully(){
        bank.deposit(player1, DEPOSIT_AMOUNT);
        assertEquals(INITIAL_BALANCE+DEPOSIT_AMOUNT, player1.getBalance());
    }

    /**
     * Tests deposit of negative amount on a player.
     */
    @Test
    void testDepositMoneyNegative(){
        assertThrows(IllegalArgumentException.class, () -> bank.deposit(player1, -DEPOSIT_AMOUNT));
    }

    /**
     * Tests withdraw with positive amount lower than player balance.
     */
    @Test
    void testWithdrawMoneySuccessfully(){
        final boolean result = bank.withdraw(player1, WITHDRAW_AMOUNT);
        assertTrue(result);
        assertEquals(INITIAL_BALANCE - WITHDRAW_AMOUNT, player1.getBalance());
    }

    /**
     * Tests withdraw with negative amount.
     */
    @Test
    void testWithdrawMoneyNegative(){
        assertThrows(IllegalArgumentException.class, () -> bank.withdraw(player1, -WITHDRAW_AMOUNT));
    }

    /**
     * Tests withdraw with positive amount greater than player balance.
     */
    @Test
    void testWithdrawMoneyGreaterThanBalance(){
        final  boolean result = bank.withdraw(player1, INITIAL_BALANCE+1);
        assertFalse(result);
        assertEquals(INITIAL_BALANCE, player1.getBalance());
    }

    /**
     * Tests transfer funds with positive amount lower than the player1's balance.
     */
    @Test
    void testTransferFundsSuccessfully(){
        final boolean result = bank.transferFunds(player1, player2, TRANSFER_AMOUNT);
        assertTrue(result);
        assertEquals(INITIAL_BALANCE - TRANSFER_AMOUNT, player1.getBalance());
        assertEquals(INITIAL_BALANCE + TRANSFER_AMOUNT, player2.getBalance());
    }

    /**
     * Tests transfer funds with positive amount greater than the player1's balance.
     */
    @Test
    void testTransferFundsGreaterThanBalance(){
        final boolean result = bank.transferFunds(player1, player2, INITIAL_BALANCE+1);
        assertFalse(result);
        assertEquals(INITIAL_BALANCE, player1.getBalance());
        assertEquals(INITIAL_BALANCE, player2.getBalance());
    }

    /**
     * Tests transfer funds with negative amount.
     */
    @Test
    void testTransferFundsMoneyNegative(){
        assertThrows(IllegalArgumentException.class, () -> bank.transferFunds(player1, player2, -WITHDRAW_AMOUNT));
    }
}
