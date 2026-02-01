package it.unibo.javapoly.model.player;

/**
 * Interfaccia pubblica del Giocatore.
 */
public interface Player {

    String getName();

    int getBalance();

    Token getToken();

    /**
     * Metodo principale chiamato dal GameEngine per far giocare il turno.
     * Delega la logica allo stato corrente.
     */
    void playTurn(int diceResult);

    /**
     * Sposta fisicamente il giocatore (aggiorna la posizione).
     * Questo metodo dovrebbe essere chiamato dagli Stati, non direttamente
     * dal motore.
     */
    void move(int steps);

    /**
     * Gestione pagamenti.
     * 
     * @return true se pagamento riuscito, false se bancarotta.
     */
    boolean tryToPay(int amount);

    void receiveMoney(int amount);

    // Metodi per la gestione dello stato (usati internamente o da carte speciali)
    void setState(PlayerState state);

    PlayerState getState();
}
