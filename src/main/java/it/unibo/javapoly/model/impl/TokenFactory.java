package it.unibo.javapoly.model.impl;

import java.util.Objects;

import it.unibo.javapoly.model.api.Token;
import it.unibo.javapoly.model.api.TokenType;

/**
 * Factory class for creating Token instances.
 * This class provides a static method to create tokens based on a specified
 * type.
 * It follows the Factory pattern to abstract the instantiation logic of Token
 * objects.
 * 
 * @see Token
 * @see TokenType
 */
public final class TokenFactory {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private TokenFactory() {
    }

    /**
     * Creates a new Token based on the provided TokenType.
     *
     * @param type the type of the token to be created.
     * @return a new Token instance corresponding to the specified type.
     * @throws IllegalArgumentException if the provided token type is not supported.
     * @see Token
     * @see TokenType
     */
    public static Token createToken(final TokenType type) {
        Objects.requireNonNull(type, "The token type cannot be null");

        switch (type) {
            case CAR -> {
                return new TokenImpl("Car");
            }
            case HAT -> {
                return new TokenImpl("Hat");
            }
            case DOG -> {
                return new TokenImpl("Dog");
            }
            case SHOE -> {
                return new TokenImpl("Shoe");
            }
            case SHIP -> {
                return new TokenImpl("Ship");
            }
            default -> throw new IllegalArgumentException("Unsupported token type: " + type);
        }
    }
}
