package persistance;

import boardLogic.BoardManager;
import boardLogic.Node;
import boardLogic.Stone;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import difficulties.*;
import phases.*;
import players.CPUPlayer;
import players.Player;
import players.PlayerEntity;
import view.GUI;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manages the persistence of game states by saving and loading
 * game data to and from JSON files.
 * <p>
 * This class provides methods to create a serializable representation of
 * the current game state, save it to a file, and restore it again.
 */
public class GamePersistenceManager {
    //region Save Game

    /**
     * Creates a GameStateData object containing all necessary information
     * to restore the current game state. This includes player data, move history,
     * and game settings.
     * <p>
     * The GameStateData can be serialized into JSON and saved to a file.
     * <p>
     * This method is used when saving the current game state, e.g. before exiting
     * the application or when the user chooses to save the game.
     *
     * @param white             The white player.
     * @param black             The black player
     * @param current           The current player.
     * @param history           The history containing the undo- and redo-stacks.
     * @param noMillTurnCounter The amount of turns no mills where formed.
     * @param timePerTurn       The time each player has to perform a move.
     * @param timeLeft          The time the current player has left to perform a move.
     * @param mode              The current mode defining if there is a CPU or not.
     * @return The game state containing the current game.
     * @see #toPlayerData(PlayerEntity)
     * @see #toMoveData(Move)
     */
    public GameStateData createGameState(PlayerEntity white, PlayerEntity black, PlayerEntity current,
                                         History history, int noMillTurnCounter, int timePerTurn,
                                         int timeLeft, String mode) {
        PlayerData whiteData = toPlayerData(white);
        PlayerData blackData = toPlayerData(black);
        boolean currentIsWhite = "white".equalsIgnoreCase(current.getName());
        List<MoveData> undo = history.getUndoMoves().stream().map(this::toMoveData).collect(Collectors.toList());
        List<MoveData> redo = history.getRedoMoves().stream().map(this::toMoveData).collect(Collectors.toList());

        return new GameStateData(whiteData, blackData, currentIsWhite, undo,
                redo, noMillTurnCounter, timePerTurn, timeLeft, mode);
    }

