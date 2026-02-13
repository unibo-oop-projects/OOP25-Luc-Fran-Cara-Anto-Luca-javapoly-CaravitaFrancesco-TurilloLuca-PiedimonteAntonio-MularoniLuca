package it.unibo.javapoly.controller.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import it.unibo.javapoly.controller.api.*;
import it.unibo.javapoly.model.api.Player;
import it.unibo.javapoly.model.api.PlayerState;
import it.unibo.javapoly.model.api.board.Board;
import it.unibo.javapoly.model.api.board.Tile;
import it.unibo.javapoly.model.api.property.Property;
import it.unibo.javapoly.model.impl.BankruptState;
import it.unibo.javapoly.model.impl.DiceImpl;
import it.unibo.javapoly.model.impl.DiceThrow;
import it.unibo.javapoly.model.impl.FreeState;
import it.unibo.javapoly.model.impl.JailedState;
import it.unibo.javapoly.model.impl.board.BoardImpl;
import it.unibo.javapoly.model.impl.board.tile.PropertyTile;
import it.unibo.javapoly.model.impl.board.tile.UnexpectedTile;
import it.unibo.javapoly.view.impl.MainView;
import javafx.application.Platform;

/**
 * MatchControllerImpl manages the flow of the game, including turns,
 * movement, and GUI updates.
 */
