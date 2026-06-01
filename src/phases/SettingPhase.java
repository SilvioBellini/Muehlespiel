package phases;

import boardLogic.BoardManager;
import boardLogic.Node;
import boardLogic.Stone;
import players.PlayerEntity;


/**
 * This class represents the setting phase of the game where players can place stones to unoccupied nodes.
 * It extends the GamePhase class and implements the handleMove method to allow placing stones.
 */
public class SettingPhase extends GamePhase {
    /**
     * Sets the context.
     *
     * @param playerEntity Context that uses the phase.
     */
    public SettingPhase(PlayerEntity playerEntity) {
        super(playerEntity);
    }

    /**
     * In this state the player can place the stones everywhere, if the target Node
     * is not already occupied.
     * Adds a stone and its owner to the players stone list.
     *
     * @param fromNode The origin node (not used here).
     * @param toNode   The target node.
     * @return {@code true} if the move was successful, {@code false} otherwise.
     */
    @Override
    public boolean handleMove(Node fromNode, Node toNode) {
        Stone stone = new Stone(playerEntity);
        playerEntity.addStone(stone);

        if (BoardManager.getInstance().placeStone(playerEntity, stone, toNode)) {
            playerEntity.stonePlaced();

            nextPhase();
            return true;
        }

        return false;
    }

    /**
     * Checks if the condition to change the phase is met (all stones are placed).
     * Changes to the moving phase if so.
     */
    @Override
    public void nextPhase() {
        if (playerEntity.allStonesPlaced()) {
            playerEntity.setPhase(new MovingPhase(playerEntity));

            System.out.println("[" + (playerEntity.getName().equalsIgnoreCase("white")
                    ? "WHITE" : "BLACK") + "] - Changed to MOVINGPHASE");
        }
    }
}
