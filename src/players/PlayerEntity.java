package players;

import boardLogic.Stone;
import phases.GamePhase;
import phases.SettingPhase;

import java.util.ArrayList;
import java.util.List;


/**
 * This class represents the abstract base class for different players.
 * It provides a common structure for all players in the game.
 */
public abstract class PlayerEntity {
    protected String name;
    protected List<Stone> stones;
    protected GamePhase currentPhase;
    protected int placedStones = 0;

    /**
     * Constructs a player with the given name.
     * Initializes an empty list for its stones.
     * Sets the phase, always starting with the setting phase.
     *
     * @param name The name of the player.
     */
    protected PlayerEntity(String name) {
        this.name = name;
        this.stones = new ArrayList<>();
        this.currentPhase = new SettingPhase(this);
    }

    /**
     * Adds a stone to the players stone list.
     *
     * @param stone The stone that gets added to the list.
     */
    public void addStone(Stone stone) {
        stones.add(stone);
    }

    /**
     * Removes a stone from the player.
     *
     * @param stone The stone that should get removed.
     */
    public void removeStone(Stone stone) {
        stones.remove(stone);
    }

    /**
     * Counts how many stones of the player have already been placed.
     */
    public void stonePlaced() {
        placedStones++;
    }

    /**
     * Checks if all the stones of the player have been placed already.
     *
     * @return {@code true} if all stones have been placed, {@code false} otherwise.
     */
    public boolean allStonesPlaced() {
        return placedStones == 9;
    }

    /**
     * Getter for the list containing all the players remaining stones.
     *
     * @return List containing all remaining stones.
     */
    public List<Stone> getRemainingStones() {
        return stones;
    }

    /**
     * Getter for the players name.
     *
     * @return name of the player.
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for the current phase the player is in.
     *
     * @return Current phase the player is in.
     */
    public GamePhase getPhase() {
        return currentPhase;
    }

    /**
     * Getter for the number of stones that have been placed by the player.
     *
     * @return The number of placed stones.
     */
    public int getPlacedStones() {
        return placedStones;
    }

    /**
     * Changes the current phase to a new one.
     *
     * @param phase The new phase that should be changed to.
     */
    public void setPhase(GamePhase phase) {
        currentPhase = phase;
    }

    /**
     * Sets the number of placed stones for the player.
     *
     * @param placedStones The number of stones that have been placed by the player.
     */
    public void setPlacedStones(int placedStones) {
        this.placedStones = placedStones;
    }
}