    /**
     * Saves the current game state to a JSON-File.
     * <p>
     * This method serializes the GameStateData created by {@link #createGameState(PlayerEntity, PlayerEntity, PlayerEntity, History, int, int, int, String)}
     * and writes it to the specified target file.
     * <p>
     * If the file already exists, it will be overwritten.
     *
     * @param gameState  The GameStateData containing the current game state.
     * @param targetFile The target file to which the game state should be saved.
     * @throws JsonIOException if the data could not be written into a JSON-File.
     */
    public void saveGameState(GameStateData gameState, File targetFile) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(targetFile)) {
            gson.toJson(gameState, writer);
        } catch (IOException e) {
            throw new JsonIOException("Writing data to JSON failed!");
        }
    }

    /**
     * Helpermethod for {@link #createGameState(PlayerEntity, PlayerEntity, PlayerEntity, History, int, int, int, String)}.
     * <p>
     * Converts a PlayerEntity to its corresponding serializable PlayerData.
     *
     * @param player The player entity to convert.
     * @return A PlayerData object containing serializable fields.
     */
    private PlayerData toPlayerData(PlayerEntity player) {
        List<Integer> nodeIds = player.getRemainingStones().stream().map(stone -> stone.getNode().getID())
                .collect(Collectors.toList());

        DifficultyLevel difficulty = DifficultyLevel.NONE;
        if (player instanceof CPUPlayer cpuPlayer) {
            difficulty = DifficultyLevel.fromStrategyClass(cpuPlayer.getDifficulty().getClass());
        }

        return new PlayerData(player.getName(), player.getPhase().getClass().getSimpleName(), player.getPlacedStones(),
                nodeIds, player.getClass().getSimpleName(), difficulty);
    }

    /**
     * Helpermethod for {@link #createGameState(PlayerEntity, PlayerEntity, PlayerEntity, History, int, int, int, String)}.
     * <p>
     * Converts a Move to its corresponding serializable MoveData.
     *
     * @param move The move to convert.
     * @return A MoveData object containing serializable fields.
     */
    private MoveData toMoveData(Move move) {
        Map<Integer, Integer> board = createBoardSnapshot(move.getBoardSnapshot());

        return new MoveData(move.getPlacedStonesWhite(), move.getPlacedStonesBlack(), board, move.getCurrentIsWhite(),
                move.getPhaseWhite().getClass().getSimpleName(), move.getPhaseBlack().getClass().getSimpleName(), move.getNoMillTurnCounter(), move.getTimeLeft());
    }

    /**
     * Helpermethod for {@link #toMoveData(Move)}.
     * <p>
     * Creates a simplified board snapshot from a full board state.
     * <ul>
     * <li>0 = empty</li>
     * <li>1 = white</li>
     * <li>2 = black</li>
     * </ul>
     *
     * @param board The full board state as a map of nodes.
     * @return A simplified map of node IDs to owner codes.
     */
    private Map<Integer, Integer> createBoardSnapshot(Map<Integer, Node> board) {
        Map<Integer, Integer> snapshot = new HashMap<>();

        for (Map.Entry<Integer, Node> entry : board.entrySet()) {
            Node node = entry.getValue();
            int ownerId = 0;

            if (node.isOccupied()) {
                String ownerName = node.getStone().getOwner().getName().toLowerCase();
                ownerId = switch (ownerName) {
                    case "white" -> 1;
                    case "black" -> 2;
                    default -> 0;
                };
            }
            snapshot.put(entry.getKey(), ownerId);
        }
        return snapshot;
    }
    //endregion

    // region Load Game

    /**
     * Loads a saved game state from a JSON-File and restores it.
     *
     * @param file The JSON file containing the saved game state.
     * @return The GameStateData containing the saved game state.
     * @throws JsonIOException if the data could not be read from a JSON-File.
     */
    public GameStateData restoreGameState(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // Read data from File
            Gson gson = new Gson();
            return gson.fromJson(reader, GameStateData.class);
        } catch (IOException e) {
            throw new JsonIOException("Reading data from JSON failed");
        }
    }

    /**
     * Reconstructs a PlayerEntity from its saved PlayerData.
     * <p>
     * This method is used when loading a game state to restore the players
     * with their names, phases, and stones.
     *
     * @param data          The PlayerData containing saved state.
     * @param opponentIfCPU The opponent of the {@code CPUPlayer}. Can be null for
     *                      {@code Player}.
     * @return A PlayerEntity with restored name, phase, and stones.
     */
    public PlayerEntity fromPlayerData(PlayerData data, PlayerEntity opponentIfCPU, boolean placeStones) {
        PlayerEntity player;
        BoardManager boardManager = BoardManager.getInstance();

        if ("Player".equalsIgnoreCase(data.type)) {
            player = new Player(data.name);
        } else {
            player = new CPUPlayer(data.name, opponentIfCPU);
            DifficultyStrategy loadedDifficulty = switch (data.difficulty) {
                case DifficultyLevel.EASY -> new EasyDifficulty();
                case DifficultyLevel.MEDIUM -> new MediumDifficulty();
                case DifficultyLevel.HARD -> new HardDifficulty();
                default -> throw new IllegalStateException("Invalid difficulty: " + data.difficulty);
            };
            ((CPUPlayer) player).setDifficulty(loadedDifficulty);
            GUI.difficulty = loadedDifficulty;
        }

        player.setPlacedStones(data.placedStones);

        // Reconstruct phase
        switch (data.phase) {
            case "SettingPhase" -> player.setPhase(new SettingPhase(player));
            case "MovingPhase" -> player.setPhase(new MovingPhase(player));
            case "JumpingPhase" -> player.setPhase(new JumpingPhase(player));
            case "GameOverPhase" -> player.setPhase(new GameOverPhase(player));
            default -> throw new IllegalStateException("Unknown Phase: " + data.phase);
        }

        // Assign stones
        if (placeStones) {
            for (Integer nodeId : data.stonePositions) {
                Stone stone = new Stone(player);
                player.addStone(stone);
                boardManager.placeStone(player, stone, boardManager.getNode(nodeId));
            }
        }

        return player;
    }

    /**
     * Reconstructs a {@link Move} from its saved {@link MoveData}.
     * <p>
     * This method is used when loading a game state to restore the move history
     * with the correct board state and placed stones.
     *
     * @param data        The MoveData containing saved move state.
     * @param loadedWhite The PlayerEntity representing the white player.
     * @param loadedBlack The PlayerEntity representing the black player.
     * @return A Move object with restored board state and placed stones.
     */
    public Move fromMoveData(MoveData data, PlayerEntity loadedWhite, PlayerEntity loadedBlack) {
        Map<Integer, Node> board = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : data.boardSnapshot.entrySet()) {
            int id = entry.getKey();
            int ownerCode = entry.getValue();
            Node node = new Node(id);

            if (ownerCode == 1 || ownerCode == 2) {
                PlayerEntity owner = (ownerCode == 1) ? loadedWhite : loadedBlack;
                Stone stone = new Stone(owner);
                node.placeStone(stone);
                owner.addStone(stone);
            }

            board.put(id, node);
        }

        GamePhase loadedPhaseWhite = fromPhaseData(data.phaseWhite, loadedWhite);
        GamePhase loadedPhaseBlack = fromPhaseData(data.phaseBlack, loadedBlack);

        return new Move(data.placedStonesWhite, data.placedStonesBlack, board, data.currentIsWhite, loadedPhaseWhite, loadedPhaseBlack,
                data.noMillTurnCounter, data.timeLeft);
    }

    private GamePhase fromPhaseData(String phase, PlayerEntity player) {
        return switch (phase) {
            case "SettingPhase" -> new SettingPhase(player);
            case "MovingPhase" -> new MovingPhase(player);
            case "JumpingPhase" -> new JumpingPhase(player);
            case "GameOverPhase" -> new GameOverPhase(player);
            default -> throw new IllegalStateException("Unknown Phase: " + phase);
        };
    }

    // endregion
}