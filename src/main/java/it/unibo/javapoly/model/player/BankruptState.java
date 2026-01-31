package it.unibo.javapoly.model.player;

public class BankruptState implements PlayerState {

    private static final BankruptState INSTANCE = new BankruptState();

    private BankruptState() {
    }

    public static BankruptState getInstance() {
        return INSTANCE;
    }

    @Override
    public void playTurn(Player player, int diceResult) {
        System.out.println("[Bancarotta] Il giocatore " + player.getName() + " è fuori dal gioco.");
        // Non fa nulla
    }

    @Override
    public boolean canMove() {
        return false;
    }
}