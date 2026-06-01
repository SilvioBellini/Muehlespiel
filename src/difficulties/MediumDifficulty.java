package difficulties;

import boardLogic.*;
import players.*;

import java.util.List;

/**
 * Represents a strategy for the medium difficulty in the game.
 * This class extends the DifficultyStrategy to implement complex move calculations
 * and removal strategies for players.
 */
public class MediumDifficulty extends DifficultyStrategy {

    @Override
    public Node[] calculateMove(ImaginativePlayer player, ImaginativePlayer opponent) {
        List<Node[]> playerMoves = player.getAllPossibleMoves();
        List<Node[]> opponentMoves = opponent.getAllPossibleMoves();

        return tryMoves(
                entry("findMill", () -> findMill(playerMoves, player)),
                entry("blockMill", () -> blockMill(playerMoves, opponentMoves, opponent)),
                entry("clusterMove", () -> clusterMove(playerMoves, player)),
                entry("randomMove", () -> randomMove(playerMoves)));
    }

    @Override
    public Node calculateRemoval(ImaginativePlayer player, ImaginativePlayer opponent) {
        List<Node> removals = player.getAllPossibleRemovals(opponent);
        List<Node[]> opponentMoves = opponent.getAllPossibleMoves();

        Node removalNode = removeMillThreat(removals, opponentMoves, opponent);
        if (removalNode != null) {
            return removalNode;
        }

        return randomRemoval(removals);
    }
}
