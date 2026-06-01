package phases;

import boardLogic.BoardManager;
import boardLogic.Node;
import players.PlayerEntity;

/**
 * This class represents the jumping phase of the game where players can jump to unoccupied nodes.
 * It extends the GamePhase class and implements the handleMove method to allow jumping moves.
 */
public class JumpingPhase extends GamePhase {
    /**
     * Sets the context.
     *
     * @param playerEntity Context that uses the phase.
     */
    public JumpingPhase(PlayerEntity playerEntity) {
        super(playerEntity);
    }

    /**
     * In this state the player can jump to unoccupied nodes, if the target Node
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
     * Checks if the condition to change the phase is met (2 stones remaining).
     * Changes to the {@link GameOverPhase} if so.
     */
    @Override
    public void nextPhase() {
        if (playerEntity.getRemainingStones().size() <= 2) {
            playerEntity.setPhase(new GameOverPhase(playerEntity));

            System.out.println("[" + (playerEntity.getName().equalsIgnoreCase("white")
                    ? "WHITE" : "BLACK") + "] - Changed to GAMEOVERPHASE");
        }
    }
}
