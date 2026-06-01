package phases;

import boardLogic.BoardManager;
import boardLogic.Node;
import players.PlayerEntity;

/**
 * This class represents the moving phase of the game, where players can move their stones
 * to neighbours nodes on the board.
 */
public class MovingPhase extends GamePhase {
    /**
     * Sets the context.
     *
     * @param playerEntity Context that uses the phase.
     */
    public MovingPhase(PlayerEntity playerEntity) {
        super(playerEntity);
    }

    /**
     * In this state the player can move the stones to neighbours nodes, if the target Node
     * is not already occupied.
     *
     * @param fromNode The origin node (not used here).
     * @param toNode   The target node.
     * @return {@code true} if the move was successful, {@code false} otherwise.
     */
    @Override
    public boolean handleMove(Node fromNode, Node toNode) {
        return BoardManager.getInstance().moveStone(playerEntity, fromNode, toNode);
    }

    /**
     * Checks if the condition to change the phase is met (3 stones remaining).
     * Changes to the {@link JumpingPhase} if so.
     */
    @Override
    public void nextPhase() {
        if (playerEntity.getRemainingStones().size() == 3) {
            playerEntity.setPhase(new JumpingPhase(playerEntity));

            System.out.println("[" + (playerEntity.getName().equalsIgnoreCase("white")
                    ? "WHITE" : "BLACK") + "] - Changed to JUMPINGPHASE");
        }
    }
}
