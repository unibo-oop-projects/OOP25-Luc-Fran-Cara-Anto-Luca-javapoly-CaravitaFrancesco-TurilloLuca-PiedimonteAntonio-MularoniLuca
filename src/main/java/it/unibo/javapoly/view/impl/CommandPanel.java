package it.unibo.javapoly.view.impl;

import javax.swing.JButton;
import javax.swing.JPanel;

import it.unibo.javapoly.controller.api.MatchController;

/**
 * CommandPanel contains the buttons for player actions,
 * such as throwing the dice and ending the turn.
 */
public class CommandPanel extends JPanel {

    private final MatchController matchController;
    JButton throwDice;
    JButton endTurnButton;

    /**
     * Constructor: creates the panel and its buttons.
     *
     * @param matchController the controller that handles game logic
     */
    public CommandPanel(MatchController matchController) {
        this.matchController = matchController;

        throwDice = new JButton("Lancia dadi");
        endTurnButton = new JButton("Fine turno");

        throwDice.addActionListener(e -> matchController.handleDiceThrow());
        endTurnButton.addActionListener(e -> matchController.nextTurn());

        add(throwDice);
        add(endTurnButton);
    }
}
