package it.unibo.javapoly.controller.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonProperty;

import it.unibo.javapoly.controller.api.BoardController;
import it.unibo.javapoly.controller.api.EconomyController;
import it.unibo.javapoly.controller.api.MatchController;
import it.unibo.javapoly.controller.api.PropertyController;
import it.unibo.javapoly.model.api.Player;
import it.unibo.javapoly.model.api.PlayerState;
import it.unibo.javapoly.model.api.board.Board;
import it.unibo.javapoly.model.api.board.Tile;
import it.unibo.javapoly.model.api.property.Property;
import it.unibo.javapoly.model.impl.DiceImpl;
import it.unibo.javapoly.model.impl.DiceThrow;
import it.unibo.javapoly.model.impl.FreeState;
import it.unibo.javapoly.model.impl.JailedState;
import it.unibo.javapoly.model.impl.board.BoardImpl;
import it.unibo.javapoly.model.impl.board.tile.PropertyTile;
import it.unibo.javapoly.view.impl.MainView;

/**
 * MatchControllerImpl manages the flow of the game, including turns, 
 * movement, and GUI updates.
 */
public class MatchControllerImpl implements MatchController {

    private static final int MAX_DOUBLES = 3;
    private static final int JAIL_EXIT_FEE = 50;
    
    private final List<Player> players;  
    private final DiceThrow diceThrow;
    private final Board gameBoard;    
    private final MainView gui;
    private final Map<Player, Integer> jailTurnCounter = new HashMap<>();

    private final EconomyController economyController;
    private final PropertyController propertyController;
    private final BoardController boardController;

    private int currentPlayerIndex;
    private int consecutiveDoubles;
    private int lastDiceResult;
    private boolean hasRolled = false;

    /**
     * Constructor
     *
     * @param players   list of players (already created)
     * @param gameBoard the game board implementation
     * @param bank      the bank implementation
     */
    public MatchControllerImpl(final List<Player> players, final Board gameBoard, final Map<String, Property> properties){
        this.players = List.copyOf(players);
        this.gameBoard = Objects.requireNonNull(gameBoard);
        this.propertyController = new PropertyControllerImpl(properties);
        this.economyController = new EconomyControllerImpl(propertyController, players);
        this.boardController = new BoardControllerImpl(gameBoard, economyController, propertyController);
        this.diceThrow = new DiceThrow(new DiceImpl(), new DiceImpl());
        this.gui = new MainView(this);
        this.currentPlayerIndex = 0;
        this.consecutiveDoubles = 0;

        for (Player p : this.players) {
            p.addObserver(this); 
        }
    }

