package it.unibo.javapoly.view.impl;

import java.util.Objects;

import it.unibo.javapoly.model.api.board.Board;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * BoardPanel handles the visual representation of the board.
 */
public class BoardPanel {

    private final StackPane root;
    private final Board board;

    public BoardPanel(final Board board){
        this.board = Objects.requireNonNull(board);
        this.root = new StackPane();

        //da vedere, e' solo una prova
        // Esempio: Disegna un rettangolo per rappresentare il tabellone
        final Rectangle rect = new Rectangle(400, 400, Color.LIGHTGREEN);
        rect.setStroke(Color.BLACK);
        this.root.getChildren().add(rect);
    }

    /** @return the visual root of the board. */
    public Pane getRoot() {
        return this.root;
    }

    /** Updates the view based on current model state. */
    public void update() {
        // Qui andrà la logica per spostare i cerchi/token dei giocatori
        // Per ora Checkstyle è contento perché il metodo esiste!
    }
}
