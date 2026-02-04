package it.unibo.javapoly.controller.impl;

import java.util.List;
import java.util.Objects;
import it.unibo.javapoly.controller.api.Bank;
import it.unibo.javapoly.controller.api.GameBoard;
import it.unibo.javapoly.controller.api.MatchController;
import it.unibo.javapoly.model.api.Player;
import it.unibo.javapoly.model.impl.DiceImpl;
import it.unibo.javapoly.model.impl.DiceThrow;
import it.unibo.javapoly.model.impl.PlayerImpl;
import it.unibo.javapoly.view.impl.MainView;

/**
 * MatchControllerImpl manages the flow of the game, including turns, 
 * movement, and GUI updates.
 */
public class MatchControllerImpl implements MatchController{

    private static final int MAX_DOUBLES = 3;

    private final List<PlayerImpl> players;    //implementazione esterna
    private final DiceThrow diceThrow;
    private final GameBoard gameBoard;
    private final Bank bank;               //implementazione esterna
    private final MainView gui;

    private int currentPlayerIndex;
    private int consecutiveDoubles;

    /**
     * Constructor
     *
     * @param players   list of players (already created)
     * @param gameBoard the game board implementation
     * @param bank      the bank implementation
     */
    public MatchControllerImpl(final List<PlayerImpl> players, final GameBoard gameBoard, final Bank bank){
        this.players = List.copyOf(players); //perche' la lista e' gia stata creata, cosi' la passo semplicemente
        this.currentPlayerIndex = 0;
        this.consecutiveDoubles = 0;
        this.diceThrow = new DiceThrow(new DiceImpl(), new DiceImpl());
        this.gameBoard = Objects.requireNonNull(gameBoard);
        this.bank = Objects.requireNonNull(bank);
        this.gui = new MainView(this);
    }

    /**
     * Starts the game.
     * Updates GUI and announces the first player.
     */
    @Override
    public void startGame() {
        gui.addLog("Partita avviata");
        gui.updateBoard();
        gui.updateInfo();

        final Player firstPlayer = getCurrentPlayer();
        gui.addLog("E' il turno di: " + firstPlayer.getName());
    }

    /**
     * Advances to the next player's turn.
     * Updates GUI and logs new turn.
     */
    @Override
    public void nextTurn() {
        this.currentPlayerIndex = (this.currentPlayerIndex + 1) % this.players.size();
        final Player current = getCurrentPlayer();
        gui.addLog("Ora e' il turno di: " + current.getName());
        gui.updateInfo();
    }

    /**
     * Returns the current player whose turn it is.
     */
    @Override
    public PlayerImpl getCurrentPlayer() {
        return this.players.get(this.currentPlayerIndex);
    }

    /**
     * Handles the logic when the current player throws the dice.
     */
    @Override
    public void handleDiceThrow() {
        final Player currentPlayer = getCurrentPlayer();
        final int steps = diceThrow.throwAll();

        gui.addLog("Il giocatore: " + currentPlayer.getName() + " lancia i dadi: " + steps);
        
        if(diceThrow.isDouble()){
            this.consecutiveDoubles++;
            gui.addLog("Doppio! Avanza e rilancia i dadi");
        }else{
            this.consecutiveDoubles = 0;
        }

        if(consecutiveDoubles == MAX_DOUBLES){
            gui.addLog("3 doppi consecutivi! Vai in prigione senza passare dal Via");
            handlePrison();
            this.consecutiveDoubles = 0;
            return;
        }

        handleMove(steps);
        gui.addLog("Il giocatore " + currentPlayer.getName() + " e' avanzato di: " + steps);

        if (diceThrow.isDouble()) {
            gui.addLog(currentPlayer.getName() + " deve tirare ancora");
        } else {
            gui.addLog(currentPlayer.getName() + " turno terminato");
        }
    }

    /**
     * Moves the current player by 'steps' spaces on the board.
     * Updates board and info panels.
     *
     * @param steps number of spaces to move
     */
    @Override
    public void handleMove(int steps) {
        final Player currentPlayer = getCurrentPlayer();
        currentPlayer.move(steps);

        gui.addLog(currentPlayer.getName() + " si e' spostato di: " + steps + " spazi");
        gui.updateBoard();
        gui.updateInfo();

        handlePropertyLanding();
    }

    /**
     * Handles the logic when a player is in prison.
     * For simplicity, we can just log it here.
     */
    @Override
    public void handlePrison() {
        final Player currentPlayer = getCurrentPlayer();
        gui.addLog(currentPlayer + " e' in prigione ora");
        //da finire di implementare
    }

    /**
     * Handles actions when a player lands on a property.
     * For now, just logs the event.
     */
    @Override
    public void handlePropertyLanding() {
        final Player currentPlayer = getCurrentPlayer();
        gui.addLog(currentPlayer + " e' in una proprieta' ora");
        //da finire di implementare
    }

    /**
     * Returns the game board.
     */
    public GameBoard getBoard() {
        return this.gameBoard;
    }
}
