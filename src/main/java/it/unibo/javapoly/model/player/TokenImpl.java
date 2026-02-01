package it.unibo.javapoly.model.player;

/**
 * Implementation of the {@link Token} interface representing a player's piece
 * on the board.
 * This class encapsulates the visual or logical type of the token.
 */
class TokenImpl implements Token {

    private final String type;

    /**
     * Constructs a new TokenImpl with the specified type.
     *
     * @param type the string representation of the token type.
     */
    protected TokenImpl(final String type) {
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
     * Returns a string representation of the token.
     *
     * @return a string in the format "Token[type]"
     */
    @Override
    public String toString() {
        return "Token[" + type + "]";
    }
}
