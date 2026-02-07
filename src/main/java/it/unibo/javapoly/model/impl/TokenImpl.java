package it.unibo.javapoly.model.impl;

import java.util.Objects;

import it.unibo.javapoly.model.api.Token;

/**
 * Implementation of the {@link Token} interface representing a player's piece
 * on the board.
 * This class encapsulates the visual or logical type of the token.
 * 
 * @see Token
 * @see TokenType
 */
class TokenImpl implements Token {

    private final String type;

    /**
     * Constructs a new TokenImpl with the specified type.
     *
     * @param type the {@link String} representation of the token type.
     */
    protected TokenImpl(final String type) {
        Objects.requireNonNull(type, "The token type cannot be null");
        if (type.isBlank()) {
            throw new IllegalArgumentException("The token type cannot be blank");
        }

        this.type = type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Token[" + type + "]";
    }
}
