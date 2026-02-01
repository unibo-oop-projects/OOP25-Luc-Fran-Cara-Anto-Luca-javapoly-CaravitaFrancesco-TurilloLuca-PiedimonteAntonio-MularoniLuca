package it.unibo.javapoly.model.player;

// Visibilità package-private
class TokenImpl implements Token {

    private final String type;

    // Costruttore Protected: solo la Factory può chiamarlo
    protected TokenImpl(final String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Token[" + type + "]";
    }
}
