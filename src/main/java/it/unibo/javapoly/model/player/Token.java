package it.unibo.javapoly.model.player;

/**
 * Interfaccia per la pedina (Product del Factory Pattern).
 */
@FunctionalInterface
public interface Token {
    String getType();
}
