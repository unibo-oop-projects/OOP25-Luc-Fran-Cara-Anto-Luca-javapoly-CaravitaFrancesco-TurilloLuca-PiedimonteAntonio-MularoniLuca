package it.unibo.javapoly.model.player;

/**
 * Interfaccia per il pattern State.
 * Definisce il comportamento che varia in base alla condizione del giocatore.
 */
public interface PlayerState {

    /**
     * Esegue la logica del turno specifica per lo stato corrente.
     * 
     * @param player     il contesto (il giocatore)
     * @param diceResult la somma dei dadi lanciati
     */
    void playTurn(Player player, int diceResult);

    /**
     * @return true se il giocatore può muoversi, false altrimenti.
     */
    boolean canMove();

}
