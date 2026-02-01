package it.unibo.javapoly.model.player;

public final class JailedState implements PlayerState {

    private int turnsInJail;
    private static final int MAX_TURNS = 3;

    public JailedState() {
        this.turnsInJail = 0;
    }

    @Override
    public void playTurn(final Player player, final int diceResult) {
        turnsInJail++;
        System.out.println("[Prigione] Turno " + turnsInJail + " di detenzione."); // NOPMD

        // 1. Se fa "doppio" esce (se diceResult > 10 esce)
        // 2. Se raggiunge 3 turni esce

        final boolean rolledDouble = diceResult > 10; // placeholder per "doppio"

        if (rolledDouble || turnsInJail >= MAX_TURNS) {
            System.out.println(player.getName() + " esce di prigione!"); // NOPMD

            // Transizione di stato: torna Libero
            player.setState(FreeState.getInstance());

            // Appena uscito, si muove
            player.move(diceResult);
        } else {
            System.out.println("Resta in prigione."); // NOPMD
        }
    }

    @Override
    public boolean canMove() {
        return false;
    }
}
