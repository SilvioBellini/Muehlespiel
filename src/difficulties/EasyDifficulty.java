package difficulties;

import boardLogic.*;
import players.*;

import java.util.List;

/**
 * Represents a strategy for the easy difficulty in the game.
 * This class extends the DifficultyStrategy to implement complex move calculations
 * and removal strategies for players.
 */
public class EasyDifficulty extends DifficultyStrategy {

    @Override
    public Node[] calculateMove(ImaginativePlayer player, ImaginativePlayer opponent) {
        List<Node[]> moves = player.getAllPossibleMoves();
        return randomMove(moves);
    }

    @Override
    public Node calculateRemoval(ImaginativePlayer player, ImaginativePlayer opponent) {
        List<Node> removals = player.getAllPossibleRemovals(opponent);
        return randomRemoval(removals);
    }
}