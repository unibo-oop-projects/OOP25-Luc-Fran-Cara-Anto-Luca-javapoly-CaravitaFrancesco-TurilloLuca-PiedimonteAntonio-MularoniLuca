package it.unibo.javapoly.model.player;

public final class BankruptState implements PlayerState {

    private static final BankruptState INSTANCE = new BankruptState();

    private BankruptState() {
    }

    public static BankruptState getInstance() {
        return INSTANCE;
    }

    @Override
    public void playTurn(final Player player, final int diceResult) {
        System.out.println("[Bancarotta] Il giocatore " + player.getName() + " è fuori dal gioco."); // NOPMD
        // Non fa nulla
    }

    @Override
    public boolean canMove() {
        return false;
    }
}
