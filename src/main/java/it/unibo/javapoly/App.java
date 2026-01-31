package it.unibo.javapoly;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.unibo.javapoly.model.impl.Card.LandProprietyCard;
import it.unibo.javapoly.model.impl.Card.ProprietyCard;

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

                // The library JACKSON: is a json formatter for data
                //implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
            
            List<Integer> rents = List.of(10, 30, 90, 160, 250);

            ProprietyCard p = new LandProprietyCard(
                "VIA_ROMA",
                "Via Roma",
                "Descrizione", 100,
                "RED", 10, rents, 10, 10, 10
            );

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(p);

            System.out.println(json);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
