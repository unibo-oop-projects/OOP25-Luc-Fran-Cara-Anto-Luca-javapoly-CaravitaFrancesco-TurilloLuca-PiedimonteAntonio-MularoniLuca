package it.unibo.javapoly;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;

import it.unibo.javapoly.controller.api.Bank;
import it.unibo.javapoly.controller.api.MatchController;
import it.unibo.javapoly.controller.impl.MatchControllerImpl;
import it.unibo.javapoly.model.api.TokenType;
import it.unibo.javapoly.model.api.board.Board;
import it.unibo.javapoly.model.api.card.CardType;
import it.unibo.javapoly.model.api.property.PropertyGroup;
import it.unibo.javapoly.model.impl.PlayerImpl;
import it.unibo.javapoly.model.impl.Card.AbstractPropertyCard;
import it.unibo.javapoly.model.impl.Card.LandPropertyCard;
import it.unibo.javapoly.model.impl.board.BoardImpl;
import it.unibo.javapoly.utils.JsonUtils;
import javafx.application.Application;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Main application entry-point's class.
 */
public final class App extends Application{

    @Override
    public void start(Stage primaryStage) {
        // 1. Crei il Modello (Board, Bank, Giocatori)
        Board board = new BoardImpl(); // La tua implementazione
        Bank bank = new BankImpl();   
        List<PlayerImpl> players = List.of(
            new PlayerImpl("Gigi", 100, TokenType.CAR),
            new PlayerImpl("Mario", 120, TokenType.DOG)
        );

        // 2. Crei il Controller passandogli i dati
        MatchController controller = new MatchControllerImpl(players, board, bank);

        // 3. Avvii il gioco tramite il controller
        controller.startGame(); 
        
        // Nota: Se MainView (nel controller) apre già la finestra, sei a posto.
        // Altrimenti dovresti aggiungere la scena al primaryStage qui.
    }

    public static void main(final String[] args) {
        launch(args); // Questo fa partire JavaFX
    }
}