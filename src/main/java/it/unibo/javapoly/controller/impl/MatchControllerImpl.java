package it.unibo.javapoly.controller.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.*;

import it.unibo.javapoly.controller.api.BoardController;
import it.unibo.javapoly.controller.api.EconomyController;
import it.unibo.javapoly.controller.api.LiquidationObserver;
import it.unibo.javapoly.controller.api.MatchController;
import it.unibo.javapoly.controller.api.PropertyController;
import it.unibo.javapoly.model.api.Player;
import it.unibo.javapoly.model.api.PlayerState;
import it.unibo.javapoly.model.api.board.Board;
import it.unibo.javapoly.model.api.board.Tile;
import it.unibo.javapoly.model.api.board.TileType;
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
@JsonIgnoreProperties(value = {"gui","economyController", "mainView", ""}, ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class MatchControllerImpl implements MatchController{
    private static final int MAX_DOUBLES = 3;
    private static final int JAIL_EXIT_FEE = 50;

    private final List<Player> players;
    private final DiceThrow diceThrow;
    private final Board gameBoard;
    @JsonIgnore
    private final MainView gui;
    private final Map<Player, Integer> jailTurnCounter = new HashMap<>();

    @JsonIgnore
    private final EconomyController economyController;
    private final PropertyController propertyController;
    @JsonIgnore
    private final BoardController boardController;

    private int currentPlayerIndex;
    private int consecutiveDoubles;
    private boolean hasRolled = false;
    private Player currentCreditor;
    @JsonIgnore
    private LiquidationObserver liquidationObserver;

    /**
     * Constructor
     *
     * @param players   list of players (already created)
     * @param gameBoard the game board implementation
     * @param bank      the bank implementation
     */
    @JsonIgnoreProperties({"gui", "economyController"})
    public MatchControllerImpl(final List<Player> players, final Board gameBoard, final Map<String, Property> properties){
        this.players = List.copyOf(players);
        this.gameBoard = Objects.requireNonNull(gameBoard);
        this.propertyController = new PropertyControllerImpl(properties);
        this.economyController = new EconomyControllerImpl(propertyController, players);
        this.boardController = new BoardControllerImpl(gameBoard, economyController, propertyController);
        this.liquidationObserver = new LiquidationObserverImpl(this);
        this.economyController.setLiquidationObserver(this.liquidationObserver);
        this.diceThrow = new DiceThrow(new DiceImpl(), new DiceImpl());
        this.gui = new MainView(this);
        this.currentPlayerIndex = 0;
        this.consecutiveDoubles = 0;

        for (Player p : this.players) {
            p.addObserver(this);
        }
    }

    @JsonCreator
    public MatchControllerImpl(
            @JsonProperty("players")
            final List<Player> players,
            @JsonProperty("gameBoard")
            final Board gameBoard,
            @JsonProperty("propertyController")
            final PropertyController propertyController,
            @JsonProperty("currentPlayerIndex")
            int currentPlayerIndex,
            @JsonProperty("consecutiveDoubles")
            int consecutiveDoubles,
            @JsonProperty("hasRolled")
            boolean hasRolled,
            @JsonProperty("jailTurnCounter")
            Map<String, Integer> jailTurnCounterJson,
            @JsonProperty("diceThrow")
            DiceThrow diceThrow) {
        this.players = players != null ? List.copyOf(players) : new ArrayList<>();
        this.gameBoard = gameBoard != null ? gameBoard : new BoardImpl(new ArrayList<>());
        this.propertyController = propertyController != null ? propertyController : new PropertyControllerImpl(new HashMap<>());
        this.liquidationObserver = new LiquidationObserverImpl(this);
        this.economyController = new EconomyControllerImpl(this.propertyController, this.players);
        this.economyController.setLiquidationObserver(this);
        this.boardController = new BoardControllerImpl(this.gameBoard, this.economyController, this.propertyController);
        this.diceThrow = diceThrow != null ? diceThrow : new DiceThrow(new DiceImpl(), new DiceImpl());
        this.gui = new MainView(this);
        this.currentPlayerIndex = currentPlayerIndex;
        this.consecutiveDoubles = consecutiveDoubles;
        this.hasRolled = hasRolled;
        if (jailTurnCounterJson != null) {
            for (Map.Entry<String, Integer> entry : jailTurnCounterJson.entrySet()) {
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
        for (Player p: this.players) {
            p.addObserver(this);
        }
    }

    @JsonGetter("jailTurnCounter")
    public Map<String, Integer> getJailTurnCounterJson() {
        Map<String, Integer> result = new HashMap<>();
        for (Map.Entry<Player, Integer> entry : this.jailTurnCounter.entrySet()) {
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
        do{
            this.currentPlayerIndex = (this.currentPlayerIndex + 1) % this.players.size();
        }while(getCurrentPlayer().getState() instanceof BankruptState);

        this.hasRolled = false;
        this.consecutiveDoubles = 0;

        final Player current = getCurrentPlayer();

        updateGui(g -> {
            g.addLog("Ora e' il turno di: " + current.getName());
            g.refreshAll();
        });

        checkWinCondition();
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
        diceThrow.throwAll();
        final boolean isDouble = diceThrow.isDouble();

        if(currentPlayer.getState() instanceof JailedState){
            int turns = jailTurnCounter.getOrDefault(currentPlayer, 0);
            if(isDouble){
                updateGui(g -> g.addLog(currentPlayer.getName() + " esce col DOPPIO (" + this.diceThrow.getLastThrow() + ")!"));
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

        updateGui(g -> g.addLog(currentPlayer.getName() + " lancia: " + this.diceThrow.getLastThrow() + (isDouble ? " (DOPPIO!)" : "")));

        if(isDouble && !(currentPlayer.getState() instanceof JailedState)){
            this.consecutiveDoubles++;
            if(this.consecutiveDoubles == MAX_DOUBLES){
                updateGui(g -> g.addLog("3 doppi! In prigione."));
                handlePrison();
                this.hasRolled = true;
                return;
            }else {
                this.hasRolled = false;
                updateGui(g -> g.addLog("Puoi lanciare ancora!"));
            }
        } else {
            this.consecutiveDoubles = 0;
            this.hasRolled = true;
        }

        this.handleMove(this.diceThrow.getLastThrow());
        //this.boardController.movePlayer(currentPlayer, this.lastDiceResult);
    }

    /**
     * Moves the current player by 'steps' spaces on the board.
     * Updates board and info panels.
     *
     * @param steps number of spaces to move
     */
    public void handleMove(int steps) {
        final Player currentPlayer = getCurrentPlayer();
        int oldPos = currentPlayer.getCurrentPosition();

        int newPos = this.boardController.movePlayer(currentPlayer, steps).getPosition();
        currentPlayer.setPosition(newPos);

        this.onPlayerMoved(currentPlayer, oldPos, newPos);

        updateGui(g -> g.refreshAll());
    }

    /**
     * Handles the logic when a player is in prison.
     * For simplicity, we can just log it here.
     */
    public void handlePrison() {
        final Player currentPlayer = getCurrentPlayer();

        currentPlayer.setPosition(this.boardController.sendPlayerToJail(currentPlayer).getPosition());

        updateGui(g -> {
            g.refreshAll();
        });
    }

    @Override
    public void onPlayerMoved(Player player, int oldPosition, int newPosition) {
        updateGui(g -> g.refreshAll());

        final Tile currentTile = gameBoard.getTileAt(newPosition);

        this.boardController.executeTileLogic(player, currentTile.getPosition(), this.diceThrow.getLastThrow());

        if(currentTile instanceof PropertyTile pt){
            this.currentCreditor = players.stream()
                                          .filter(p -> p.getName().equals(pt.getProperty().getIdOwner()))
                                          .findFirst()
                                          .orElse(null);
            handlePropertyLanding();
            /*
            Property prop = pt.getProperty();
            String ownerId = prop.getIdOwner();

            this.currentCreditor = players.stream()
                                        .filter(p -> p.getName().equals(ownerId))
                                        .findFirst()
                                        .orElse(null);
            if(ownerId != null && ownerId.equals(player.getName())){
                if(this.propertyController.checkPayRent(player, prop.getId())){
                    this.economyController.payRent(player, ownerId, prop, this.lastDiceResult);
                }
            } */
        }else {
            this.currentCreditor = null;

            if(currentTile instanceof UnexpectedTile){
                String cardDescription = boardController.getMessagePrint();
                if(cardDescription != null && !cardDescription.isEmpty()){
                    int pos = player.getCurrentPosition();
                    boolean isImprevisto = !(pos == 2 || pos == 17 || pos == 33);
                    updateGui(g -> {
                        g.showCard(isImprevisto ? "IMPREVISTO" : "PROBABILITÀ", cardDescription, isImprevisto);
                        g.addLog("[" + (isImprevisto ? "Imprevisto" : "Probabilità") + "] " + cardDescription);
                        g.refreshAll();
                    });
                }
            }
        }

        updateGui(g -> {
            g.refreshAll();
            String msg = boardController.getMessagePrint();
            if (msg != null && !msg.isEmpty()) g.addLog(msg);
        });
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

    @Override
    public List<Player> getPlayers(){
        return this.players;
    }

    @Override
    public Player getCurrentPlayer() {
        return this.players.get(this.currentPlayerIndex);
    }

    @Override
    @JsonIgnore
    public Board getBoard(){
        return this.gameBoard;
    }



    @Override
    @JsonIgnore
    public MainView getMainView(){
        return this.gui;
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

    //#region public method
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

    public boolean canCurrentPlayerRoll(){
        return !hasRolled;
    }

    public Map<Player, Integer> getJailTurnCounter() {
        return this.jailTurnCounter;
    }

    public EconomyController getEconomyController() {
        return this.economyController;
    }

    public PropertyController getPropertyController() {
        return this.propertyController;
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

    public void finalizeLiquidation(Player p){
        if(p.getBalance() >= 0){
            updateGui(g -> {
                g.addLog("✅ Debito saldato! " + p.getName() + " può continuare.");
                g.refreshAll();
            });
            this.currentCreditor = null;
        }else{
            this.onBankruptcyDeclared(p, this.currentCreditor, Math.abs(p.getBalance()));
            this.currentCreditor = null;
        }
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

    //#endregion


    //#region Private method
    private void checkWinCondition(){
        List<Player> activePlayers = players.stream()
                                            .filter(p -> !(p.getState() instanceof BankruptState))
                                            .toList();
        if(activePlayers.size() == 1){
            Player winner = activePlayers.get(0);
            updateGui(g -> {
                g.addLog("🏆 PARTITA FINITA! Il vincitore è " + winner.getName());
            });
        }
    }

    private void updateGui(Consumer<MainView> action) {
        if (this.gui != null) Platform.runLater(() -> action.accept(this.gui));
    }

    /**
     * Handles actions when a player lands on a property.
     * For now, just logs the event.
     */
    private void handlePropertyLanding() {
        final Player currentPlayer = getCurrentPlayer();
        final Tile currentTile = gameBoard.getTileAt(currentPlayer.getCurrentPosition());

        if(currentTile instanceof PropertyTile){
            Property prop =  ((PropertyTile) currentTile).getProperty();
            if(prop.getIdOwner() == null){
                updateGui(g -> g.addLog("Puoi acquistare " + prop.getId() + " per " + prop.getPurchasePrice() + "€"));
            }else if(prop.getIdOwner().equals(currentPlayer.getName())){
                updateGui(g -> {
                    g.addLog("Sei a casa tua (" + prop.getId() + ").");
                    // Qui la GUI potrebbe abilitare il tasto "Costruisci"
                    // se l'EconomyController.canBuild (o simile) è true
                });
            }
        }
        updateGui(g -> g.refreshAll());
    }

    @Override
    public void handleEndTurn() {

    }
    // LiquidationObserver implementations
    @Override
    public void onInsufficientFunds(Player player, int amount) {
        if (this.liquidationObserver != null) {
            this.liquidationObserver.onInsufficientFunds(player, amount);
        }
    }

    @Override
    public void onBankruptcyDeclared(Player payer, Player payee, int amount) {
        if (this.liquidationObserver != null) {
            this.liquidationObserver.onBankruptcyDeclared(payer, payee, amount);
        }
    }

    //#endregion

}
