package it.unibo.javapoly.model.api.card;

/**
 * Supported Card types. 
 */
public enum CardType {
    MOVE_TO,            // go to a fixed position
    MOVE_RELATIVE,      // move forward/backward by N
    MOVE_TO_NEAREST,    // nearest station/utility
    PAY_BANK,           // pay the bank
    RECEIVE_BANK,       // receive from the bank
    GO_TO_JAIL,         // go to jail
    GET_OUT_OF_JAIL_FREE,
    NO_EFFECT
}
