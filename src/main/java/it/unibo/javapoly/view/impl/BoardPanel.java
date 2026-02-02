package it.unibo.javapoly.view.impl;

import javax.swing.JPanel;

import it.unibo.javapoly.controller.api.GameBoard;

public class BoardPanel extends JPanel {
    private final GameBoard board;

    public BoardPanel(GameBoard board) {
        this.board = board;
    }
}
