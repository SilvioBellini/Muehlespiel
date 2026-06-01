package phases;

import boardLogic.Node;
import players.PlayerEntity;

/**
 * This class represents the abstract base class for different gamePhases.
 * It provides a common structure for all game phases in the game.
 */
public abstract class GamePhase {
    protected PlayerEntity playerEntity;

    /**
     * Constructor for GamePhase.
     * Initializes the game phase with the specified player entity.
     *
     * @param playerEntity The player entity associated with this game phase.
     */
    protected GamePhase(PlayerEntity playerEntity) {
        this.playerEntity = playerEntity;
    }

    /**
     * Handles the move action for the game phase.
     * This method should be overridden by subclasses to implement specific move logic.
     *
     * @param fromNode The origin node from which the stone is moved.
     * @param toNode   The target node to which the stone is moved.
     * @return true if the move was successful, false otherwise.
     */
    public boolean handleMove(Node fromNode, Node toNode) {
        return false;
    }

    /**
     * Advances to the next game phase if the conditions are met.
     * This method should be overridden by subclasses to implement specific phase transition logic.
     */
    public void nextPhase() {
    }
}
