package it.unibo.javapoly.model.player;

public final class FreeState implements PlayerState {

    private static final FreeState INSTANCE = new FreeState();

    private FreeState() {
    }

    public static FreeState getInstance() {
        return INSTANCE;
    }

    @Override
    public void playTurn(final Player player, final int diceResult) {
        // Logica standard: il giocatore è libero, quindi si muove.
        System.out.println("[Stato Libero] Il giocatore si muove di " + diceResult); // NOPMD
        player.move(diceResult);
    }

    @Override
    public boolean canMove() {
        return true;
    }
}
