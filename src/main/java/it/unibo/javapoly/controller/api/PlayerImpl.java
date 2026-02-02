package it.unibo.javapoly.controller.api;

public class PlayerImpl implements Player{

    private String name;
    public PlayerImpl(String name){
        this.name = name;
    }
    @Override
    public String getName() {
        return this.name;
    }
    @Override
    public int move(int steps) {
        return steps;
    }
    
    

}