@JsonIgnoreProperties(value = { "gui", "economyController", "mainView", "" }, ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class MatchControllerImpl implements MatchController {
    private static final int MAX_DOUBLES = 1;
    private static final int JAIL_EXIT_FEE = 1410;

    private final List<Player> players;
    private final List<Player> playersBankrupt;
    private final DiceThrow diceThrow;
    private final Board gameBoard;
    @JsonIgnore
    private final MainView gui;
    private final Map<Player, Integer> jailTurnCounter = new HashMap<>();

    @JsonIgnore
    private final EconomyController economyController;
    private final PropertyController propertyController;

    private final BoardController boardController;

    private int currentPlayerIndex;
    private int consecutiveDoubles;
    private boolean hasRolled;
    private Player currentCreditor;
    @JsonIgnore
    private LiquidationObserver liquidationObserver;

    /**
     * Constructor for MatchControllerImpl.
     *
     * @param players    list of players (already created).
     * @param gameBoard  the game board implementation.
     * @param properties the map of properties in the game.
     */
    @JsonIgnoreProperties({ "gui", "economyController" })
    public MatchControllerImpl(final List<Player> players, final Board gameBoard,
            final Map<String, Property> properties) {
        this.players = List.copyOf(players);
        this.gameBoard = Objects.requireNonNull(gameBoard);
        this.liquidationObserver = new LiquidationObserverImpl(this);
        this.propertyController = new PropertyControllerImpl(properties);
        this.economyController = new EconomyControllerImpl(propertyController);
        this.economyController.setLiquidationObserver(this.liquidationObserver);
        this.playersBankrupt = new ArrayList<>();

        this.boardController = new BoardControllerImpl(gameBoard, economyController, propertyController);
        this.diceThrow = new DiceThrow(new DiceImpl(), new DiceImpl());
        this.gui = new MainView(this);
        this.currentPlayerIndex = 0;
        this.consecutiveDoubles = 0;

        for (final Player p : this.players) {
            p.addObserver(this);
        }
    }

    /**
     * JSON Creator for loading a saved match state.
     * @param players             the list of players.  
     * @param gameBoard           the game board.
     * @param propertyController  the property controller.
     * @param currentPlayerIndex  index of the current player.
     * @param consecutiveDoubles  number of consecutive doubles.
     * @param hasRolled           if player has already rolled.
     * @param jailTurnCounterJson map of players in jail.
     * @param diceThrow           the dice state.
     */
    @JsonCreator
    public MatchControllerImpl(
            @JsonProperty("players") final List<Player> players,
            @JsonProperty("gameBoard") final Board gameBoard,
            @JsonProperty("propertyController") final PropertyController propertyController,
            @JsonProperty("boardController") final BoardController boardController,
            @JsonProperty("currentPlayerIndex") final int currentPlayerIndex,
            @JsonProperty("consecutiveDoubles") final int consecutiveDoubles,
            @JsonProperty("hasRolled") final boolean hasRolled,
            @JsonProperty("jailTurnCounter") final Map<String, Integer> jailTurnCounterJson,
            @JsonProperty("diceThrow") final DiceThrow diceThrow,
            @JsonProperty("playersBankrupt") final List<Player> playersBankrupt) {
        this.players = players != null ? List.copyOf(players) : new ArrayList<>();
        this.gameBoard = gameBoard != null ? gameBoard : new BoardImpl(new ArrayList<>());
        this.propertyController = propertyController != null ? propertyController
                : new PropertyControllerImpl(new HashMap<>());
        this.liquidationObserver = new LiquidationObserverImpl(this);
        this.economyController = new EconomyControllerImpl(this.propertyController);
        this.economyController.setLiquidationObserver(this.liquidationObserver);
        this.boardController = boardController;
        this.diceThrow = diceThrow != null ? diceThrow : new DiceThrow(new DiceImpl(), new DiceImpl());
        this.playersBankrupt = playersBankrupt;

        this.gui = new MainView(this);
        this.currentPlayerIndex = currentPlayerIndex;
        this.consecutiveDoubles = consecutiveDoubles;
        this.hasRolled = hasRolled;
        if (jailTurnCounterJson != null) {
            for (final Map.Entry<String, Integer> entry : jailTurnCounterJson.entrySet()) {
                final String playerName = entry.getKey();
                final Player player = this.players.stream()
                        .filter(p -> p.getName().equals(playerName))
                        .findFirst()
                        .orElse(null);
                if (player != null) {
                    this.jailTurnCounter.put(player, entry.getValue());
                }
            }
        }
        for (final Player p : this.players) {
            p.addObserver(this);
        }
    }

    /**
     * Returns a JSON-compatible map of the jail turn counter.
     * 
     * @return map of player names and turn counts.
     */
    @JsonGetter("jailTurnCounter")
    public Map<String, Integer> getJailTurnCounterJson() {
        final Map<String, Integer> result = new HashMap<>();
        for (final Map.Entry<Player, Integer> entry : this.jailTurnCounter.entrySet()) {
            result.put(entry.getKey().getName(), entry.getValue());
        }
        return result;
    }

    /**
     * Starts the game.
     * Updates GUI and announces the first player.
     */
    @Override
    public void startGame() {
        updateGui(g -> {
            g.addLog("Game started");
            g.refreshAll();
            g.addLog("It's " + getCurrentPlayer().getName() + "'s turn");
        });
    }

    /**
     * Advances to the next player's turn.
     * Updates GUI and logs new turn.
     */
    @Override
    public void nextTurn() {
        do {
            this.currentPlayerIndex = (this.currentPlayerIndex + 1) % this.players.size();
        } while (getCurrentPlayer().getState() instanceof BankruptState);

        this.hasRolled = false;
        this.consecutiveDoubles = 0;

        final Player current = getCurrentPlayer();

        updateGui(g -> {
            g.addLog("Now it's " + current.getName() + "'s turn");
            g.refreshAll();
        });

        checkWinCondition();
    }

    /**
     * Handles the logic when the current player throws the dice.
     */
    @Override
    public void handleDiceThrow() {

        if (this.hasRolled) {
            return;
        }

        final Player currentPlayer = getCurrentPlayer();
        diceThrow.throwAll();
        final boolean isDouble = diceThrow.isDouble();

        if (currentPlayer.getState() instanceof JailedState) {
            final int turns = jailTurnCounter.getOrDefault(currentPlayer, 0);
            if (isDouble) {
                updateGui(g -> g.addLog(currentPlayer.getName() + " leaves jail with a DOUBLE ("
                        + this.diceThrow.getLastThrow() + ")!"));
                currentPlayer.setState(FreeState.getInstance());
                jailTurnCounter.remove(currentPlayer);
            } else if (turns >= 2) {
                updateGui(g -> g.addLog(currentPlayer.getName() + " fails the 3rd attempt. Pays €50 and leaves jail!"));
                economyController.withdrawFromPlayer(currentPlayer, JAIL_EXIT_FEE);
                currentPlayer.setState(FreeState.getInstance());
                jailTurnCounter.remove(currentPlayer);
            } else {
                jailTurnCounter.put(currentPlayer, turns + 1);
                updateGui(g -> g.addLog(currentPlayer.getName() + " remains in jail (Attempt " + (turns + 1) + "/3)"));
                this.hasRolled = true;
                return;
            }
        }

        updateGui(g -> g.addLog(currentPlayer.getName() + " throws: " + this.diceThrow.getLastThrow()
                + (isDouble ? " (DOUBLE!)" : "")));
        this.hasRolled = true;
        if (isDouble && !(currentPlayer.getState() instanceof JailedState)) {
            this.consecutiveDoubles++;
            if (this.consecutiveDoubles == MAX_DOUBLES) {
                updateGui(g -> g.addLog("3 doubles in a row! Go to jail."));
                handlePrison();
                return;
            }
        } else {
            this.consecutiveDoubles = 0;
        }

        this.handleMove(this.diceThrow.getLastThrow());
        if (isDouble && this.consecutiveDoubles < MAX_DOUBLES) {
            this.hasRolled = false;
        }
        updateGui(g -> g.refreshAll());
    }

    /**
     * Moves the current player.
     * 
     * @param steps number of steps.
     */
    public void handleMove(final int steps) {
        final Player currentPlayer = getCurrentPlayer();
        final int oldPos = currentPlayer.getCurrentPosition();

        final int newPos = this.boardController.movePlayer(currentPlayer, steps).getPosition();
        currentPlayer.setPosition(newPos);

        this.onPlayerMoved(currentPlayer, oldPos, newPos);

        updateGui(g -> g.refreshAll());
    }

    /**
     * Sends the current player to prison.
     */
    public void handlePrison() {
        final Player currentPlayer = getCurrentPlayer();

        currentPlayer.setPosition(this.boardController.sendPlayerToJail(currentPlayer).getPosition());

        updateGui(g -> {
            g.refreshAll();
        });
    }

    /**
     * Handles the logic after a player has moved to a new position.
     *
     * @param player the player who moved.
     * @param oldPosition the previous position of the player.
     * @param newPosition the current position of the player.
     */
    @Override
    public void onPlayerMoved(final Player player, final int oldPosition, final int newPosition) {
        final Tile currentTile = this.boardController.executeTileLogic(player, newPosition,
                this.diceThrow.getLastThrow());

        if (newPosition != currentTile.getPosition()) {
            player.setPosition(currentTile.getPosition());
        }

        handlePropertyLanding();

        final String msg = boardController.getMessagePrint();

        updateGui(g -> {
            if (currentTile instanceof UnexpectedTile) {
                if (msg != null && !msg.isEmpty()) {
                    g.showCard("CHANCE", msg, true);
                }
            }

            if (msg != null && !msg.isEmpty()) {
                g.addLog(msg);
            }
            g.refreshAll();
        });
    }

    /**
     * Allows the current player to pay the required fee to exit jail.
     */
    @Override
    public void payToExitJail() {
        final Player p = getCurrentPlayer();
        if (p.getState() instanceof JailedState && (economyController.withdrawFromPlayer(p, JAIL_EXIT_FEE))) {
            p.setState(FreeState.getInstance());
            jailTurnCounter.remove(p);
            updateGui(g -> {
                g.addLog(p.getName() + " pays 50€ and is now free!");
                g.refreshAll();
            });
            return;
        }

        if (p.getState() != BankruptState.getInstance()) {
            p.setState(FreeState.getInstance());
            jailTurnCounter.remove(p);
            updateGui(g -> {
                g.addLog(p.getName() + " pays 50€ and is now free!");
                g.refreshAll();
            });
            return;
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updatePlayerBankrupt() {
        final Player currentPlayer = this.getCurrentPlayer();
        if (currentPlayer.getState() instanceof BankruptState) {
            this.players.remove(currentPlayer);
            this.players.add(currentPlayer);
        }
    }

    /**
     * Returns the list of players.
     *
     * @return the list of players.
     */
    @Override
    public List<Player> getPlayers() {
        return this.players;
    }

    /**
     * Returns the current player.
     *
     * @return the current player.
     */
    @Override
    public Player getCurrentPlayer() {
        return this.players.get(this.currentPlayerIndex);
    }

    /**
     * Returns the board.
     *
    /** @return the game board. */
    @Override
    @JsonIgnore
    public Board getBoard() {
        return this.gameBoard;
    }

    /**
     * Returns the main view.
     *
    /** @return the main view. */
    @Override
    @JsonIgnore
    public MainView getMainView() {
        return this.gui;
    }

    /**
     * Notifies the controller that a player's balance has changed.
     *
     * @param player the player whose balance changed.
     * @param newBalance the new balance value.
     */
    @Override
    public void onBalanceChanged(final Player player, final int newBalance) {
        updateGui(g -> g.refreshAll());
    }

    /**
     * Notifies the controller that a player's state has changed.
     *
     * @param player the player whose state changed.
     * @param oldState the previous state.
     * @param newState the new state.
     */
    @Override
    public void onStateChanged(final Player player, final PlayerState oldState, final PlayerState newState) {
        updateGui(g -> {
            g.addLog(player.getName() + " is now in state: " + newState.getClass().getSimpleName());
            g.refreshAll();
        });
    }

    // #region public method
    /**
     * Returns the index.
     *
     * @return the current player index.
     */
    public int getCurrentPlayerIndex() {
        return this.currentPlayerIndex;
    }

    /**
     * Returns doubles count.
     *
     * @return the number of consecutive doubles.
     */
    public int getConsecutiveDoubles() {
        return this.consecutiveDoubles;
    }

    /**
     * Sets index.
     *
     * @param i the new current player index.
     */
    public void setCurrentPlayerIndex(final int i) {
        this.currentPlayerIndex = i;
    }

    /**
     * Sets doubles count.
     *
     * @param d the new number of consecutive doubles.
     */
    public void setConsecutiveDoubles(final int d) {
        this.consecutiveDoubles = d;
    }

    /**
     * Sets rolled flag.
     *
     * @param b set if player has rolled.
     */
    public void setHasRolled(final boolean b) {
        this.hasRolled = b;
    }

    /**
     * Checks roll possibility.
     *
     * @return true if player can roll.
     */
    public boolean canCurrentPlayerRoll() {
        return !hasRolled;
    }

    /**
     * Returns jail counter.
     *
     * @return the jail turn counter map.
     */
    public Map<Player, Integer> getJailTurnCounter() {
        return this.jailTurnCounter;
    }

    /**
     * Returns economy controller.
     *
     * @return the economy controller.
     */
    public EconomyController getEconomyController() {
        return this.economyController;
    }

    /**
     * Returns property controller.
     *
     * @return the property controller.
     */
    public PropertyController getPropertyController() {
        return this.propertyController;
    }

    /**
     * Logic for buying the current property.
     */
    public void buyCurrentProperty() {
        final Player currentPlayer = getCurrentPlayer();
        final Tile currentTile = gameBoard.getTileAt(currentPlayer.getCurrentPosition());

        if (currentTile instanceof PropertyTile pt) {
            final Property prop = pt.getProperty();

            if (prop.getIdOwner() != null && !prop.getIdOwner().isEmpty() && !prop.getIdOwner().equals("BANK")) {
                updateGui(g -> g.addLog("You cannot buy a property that already has an owner!"));
                return;
            }

            if (this.economyController.purchaseProperty(currentPlayer, prop)) {
                updateGui(g -> {
                    g.addLog(currentPlayer.getName() + " purchased " + prop.getCard().getName() + " for € "
                            + prop.getPurchasePrice());
                    g.refreshAll();
                });
            } else {
                updateGui(g -> g.addLog("You don't have enough money to buy " + prop.getId()));
            }
        }
    }

    /**
     * Logic for building a house.
     * 
     * @param property the property to build on.
     */
    public void buildHouseOnProperty(final Property property) {
        try {
            if (this.economyController.purchaseHouse(getCurrentPlayer(), property)) {
                updateGui(g -> {
                    g.addLog("Built a house on " + property.getId());
                    g.refreshAll();
                });
            } else {
                updateGui(g -> g.addLog("Cannot build on " + property.getId()));
            }
        } catch (final IllegalStateException e) {
            updateGui(g -> g.addLog("Error: " + e.getMessage()));

        } catch (final IllegalArgumentException e) {
            updateGui(g -> g.addLog("You cannot build on this type of tile."));
        }
    }

    /**
     * Finalizes the liquidation process.
     * 
     * @param p the player.
     */
    public void finalizeLiquidation(final Player p) {
        if (p.getBalance() >= 0) {
            updateGui(g -> {
                g.addLog("✅ Debt settled! " + p.getName() + " can continue.");
                g.refreshAll();
            });
            this.currentCreditor = null;
        } else {
            this.liquidationObserver.onBankruptcyDeclared(p, this.currentCreditor, Math.abs(p.getBalance()));
            this.currentCreditor = null;
        }
    }

    /**
     * Restores the jail counter state.
     * 
     * @param map     the data map.
     * @param players the list of players.
     */
    public void restoreJailTurnCounter(final Map<String, Integer> map, final List<Player> players) {
        this.jailTurnCounter.clear();
        for (final Map.Entry<String, Integer> entry : map.entrySet()) {
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

    // #endregion

    // #region Private method
    /**
     * Checks if only one player remains active and declares the winner.
     */
    private void checkWinCondition() {
        final List<Player> activePlayers = players.stream()
                .filter(p -> !(p.getState() instanceof BankruptState))
                .toList();
        if (activePlayers.size() == 1) {
            final Player winner = activePlayers.get(0);
            updateGui(g -> {
                g.addLog("🏆 GAME OVER! The winner is " + winner.getName());
                g.showWinner(winner.getName());
            });
        }
    }

    /**
     * Safely updates the GUI using the JavaFX Platform thread.
     * * @param action the consumer action to perform on the MainView.
     */
    private void updateGui(final Consumer<MainView> action) {
        if (this.gui != null)
            Platform.runLater(() -> action.accept(this.gui));
    }

    /**
     * Handles actions when a player lands on a property.
     * For now, just logs the event.
     */
    private void handlePropertyLanding() {
        final Player currentPlayer = getCurrentPlayer();
        final Tile currentTile = gameBoard.getTileAt(currentPlayer.getCurrentPosition());

        if (currentTile instanceof PropertyTile) {
            final Property prop = ((PropertyTile) currentTile).getProperty();
            if (prop.getIdOwner() == null) {
                updateGui(g -> g.addLog("You can buy " + prop.getId() + " for €" + prop.getPurchasePrice()));
            } else if (prop.getIdOwner().equals(currentPlayer.getName())) {
                updateGui(g -> {
                    g.addLog("You are at home (" + prop.getId() + ").");
                });
            }
            updateGui(g -> g.refreshAll());
        }

    }

    // #endregion

}
