package it.unibo.javapoly.model.impl.Card;

import java.util.List;

import it.unibo.javapoly.model.api.card.Card;

public class ProprietyCard implements Card{

    final String id;
    final String name;
    final String description;

    final String color;

    final int baseRent;
    final List<Integer> multiProroprietyCost;
    final int hotelRent;
    final int proprietyCost;

    final int houseCost;
    final int hotelCost;


    public ProprietyCard(final String id, final String name, final String description, 
        final String color, final int baseRent, final List<Integer> multiProroprietyCost, final int hotelRent, 
        final int proprietyCost, final int houseCost, final int hotelCost) {
        
        this.id = id;
        this.name = name;
        this.description = description;
        this.color = color;
        this.baseRent = baseRent;
        this.multiProroprietyCost = multiProroprietyCost;
        this.hotelRent = hotelRent;
        this.proprietyCost = proprietyCost;
        this.houseCost = houseCost;
        this.hotelCost = hotelCost;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    

    @Override
    public String toString() {
        return "ProprietyCard{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
    
}
