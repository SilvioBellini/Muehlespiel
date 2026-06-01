package phases;

import players.PlayerEntity;

/**
 * Represents the game over phase in the game.
 * This phase is active when the game has ended.
 */
public class GameOverPhase extends GamePhase {
    /**
     * Constructor for GameOverPhase.
     * Initializes the game over phase with the specified player entity.
     *
     * @param playerEntity The player entity associated with this game phase.
     */
    public GameOverPhase(PlayerEntity playerEntity) {
        super(playerEntity);
    }
}
