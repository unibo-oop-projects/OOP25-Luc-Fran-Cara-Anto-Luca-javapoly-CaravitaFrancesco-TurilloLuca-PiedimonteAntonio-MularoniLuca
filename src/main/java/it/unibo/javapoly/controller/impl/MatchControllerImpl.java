package it.unibo.javapoly.controller.impl;

import java.util.List;

import it.unibo.javapoly.controller.api.Bank;
import it.unibo.javapoly.controller.api.GameBoardImpl;
import it.unibo.javapoly.controller.api.MatchController;
import it.unibo.javapoly.controller.api.Player;
import it.unibo.javapoly.model.impl.DiceImpl;
import it.unibo.javapoly.model.impl.DiceThrow;
import it.unibo.javapoly.view.impl.MainFrame;

public class MatchControllerImpl implements MatchController{
    private List<Player> players;    //implementazione esterna
    private int currentPlayerIndex;
    private DiceThrow diceThrow;
    private GameBoardImpl gameBoard; //implementazione esterna
    private Bank bank;               //implementazione esterna
    private MainFrame gui;

    
    public MatchControllerImpl(List<Player> players, GameBoardImpl gameBoard, Bank bank){
        this.players = players; //perche' la lista e' gia stata creata, cosi' la passo semplicemente
        this.currentPlayerIndex = 0;
        this.diceThrow = new DiceThrow(new DiceImpl(), new DiceImpl());
        this.gameBoard = gameBoard;
        this.bank = bank;
        this.gui = new MainFrame(this);
    }
    
    @Override
    public void startGame() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'startGame'");
    }

    @Override
    public void nextTurn() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'nextTurn'");
    }

    @Override
    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    @Override
    public void handleDiceThrow() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleDiceThrow'");
    }

    @Override
    public void handleMove(int steps) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleMove'");
    }

    @Override
    public void handlePrison() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handlePrison'");
    }

    @Override
    public void handlePropertyLanding() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handlePropertyLanding'");
    }
    
}
