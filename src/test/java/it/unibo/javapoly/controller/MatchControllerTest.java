package it.unibo.javapoly.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.javapoly.controller.impl.MatchControllerImpl;
import it.unibo.javapoly.model.api.TokenType;
import it.unibo.javapoly.model.api.board.Board;
import it.unibo.javapoly.model.api.board.Tile;
import it.unibo.javapoly.model.api.board.TileType;
import it.unibo.javapoly.model.api.property.Property;
import it.unibo.javapoly.model.impl.JailedState;
import it.unibo.javapoly.model.impl.PlayerImpl;
import it.unibo.javapoly.model.impl.board.BoardImpl;
import it.unibo.javapoly.model.impl.board.tile.AbstractTile;

public class MatchControllerTest {
    private MatchControllerImpl controller;
    private PlayerImpl p1;
    private PlayerImpl p2;

    @BeforeEach
    void setUp() {
        List<Tile> tiles = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            final int pos = i;
            tiles.add(new AbstractTile(pos, TileType.FREE_PARKING, "Tile " + pos, "Tile desc") {});
        }

        Board board = new BoardImpl(tiles);
        Map<String, Property> properties = new HashMap<>();
        p1 = new PlayerImpl("Gigi", 1500, TokenType.CAR);
        p2 = new PlayerImpl("Mario", 1500, TokenType.DOG);

        // PASSIAMO 'null' ALLA GUI: così JUnit non proverà ad aprire finestre!
        controller = new MatchControllerImpl(List.of(p1, p2), board, properties);
    }

    @Test
    void testTurnRotation() {
        // All'inizio deve toccare a p1
        assertEquals(p1, controller.getCurrentPlayer());
        
        // Passiamo il turno
        controller.nextTurn();
        
        // Ora deve toccare a p2
        assertEquals(p2, controller.getCurrentPlayer());
        
        // Passiamo ancora (torna a p1)
        controller.nextTurn();
        assertEquals(p1, controller.getCurrentPlayer());
    }

    @Test
    void testPassaggioDalVia() {
        // Posizioniamo p1 alla fine del tabellone
        p1.move(38); 
        int saldoIniziale = p1.getBalance();

        // Muoviamo di 4 passi (38 -> 39 -> 0 -> 1 -> 2)
        controller.handleMove(4);

        // Verifichiamo posizione e accredito dei 200€
        assertEquals(2, p1.getCurrentPosition());
        assertEquals(saldoIniziale + 200, p1.getBalance());
    }

    @Test
    void testGoToJailLogic() {
        // Simuliamo l'invio in prigione diretto
        controller.handlePrison();

        // Verifichiamo che sia alla casella 10 e in stato Jailed
        assertEquals(10, p1.getCurrentPosition());
        assertTrue(p1.getState() instanceof JailedState);
    }
}
