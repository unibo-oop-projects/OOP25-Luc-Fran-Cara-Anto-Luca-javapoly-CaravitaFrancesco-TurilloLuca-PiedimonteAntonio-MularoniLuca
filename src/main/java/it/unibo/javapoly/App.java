package it.unibo.javapoly;

import java.util.List;

import it.unibo.javapoly.model.impl.Card.LandProprietyCard;
import it.unibo.javapoly.model.impl.Card.ProprietyCard;
import it.unibo.javapoly.utils.JsonUtils;

/** Main application entry-point's class. */

public final class App {
    private App() { }

    /**
     * Main application entry-point.
     *
     * @param args passed to JavaFX.
     */
    public static void main(final String[] args) {
        try {
            // ObjectMapper mapper = new ObjectMapper();
            // String json = mapper.writeValueAsString(Map.of("test", 1));
            // System.out.println(json);
            
            final List<Integer> rents = List.of(10, 35, 90, 170, 260);

            final ProprietyCard p = new LandProprietyCard(
                "VIA_ROMA",
                "Via Roma",
                "Descrizione", 100,
                "RED", 10, rents, 10, 10, 10
            );

            final String json = JsonUtils.mapper().writeValueAsString(p);
            System.out.println(json);

        } catch (final Exception e) {
            e.printStackTrace();
        }

    }
}
