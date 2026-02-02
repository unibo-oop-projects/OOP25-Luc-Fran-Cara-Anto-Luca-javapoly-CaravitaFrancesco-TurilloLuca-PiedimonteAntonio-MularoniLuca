package it.unibo.javapoly.model.impl.card;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

// TODO: add all the JavaDoc comment

public class UtilityPropertyCard extends AbstractPropertyCard{

    // TODO finire di aggiustare i CDC di questa classe
    final Map<Integer, Integer> multiplierRent;


    public UtilityPropertyCard(final String id, final String name, final String description, final int propertyCost,
                             final String utility, final Map<Integer, Integer> multiProroprietyCost) {
        super(id, name, description, propertyCost, utility);
        this.multiplierRent = new HashMap<>(multiProroprietyCost);

    }

    /**
     * This method return all rent multplier in base of the utility owned
     * 
     * @return a copy of the full map of multiplier
     */
    public Map<Integer, Integer> getAllMultiplier(){

        if(checkMapIsEmpty()){
            throw new NoSuchElementException("The multplier map is empty"); // TODO: Valutare se restituire un errore o semplicemente restituire una lista vuota
        }

        return new HashMap<>(this.multiplierRent);
    }

    /**
     * 
     * @return return the Multiplier in base of the utility number owned
     */
    @Override
    public int calculateRent(int utilityNumber) {
        return 0;
    }
    


    /**
     * 
     * @return true if the this.multiplier is a empty map, false otherwise
     */
    private boolean checkMapIsEmpty(){

        if(this.multiplierRent == null){
            throw new NullPointerException("The map with the rent multiplier is null");
        }

        if(this.multiplierRent.isEmpty()){
            return true;
        }

        return false;
    }
}
