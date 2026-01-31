package it.unibo.javapoly.model.player;

public class FreeState implements PlayerState {

    private static final FreeState INSTANCE = new FreeState();

    private FreeState() {
    }

    public static FreeState getInstance() {
        return INSTANCE;
    }

    @Override
    public void playTurn(Player player, int diceResult) {
        // Logica standard: il giocatore è libero, quindi si muove.
        //System.out.println("[Stato Libero] Il giocatore si muove di " + diceResult);
        player.move(diceResult);
    }

    @Override
    public boolean canMove() {
        return true;
    }
}