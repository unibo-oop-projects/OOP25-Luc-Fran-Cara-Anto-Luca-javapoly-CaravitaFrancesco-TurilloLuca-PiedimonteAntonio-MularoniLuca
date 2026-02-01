package it.unibo.javapoly.model.impl.Card;

import java.util.ArrayList;
import java.util.List;

// TODO: add all the JavaDoc comment

public class UtilityProprietyCard extends ProprietyCard{

    final int baseRent;

    // TODO finire di aggiustare i CDC di questa classe
    final List<Integer> multiProroprietyCost;


    public UtilityProprietyCard(final String id, final String name, final String description, final int proprietyCost,
                             final String utility, final int baseRent, final List<Integer> multiProroprietyCost,
                             final int hotelRent, final int houseCost, final int hotelCost) {
        super(id, name, description, proprietyCost, utility);
        this.baseRent = baseRent;
        this.multiProroprietyCost = new ArrayList<>(multiProroprietyCost);

    }

    @Override
    public int calculateRent(int utilityNumber) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'calculateRent'");
    }
    
}
