package it.unibo.javapoly.controller.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import it.unibo.javapoly.controller.api.EconomyController;
import it.unibo.javapoly.controller.api.MatchController;
import it.unibo.javapoly.model.api.Player;
import it.unibo.javapoly.model.api.PlayerState;
import it.unibo.javapoly.model.api.board.Board;
import it.unibo.javapoly.model.api.board.Tile;
import it.unibo.javapoly.model.api.board.TileType;
import it.unibo.javapoly.model.api.economy.Bank;
import it.unibo.javapoly.model.impl.DiceImpl;
import it.unibo.javapoly.model.impl.DiceThrow;
import it.unibo.javapoly.model.impl.FreeState;
import it.unibo.javapoly.model.impl.JailedState;
import it.unibo.javapoly.view.impl.MainView;

/**
 * MatchControllerImpl manages the flow of the game, including turns, 
 * movement, and GUI updates.
 */
public class MatchControllerImpl implements MatchController {

    private static final int MAX_DOUBLES = 3;

    private final List<Player> players;  
    private final DiceThrow diceThrow;
    private final Board gameBoard;
    private final Bank bank;     
    private final MainView gui;
    private final Map<Player, Integer> jailTurnCounter = new HashMap<>();

    private EconomyController economyController;
    private PropertyController propertyController;

    private int currentPlayerIndex;
    private int consecutiveDoubles;

    private boolean hasRolled = false;

    /**
     * Constructor
     *
     * @param players   list of players (already created)
     * @param gameBoard the game board implementation
     * @param bank      the bank implementation
     */
    public MatchControllerImpl(final List<Player> players, final Board gameBoard, final Bank bank){
        this.players = List.copyOf(players);
        this.gameBoard = Objects.requireNonNull(gameBoard);
        this.bank = Objects.requireNonNull(bank);
        this.diceThrow = new DiceThrow(new DiceImpl(), new DiceImpl());
        this.gui = new MainView(this);
        this.currentPlayerIndex = 0;
        this.consecutiveDoubles = 0;

        for (Player p : this.players) {
            p.addObserver(this); 
        }
    }

    //for test
    public MatchControllerImpl(final List<Player> players, final Board gameBoard, final Bank bank, final MainView gui) {
        this.players = List.copyOf(players);
        this.gameBoard = Objects.requireNonNull(gameBoard);
        this.bank = Objects.requireNonNull(bank);
        this.diceThrow = new DiceThrow(new DiceImpl(), new DiceImpl());
        this.gui = gui;
        this.currentPlayerIndex = 0;
        this.consecutiveDoubles = 0;

        for (Player p : this.players) {
            p.addObserver(this); 
        }
    }

    public MatchControllerImpl(final List<Player> players){
        this.players = players;
    }

    /**
     * Starts the game.
     * Updates GUI and announces the first player.
     */
    @Override
    public void startGame() {
        updateGui(g -> {
            g.addLog("Partita avviata");
            g.refreshAll();
            g.addLog("E' il turno di: " + getCurrentPlayer().getName());
        });
    }

    /**
     * Advances to the next player's turn.
     * Updates GUI and logs new turn.
     */
    @Override
    public void nextTurn() {
        this.currentPlayerIndex = (this.currentPlayerIndex + 1) % this.players.size();
        this.hasRolled = false;
        this.consecutiveDoubles = 0;

        final Player current = getCurrentPlayer();

        updateGui(g -> {
            g.addLog("Ora e' il turno di: " + current.getName());
            g.refreshAll();
        });
    }

    /**
     * Returns the current player whose turn it is.
     */
    @Override
    public Player getCurrentPlayer() {
        return this.players.get(this.currentPlayerIndex);
    }

