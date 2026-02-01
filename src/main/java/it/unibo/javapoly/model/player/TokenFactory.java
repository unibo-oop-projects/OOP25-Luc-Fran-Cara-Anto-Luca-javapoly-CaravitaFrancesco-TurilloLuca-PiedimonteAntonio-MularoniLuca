package it.unibo.javapoly.model.player;

@SuppressWarnings("PMD.ExhaustiveSwitchHasDefault")
public final class TokenFactory {

    private TokenFactory() {
    }

    // Factory Method Statico
    public static Token createToken(final TokenType type) {
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
            default -> throw new IllegalArgumentException("Tipo pedina non supportato: " + type);
        }
    }
}
