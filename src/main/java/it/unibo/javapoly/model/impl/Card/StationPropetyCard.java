package it.unibo.javapoly.model.impl.card;

/**
 * Representation of a station card in the Monopoly game.
 * 
 * <p>
 * The class stores the rents for different numbers of station
 */
public class StationPropetyCard extends AbstractPropertyCard{

    public StationPropetyCard(String id, String name, String description, int propetyCost, String group) {
        super(id, name, description, propetyCost, group);
        //TODO Auto-generated constructor stub
    }

    @Override
    public int calculateRent(int number) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'calculateRent'");
    }
    
}
