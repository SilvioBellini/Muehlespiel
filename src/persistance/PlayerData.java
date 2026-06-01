package persistance;

import difficulties.DifficultyLevel;

import java.util.List;

/**
 * Represents a serializable snapshot of player data in the game.
 */
public class PlayerData {
    public String name;
    public String phase;
    public int placedStones;
    public List<Integer> stonePositions;
    public String type;
    public DifficultyLevel difficulty;

    /**
     * Creates a new PlayerData instance capturing the state of a player at a specific moment.
     *
     * @param name           The name of the player.
     * @param phase          The current game phase for the player.
     * @param placedStones   The number of stones placed by the player.
     * @param stonePositions The positions of the stones placed by the player.
     * @param type           The type of player (e.g. Player, CPUPlayer).
     * @param difficulty     The difficulty for CPU players.
     */
    public PlayerData(String name, String phase, int placedStones, List<Integer> stonePositions, String type, DifficultyLevel difficulty) {
        this.name = name;
        this.phase = phase;
        this.placedStones = placedStones;
        this.stonePositions = stonePositions;
        this.type = type;
        this.difficulty = difficulty;
    }
}
