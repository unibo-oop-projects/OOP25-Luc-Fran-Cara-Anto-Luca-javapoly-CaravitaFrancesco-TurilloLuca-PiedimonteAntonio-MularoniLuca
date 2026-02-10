package it.unibo.javapoly.model.impl;

import it.unibo.javapoly.model.api.Token;
import it.unibo.javapoly.model.api.TokenType;
import it.unibo.javapoly.utils.ValidationUtils;

/**
 * Factory class responsible for creating {@link Token} instances.
 * 
 * <p>
 * This class provides a static factory method to instantiate tokens based on
 * the available {@link TokenType}s, abstracting the creation logic.
 * </p>
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
        ValidationUtils.requireNonNull(type, "The token type cannot be null");

        switch (type) {
            case BOAT -> {
                return new TokenImpl("Boat");
            }
            case BOOT -> {
                return new TokenImpl("Boot");
            }
            case CAR -> {
                return new TokenImpl("Car");
            }
            case CAT -> {
                return new TokenImpl("Cat");
            }
            case DOG -> {
                return new TokenImpl("Dog");
            }
            case DUCK -> {
                return new TokenImpl("Duck");
            }
            case HAT -> {
                return new TokenImpl("Hat");
            }
            case IRON -> {
                return new TokenImpl("Iron");
            }
            case THIMBLE -> {
                return new TokenImpl("Thimble");
            }
            case WHEELBARROW -> {
                return new TokenImpl("Wheelbarrow");
            }
        }
        throw new IllegalArgumentException("Unsupported token type: " + type);
    }
}
