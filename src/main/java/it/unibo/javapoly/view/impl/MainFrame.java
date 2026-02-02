package it.unibo.javapoly.view.impl;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import it.unibo.javapoly.controller.impl.MatchControllerImpl;

/**
 * MainFrame is the main window of the JavaPoly game.
 * It contains the game board, player info, action buttons, and a log area.
 */
public class MainFrame extends JFrame {

    BoardPanel boardPanel; // panel displaying the game board
    CommandPanel commandPanel; // panel with action buttons (throw dice, end turn)
    InfoPanel infoPanel; // panel showing player information
    JTextArea logArea; // text area for game messages/log
    MatchControllerImpl matchControllerImpl;

    public MainFrame(MatchControllerImpl matchControllerImpl) {
        this.matchControllerImpl = matchControllerImpl;
        setTitle("JavaPoly");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setExtendedState(JFrame.MAXIMIZED_BOTH); // maximize the window to fill the entire screen
        setLayout(new BorderLayout());

        boardPanel = new BoardPanel(matchControllerImpl.getBoard()); // shows the game board and player tokens
        commandPanel = new CommandPanel(matchControllerImpl); // contains buttons for throwing dice and ending turn
        infoPanel = new InfoPanel(matchControllerImpl); // shows current player info (name, money, position)

        logArea = new JTextArea(); // create text area for logs
        logArea.setEditable(false); // set not editable the log area
        JScrollPane logScroll = new JScrollPane(logArea); // make log area scrollable
        logScroll.setPreferredSize(new Dimension(getWidth(), 100)); // fixed Dimension

        add(boardPanel, BorderLayout.CENTER);
        add(commandPanel, BorderLayout.SOUTH);
        add(infoPanel, BorderLayout.EAST);
        add(logScroll, BorderLayout.NORTH);

        pack();
        setVisible(true);
    }

    /**
     * Redraws the BoardPanel to reflect current player positions.
     */
    public void updateBoard() {
        boardPanel.repaint();
    }

    /**
     * Updates InfoPanel to reflect current player info (money, position, etc.).
     */
    public void updateInfo() {
        infoPanel.updateInfo();
    }

    /**
     * Adds a new message to the log area.
     *
     * @param msg The message to append.
     */
    public void addLog(String msg) {
        logArea.append(msg + "\n");
    }
}
