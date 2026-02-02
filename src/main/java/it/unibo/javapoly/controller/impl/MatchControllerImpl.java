package it.unibo.javapoly.controller.impl;

import java.util.List;

import it.unibo.javapoly.controller.api.Bank;
import it.unibo.javapoly.controller.api.GameBoard;
import it.unibo.javapoly.controller.api.MatchController;
import it.unibo.javapoly.model.api.Player;
import it.unibo.javapoly.model.impl.DiceImpl;
import it.unibo.javapoly.model.impl.DiceThrow;
import it.unibo.javapoly.view.impl.MainFrame;

/**
 * MatchControllerImpl manages the flow of the game:
 * - player turns
 * - dice throws
 * - player movement
 * - interaction with the bank and properties
 * - updating the GUI
 */
public class MatchControllerImpl implements MatchController {
    private final List<Player> players; // implementazione esterna
    private int currentPlayerIndex;
    private int consecutiveDoubles;
    private final DiceThrow diceThrow;
    private final GameBoard gameBoard;
    private final Bank bank; // implementazione esterna
    private final MainFrame gui;

    /**
     * Constructor
     *
     * @param players   list of players (already created)
     * @param gameBoard the game board implementation
     * @param bank      the bank implementation
     */
    public MatchControllerImpl(List<Player> players, GameBoard gameBoard, Bank bank) {
        this.players = players; // perche' la lista e' gia stata creata, cosi' la passo semplicemente
        this.currentPlayerIndex = 0;
        this.consecutiveDoubles = 0;
        this.diceThrow = new DiceThrow(new DiceImpl(), new DiceImpl());
        this.gameBoard = gameBoard;
        this.bank = bank;
        this.gui = new MainFrame(this);
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

        Player firstPlayer = getCurrentPlayer();
        gui.addLog("E' il turno di: " + firstPlayer.getName());
    }

    /**
     * Advances to the next player's turn.
     * Updates GUI and logs new turn.
     */
    @Override
    public void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();

        Player current = getCurrentPlayer();

        gui.addLog("Ora e' il turno di: " + current.getName());

        gui.updateInfo();
    }

    /**
     * Returns the current player whose turn it is.
     */
    @Override
    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    /**
     * Handles the logic when the current player throws the dice.
     */
    @Override
    public void handleDiceThrow() {
        Player currentPlayer = getCurrentPlayer();
        int steps = diceThrow.throwAll();
        gui.addLog("Il giocatore: " + currentPlayer.getName() + " lancia i dadi: " + steps);

        if (diceThrow.isDouble()) {
            consecutiveDoubles++;
            gui.addLog("Doppio! Avanza e rilancia i dadi");
        } else {
            consecutiveDoubles = 0;
        }

        if (consecutiveDoubles == 3) {
            gui.addLog("3 doppi consecutivi! Vai in prigione senza passare dal Via");
            handlePrison();
            consecutiveDoubles = 0;
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
        Player currentPlayer = getCurrentPlayer();
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handlePrison'");
    }

    /**
     * Handles actions when a player lands on a property.
     * For now, just logs the event.
     */
    @Override
    public void handlePropertyLanding() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handlePropertyLanding'");
    }

    /**
     * Returns the game board.
     */
    public GameBoard getBoard() {
        return this.gameBoard;
    }
}
