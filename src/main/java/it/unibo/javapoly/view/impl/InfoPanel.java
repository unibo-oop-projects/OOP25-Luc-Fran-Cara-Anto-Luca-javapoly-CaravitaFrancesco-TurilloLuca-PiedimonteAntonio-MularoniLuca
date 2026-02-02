package it.unibo.javapoly.view.impl;

import javax.swing.JLabel;
import javax.swing.JPanel;

import it.unibo.javapoly.controller.api.MatchController;
import it.unibo.javapoly.model.api.Player;

/**
 * InfoPanel displays information about the current player,
 * such as name, balance, and board position.
 */
public class InfoPanel extends JPanel {

    private final MatchController matchController;
    JLabel nameLabel; // shows player name
    JLabel balancLabel; // shows player balance
    JLabel positionLabel; // shows player position on the board

    /**
     * Constructor: creates labels and adds them to the panel.
     *
     * @param matchController reference to the game controller
     */
    public InfoPanel(MatchController matchController) {
        this.matchController = matchController;
        nameLabel = new JLabel();
        balancLabel = new JLabel();
        positionLabel = new JLabel();
        add(nameLabel);
        add(balancLabel);
        add(positionLabel);
        updateInfo();
    }

    /**
     * Updates the labels to show current player's info.
     */
    public void updateInfo() {
        Player p = matchController.getCurrentPlayer();
        nameLabel.setText("Giocatore: " + p.getName());
        balancLabel.setText("Saldo: "); // balance del giocatore(da aggiungere)
        positionLabel.setText("Posizione: "); // posizione del giocatore (da aggiungere)
    }
}