    public MatchControllerImpl(@JsonProperty("players") final List<Player> players) {
        this(players, new BoardImpl(new ArrayList<>()), new HashMap<>());
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
     * Handles the logic when the current player throws the dice.
     */
    @Override
    public void handleDiceThrow() {
        if(this.hasRolled){
            return;
        }

        final Player currentPlayer = getCurrentPlayer();
        this.lastDiceResult = diceThrow.throwAll();
        final boolean isDouble = diceThrow.isDouble();

        if(currentPlayer.getState() instanceof JailedState){
            int turns = jailTurnCounter.getOrDefault(currentPlayer, 0);
            if(isDouble){
                updateGui(g -> g.addLog(currentPlayer.getName() + " esce col DOPPIO (" + this.lastDiceResult + ")!"));
                currentPlayer.setState(FreeState.getInstance());
                jailTurnCounter.remove(currentPlayer);
            }else if(turns >= 2){
                updateGui(g -> g.addLog(currentPlayer.getName() + " fallisce il 3° tentativo. Paga 50€ ed esce!"));
                economyController.withdrawFromPlayer(currentPlayer, JAIL_EXIT_FEE);
                currentPlayer.setState(FreeState.getInstance());
                jailTurnCounter.remove(currentPlayer);
            }else{
                jailTurnCounter.put(currentPlayer, turns + 1);
                updateGui(g -> g.addLog(currentPlayer.getName() + " resta in prigione (Tentativo " + (turns + 1) + "/3)"));
                this.hasRolled = true;
                return;
                }
            }

        updateGui(g -> g.addLog(currentPlayer.getName() + " lancia: " + this.lastDiceResult + (isDouble ? " (DOPPIO!)" : "")));
        
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

        this.boardController.movePlayer(currentPlayer, this.lastDiceResult);
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
        this.boardController.movePlayer(currentPlayer, steps);
    }

    /**
     * Handles the logic when a player is in prison.
     * For simplicity, we can just log it here.
     */
    @Override
    public void handlePrison() {
        this.boardController.sendPlayerToJail(getCurrentPlayer());
    }

    /**
     * Handles actions when a player lands on a property.
     * For now, just logs the event.
     */
    @Override
    public void handlePropertyLanding() {
        final Player currentPlayer = getCurrentPlayer();
        final Tile currentTile = gameBoard.getTileAt(currentPlayer.getCurrentPosition());

        if(currentTile instanceof PropertyTile){
            Property prop =  ((PropertyTile) currentTile).getProperty();
            if(prop.getIdOwner() == null){
                updateGui(g -> g.addLog("Puoi acquistare " + prop.getId() + " per " + prop.getPurchasePrice() + "€"));
                // Qui la GUI dovrebbe abilitare il tasto "Acquista"
            }
        }
        updateGui(g -> g.refreshAll());
    }

    public void buyCurrentProperty(){
        final Player currentPlayer = getCurrentPlayer();
        final Tile currentTile = gameBoard.getTileAt(currentPlayer.getCurrentPosition());

        if(currentTile instanceof PropertyTile){
            Property prop = ((PropertyTile) currentTile).getProperty();

            if(this.economyController.purchaseProperty(currentPlayer, prop)){
                updateGui(g -> {
                    g.addLog(currentPlayer.getName() + " ha acquistato " + prop.getId() + " per " + prop.getPurchasePrice() + "€");
                    g.refreshAll();
                });
            }else {
                updateGui(g -> g.addLog("Non hai abbastanza soldi per acquistare " + prop.getId()));
            }
        }
    }

    public void buildHouseOnProperty(Property property){
        try {
            if(this.economyController.purchaseHouse(getCurrentPlayer(), property)){
                updateGui(g -> {
                    g.addLog("Costruita una casa su " + property.getId());
                    g.refreshAll();
                });
            }else {
                updateGui(g -> g.addLog("Impossibile costruire su " + property.getId()));
            }
        } catch (IllegalStateException e) {
            updateGui(g -> g.addLog("Errore: " + e.getMessage()));

        } catch (IllegalArgumentException e) {
            updateGui(g -> g.addLog("Non puoi costruire su questa tipologia di casella."));
        }
    }

    @Override
    public void onPlayerMoved(Player player, int oldPosition, int newPosition) {
        updateGui(g -> g.refreshAll());

        final Tile currentTile = gameBoard.getTileAt(newPosition);

        this.boardController.executeTileLogic(player, currentTile, this.lastDiceResult);
        final String boardMessage = this.boardController.getMessagePrint();
        if(boardMessage != null && !boardMessage.isEmpty()){
            updateGui(g -> g.addLog(boardMessage));
        }
        handlePropertyLanding();
    }

    @Override
    public void payToExitJail() {
        Player p = getCurrentPlayer();
        if(p.getState() instanceof JailedState && economyController.withdrawFromPlayer(p, JAIL_EXIT_FEE)){
            p.setState(FreeState.getInstance());
            jailTurnCounter.remove(p);
            updateGui(g -> {
                g.addLog(p.getName() + " paga 50€ ed è libero!");
                g.refreshAll();
            });
        }
    }

    public EconomyController getEconomyController() {
        return this.economyController;
    }

    public PropertyController getPropertyController() {
        return this.propertyController;
    }

    @Override
    public List<Player> getPlayers(){
        return this.players;
    }

    @Override
    public Player getCurrentPlayer() {
        return this.players.get(this.currentPlayerIndex);
    }

    @Override
    public Board getBoard(){
        return this.gameBoard;
    }
    @Override
    public MainView getMainView(){
        return this.gui;
    }

    public boolean canCurrentPlayerRoll(){ 
        return !hasRolled; 
    }

    public Map<Player, Integer> getJailTurnCounter() {
        return this.jailTurnCounter;
    }

    private void updateGui(Consumer<MainView> action) {
        if (this.gui != null) action.accept(this.gui);
    }

    @Override
    public void onBalanceChanged(Player player, int newBalance) {
        updateGui(g -> g.refreshAll());
    }

    @Override
    public void onStateChanged(Player player, PlayerState oldState, PlayerState newState) {
        updateGui(g -> {
            g.addLog(player.getName() + " ora è in stato: " + newState.getClass().getSimpleName());
            g.refreshAll();
        });
    }

    public int getCurrentPlayerIndex() {
        return this.currentPlayerIndex;
    }

    public int getConsecutiveDoubles() {
        return this.consecutiveDoubles;
    }

    public void setCurrentPlayerIndex(int i) {
        this.currentPlayerIndex = i;
    }

    public void setConsecutiveDoubles(int d) {
        this.consecutiveDoubles = d;
    }

    public void setHasRolled(boolean b) {
        this.hasRolled = b;
    }

    // For jail turn counter restoration
    public void restoreJailTurnCounter(final Map<String, Integer> map, final List<Player> players) {
        this.jailTurnCounter.clear();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            final String ownerId = entry.getKey();
            final Player owner = players.stream()
                    .filter(p -> p.getName().equals(ownerId))
                    .findFirst()
                    .orElse(null);
            if (owner != null) {
                this.jailTurnCounter.put(owner, entry.getValue());
            }
        }
    }
}
