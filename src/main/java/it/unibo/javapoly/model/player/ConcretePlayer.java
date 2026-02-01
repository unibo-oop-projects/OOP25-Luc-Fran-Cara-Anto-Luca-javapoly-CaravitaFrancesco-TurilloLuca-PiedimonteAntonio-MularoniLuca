package it.unibo.javapoly.model.player;

public class ConcretePlayer implements Player {

    private final String name;
    private int balance;
    private final Token token;
    private PlayerState currentState;
    private static final int SPACES_ON_BOARD = 40;

    // Placeholder per la posizione (da 0 a 39)
    private int currentPosition;

    public ConcretePlayer(final String name, final int initialBalance, final TokenType tokenType) {
        this.name = name;
        this.balance = initialBalance;
        // Factory per creare la pedina
        this.token = TokenFactory.createToken(tokenType);
        this.currentState = FreeState.getInstance(); // Stato iniziale
        this.currentPosition = 0; // Posizione iniziale
    }

    @Override
    public void playTurn(final int diceResult) {
        // Delega allo stato corrente (Pattern State)
        this.currentState.playTurn(this, diceResult);
    }

    @Override
    public void move(final int steps) {
        // Logica di movimento circolare (0-39)
        final int oldPos = currentPosition;
        currentPosition = (currentPosition + steps) % SPACES_ON_BOARD;

        System.out.println(// NOPMD
                "DEBUG: " + name + " (" + token.getType() + ") si sposta da " + oldPos + " a " + currentPosition);

        // TODO: notificare la View (Observer) e il Tabellone
    }

    @Override
    public boolean tryToPay(final int amount) {
        if (balance >= amount) {
            balance -= amount;
            return true;
        }
        return false; // TODO gestire la bancarotta o ipoteca
    }

    @Override
    public void receiveMoney(final int amount) {
        this.balance += amount;
    }

    // --- Getter e Setter ---

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getBalance() {
        return balance;
    }

    @Override
    public Token getToken() {
        return token;
    }

    @Override
    public PlayerState getState() {
        return currentState;
    }

    @Override
    public void setState(final PlayerState state) {
        this.currentState = state;
        System.out.println("Stato cambiato in: " + state.getClass().getSimpleName()); // NOPMD
    }

    @Override
    public String toString() {
        return "Player{" + name + ", " + balance + "$, " + currentState.getClass().getSimpleName() + "}";
    }
}
