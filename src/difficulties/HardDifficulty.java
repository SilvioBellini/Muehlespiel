package difficulties;

import boardLogic.*;
import players.*;

import java.util.List;

/**
 * Represents a strategy for the hard difficulty in the game.
 * This class extends the DifficultyStrategy to implement complex move calculations
 * and removal strategies for players.
 */
public class HardDifficulty extends DifficultyStrategy {

    @Override
    public Node[] calculateMove(ImaginativePlayer player, ImaginativePlayer opponent) {
        List<Node[]> playerMoves = player.getAllPossibleMoves();
        List<Node[]> opponentMoves = opponent.getAllPossibleMoves();

        return tryMoves(
                entry("useFiggerMill", () -> useFiggerMill(playerMoves, player)),
                entry("findMill", () -> findMill(playerMoves, player)),
                entry("blockMill", () -> blockMill(playerMoves, opponentMoves, opponent)),
                entry("findZwickMill", () -> findZwickMill(playerMoves, player)),
                entry("blockZwickmill", () -> blockZwickmill(playerMoves, opponentMoves, opponent)),
                entry("blockBeginnerTrap", () -> blockBeginnerTrap(playerMoves, player)),
                entry("clusterMove", () -> clusterMove(playerMoves, player)),
                entry("openMill", () -> openMill(playerMoves, player, opponent)),
                entry("randomMove", () -> randomMove(playerMoves)));
    }

    @Override
    public Node calculateRemoval(ImaginativePlayer player, ImaginativePlayer opponent) {
        List<Node> removals = player.getAllPossibleRemovals(opponent);
        List<Node[]> opponentMoves = opponent.getAllPossibleMoves();

        Node removal = removeMillThreat(removals, opponentMoves, opponent);
        return (removal != null) ? removal : randomRemoval(removals);
    }
}