    /**
     * Handles the logic when the current player throws the dice.
     */
    @Override
    public void handleDiceThrow() {
        if(this.hasRolled){
            return;
        }

        final Player currentPlayer = getCurrentPlayer();
        final int steps = diceThrow.throwAll();
        final boolean isDouble = diceThrow.isDouble();

        if(currentPlayer.getState() instanceof JailedState){
            int turns = jailTurnCounter.getOrDefault(currentPlayer, 0);
            if(isDouble){
                updateGui(g -> g.addLog(currentPlayer.getName() + " esce col DOPPIO (" + steps + ")!"));
                currentPlayer.setState(FreeState.getInstance());
                jailTurnCounter.remove(currentPlayer);
            }else if(turns >= 2){
                updateGui(g -> g.addLog(currentPlayer.getName() + " fallisce il 3° tentativo. Paga 50€ ed esce!"));
                currentPlayer.tryToPay(50);
                currentPlayer.setState(FreeState.getInstance());
                jailTurnCounter.remove(currentPlayer);
            }else{
                jailTurnCounter.put(currentPlayer, turns + 1);
                updateGui(g -> g.addLog(currentPlayer.getName() + " resta in prigione (Tentativo " + (turns + 1) + "/3)"));
                this.hasRolled = true;
                return;
                }
            }

        updateGui(g -> g.addLog(currentPlayer.getName() + " lancia: " + steps + (isDouble ? " (DOPPIO!)" : "")));
        
        if(isDouble && !(currentPlayer.getState() instanceof JailedState)){
            this.consecutiveDoubles++;
            if(this.consecutiveDoubles == MAX_DOUBLES){
                updateGui(g -> g.addLog("3 doppi! In prigione."));
                handlePrison(); 
                this.hasRolled = true;
                return;
            }
        } else {
            this.consecutiveDoubles = 0;
            this.hasRolled = true;
        }

        int potentialPos = gameBoard.normalizePosition(currentPlayer.getCurrentPosition() + steps);
        currentPlayer.playTurn(potentialPos, isDouble);
    }

    @Override
    public List<Player> getPlayers() {
        // Restituisci la lista di giocatori che hai creato all'inizio
        return this.players; 
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
        int newPosition = gameBoard.normalizePosition(currentPlayer.getCurrentPosition() + steps);
        currentPlayer.move(newPosition);
    }

    /**
     * Handles the logic when a player is in prison.
     * For simplicity, we can just log it here.
     */
    @Override
    public void handlePrison() {
        final Player currentPlayer = getCurrentPlayer();
        currentPlayer.move(10);
        currentPlayer.setState(new JailedState());
    }

    /**
     * Handles actions when a player lands on a property.
     * For now, just logs the event.
     */
    @Override
    public void handlePropertyLanding() {
        final Player currentPlayer = getCurrentPlayer();
        final Tile currentTile = gameBoard.getTileAt(currentPlayer.getCurrentPosition());

        if (currentTile.getType() == TileType.TAX && currentPlayer.getCurrentPosition() == 4) {
            currentPlayer.tryToPay(200);
            updateGui(g -> g.addLog(currentPlayer.getName() + " paga la Tassa Patrimoniale di 200€"));
        }else if(currentTile.getType() == TileType.TAX){
            currentPlayer.tryToPay(100);
            updateGui(g -> g.addLog(currentPlayer.getName() + " paga la Tassa di Lusso di 100€"));
        }
        if (currentTile.getType() == TileType.GO_TO_JAIL) {
            handlePrison();
        }
    }

    /**
     * Returns the game board.
     */
    public Board getBoard() {
        return this.gameBoard;
    }

    //descrizione da inserire
    public boolean canCurrentPlayerRoll() {
        return !hasRolled;
    }

    @Override
    public MainView getMainView() {
        return this.gui;
    }

    private void updateGui(Consumer<MainView> action) {
        if (this.gui != null) {
            action.accept(this.gui);
        }
    }

    @Override
    public void onPlayerMoved(Player player, int oldPosition, int newPosition) {
        if(newPosition < oldPosition && newPosition != 10){
            bank.deposit(player, 200);
            updateGui(g -> g.addLog(player.getName() + " è passato dal VIA! +200€"));
        }
        updateGui(g -> {
            g.refreshAll();
            g.addLog(player.getName() + " si è spostato sulla casella " + newPosition);
        });

        handlePropertyLanding();
    }

    @Override
    public void onBalanceChanged(Player player, int newBalance) {
        updateGui(g -> g.refreshAll());
    }

    @Override
    public void onStateChanged(Player player, PlayerState oldState, PlayerState newState) {
        updateGui(g -> {
            g.addLog(player.getName() + " ora è in stato: " + newState.getClass().getSimpleName());
            g.refreshAll();;
        });
    }

    @Override
    public void payToExitJail() {
        Player p = getCurrentPlayer();
        if(p.getState() instanceof JailedState){
            if(p.tryToPay(50)){
                p.setState(FreeState.getInstance());
                jailTurnCounter.remove(p);
                updateGui(g -> {
                    g.addLog(p.getName() + " paga 50€ ed è libero!");
                    g.refreshAll();
                });
            }
        }
    }

    public EconomyController getEconomyController() {
        return this.economyController;
    }

    public PropertyController getPropertyController() {
        return this.propertyController;
    }
}
