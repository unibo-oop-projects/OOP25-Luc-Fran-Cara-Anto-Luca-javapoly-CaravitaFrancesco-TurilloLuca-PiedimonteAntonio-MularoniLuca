package it.unibo.javapoly.controller.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unibo.javapoly.controller.api.MatchController;
import it.unibo.javapoly.model.api.Player;
import it.unibo.javapoly.model.api.board.Board;
import it.unibo.javapoly.model.api.board.Tile;
import it.unibo.javapoly.model.api.property.Property;
import it.unibo.javapoly.model.impl.PlayerImpl;
import it.unibo.javapoly.model.impl.PlayerStateFactory;
import it.unibo.javapoly.model.impl.board.BoardImpl;
import it.unibo.javapoly.utils.BoardLoader;
import it.unibo.javapoly.utils.JsonUtils;
import it.unibo.javapoly.utils.ValidationUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static it.unibo.javapoly.controller.impl.MenuControllerImpl.PATH_BOARD_JSON;

/**
 * Utility class for serializing and deserializing game state snapshots.
 */
public final class MatchSnapshotter {

    private MatchSnapshotter() {
        //Prevent instantiation
    }

    /**
     * Serializes the current match state to snapshot.
     *
     * @param matchController the match controller to serialize.
     * @return a serializable {@link MatchSnapshot}.
     * @throws IllegalArgumentException if board is empty or controller is inconsistent.
     */
    public static MatchSnapshot toSnapshot(final MatchControllerImpl matchController) {
        ValidationUtils.requireNonNull(matchController, "MatchController cannot be null");
        final List<PlayerSnapshot> playersSnapshots = matchController.getPlayers().stream()
                .map(p -> new PlayerSnapshot(
                        p.getName(),
                        p.getBalance(),
                        p.getCurrentPosition(),
                        p.getTokenType(),
                        PlayerStateFactory.getClassName(p.getState()),
                        PlayerStateFactory.getTurnsInJail(p.getState())
                )).toList();
        final List<PropertySnapshot> propertySnapshots = new ArrayList<>();
        final Board board = matchController.getBoard();
        final int boardSize = board.size();
        ValidationUtils.requirePositive(boardSize, "Board size must be positive");
        for (int i = 0; i < boardSize; i++) {
            final Tile tile = matchController.getBoard().getTileAt(i);
            if (tile instanceof Property prop) {
                propertySnapshots.add(new PropertySnapshot(
                        prop.getId(),
                        prop.getIdOwner(),
                        prop.getBuiltHouses(),
                        prop.getPurchasePrice(),
                        prop.getPropertyGroup().name()
                ));
            }
        }
        final Map<String, Integer> jailMap = matchController.getJailTurnCounter().entrySet().stream().collect(
                Collectors.toMap(e -> e.getKey().getName(), Map.Entry::getValue));
        return new MatchSnapshot(
                playersSnapshots,
                propertySnapshots,
                matchController.getCurrentPlayerIndex(),
                matchController.getConsecutiveDoubles(),
                !matchController.canCurrentPlayerRoll(),
                jailMap
        );
    }

    /**
     * Loads a match from a saved snapshot file.
     *
     * @param file the JSON file containing the snapshot.
     * @return a restored {@link MatchControllerImpl}.
     * @throws IOException if file cannot be read.
     */
    public static MatchController loadMatch(final File file) throws IOException {
        ValidationUtils.requireNonNull(file, "File cannot be null");
        final ObjectMapper mapper = JsonUtils.getInstance().mapper();
        mapper.enable(DeserializationFeature.UNWRAP_ROOT_VALUE);
        final MatchSnapshot snapshot;
        try {
            snapshot = mapper.readValue(file, MatchSnapshot.class);
        } catch (final IOException e) {
            throw new IOException("Failed to deserialize match snapshot from " + file, e);
        }

        final List<Player> players = snapshot.getPlayers().stream()
                .map(data -> {
                    final Player player = new PlayerImpl(
                            data.getName(),
                            data.getBalance(),
                            data.getTokenType()
                    );
                    player.setPosition(data.getPosition());
                    player.setState(PlayerStateFactory.createFromClassName(data.getPlayerStateName(),
                            data.getPlayerInJail()));
                    return player;
                })
                .toList();
        final BoardImpl board = BoardLoader.loadBoardFromJson(PATH_BOARD_JSON);
        final Map<String, Property> properties = BoardLoader.loadPropertiesFromJson(PATH_BOARD_JSON);
        final MatchController controller = new MatchControllerImpl(players, board, properties);
        final int boardSize = board.size();
        for (int i = 0; i < boardSize; i++) {
            final Tile tile = board.getTileAt(i);
            if (tile instanceof Property prop) {
                final PropertySnapshot ps = snapshot.getProperties().stream()
                        .filter(p -> p.getId().equals(prop.getId()))
                        .findFirst()
                        .orElse(null);
                if (ps != null && ps.getOwnerId() != null && !ps.getId().isEmpty()) {
                    if (ps.getOwnerId() != null && !ps.getOwnerId().isEmpty()) {
                        final Player owner = players.stream()
                                .filter(p -> p.getName().equals(ps.getOwnerId()))
                                .findFirst()
                                .orElse(null);
                        if (owner != null) {
                            prop.assignOwner(owner.getName());
                            for (int h = 0; h < ps.getHouses(); h++) {
                                try {
                                    prop.buildHouse(owner.getName());
                                } catch (final IllegalStateException e) {
                                    throw new IllegalStateException("Error: " + e.getMessage());
                                }
                            }
                        }
                    }
                }
            }
        }
        controller.setCurrentPlayerIndex(snapshot.getCurrentPlayerIdx());
        controller.setConsecutiveDoubles(snapshot.getConsecutiveDoubles());
        controller.setHasRolled(snapshot.hasRolled());
        controller.restoreJailTurnCounter(snapshot.getJailTurnCounter(), players);
        return controller;
    }
}
