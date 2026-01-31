package it.unibo.javapoly.controller.api;
    
public interface MatchController {
    public void startGame();
    public void nextTurn();
    public Player getCurrentPlayer();
    public void handleDiceThrow();
    public void handleMove(int steps);
    public void handlePrison();
    public void handlePropertyLanding();
}
