package it.unibo.javapoly.model.api;

/**
 * Represents a token used by a player in the JavaPoly game.
 * Since this is a functional interface, it can be implemented using a lambda
 * expression to define the type of the token.
 */
@FunctionalInterface
public interface Token {

    /**
     * Gets the type of the token.
     *
     * @return a {@link String} representing the type of the token.
     */
    String getType();
}
