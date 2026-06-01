package players;

import boardLogic.BoardManager;
import boardLogic.Node;
import boardLogic.RuleManager;
import boardLogic.Stone;
import phases.JumpingPhase;
import phases.MovingPhase;
import phases.SettingPhase;

import java.util.ArrayList;
import java.util.List;

/**
 * ImaginativePlayer is a subclass of PlayerEntity that represents a player
 * capable of planning moves based on the rules of the game without performing them.
 * It can generate all possible moves and removals for itself and its opponent.
 */
public class ImaginativePlayer extends PlayerEntity {
    private final RuleManager ruleManager;
    private final PlayerEntity player;

    /**
     * Constructs an ImaginativePlayer with the given PlayerEntity.
     * This constructor is used to create a player that can simulate moves
     * without actually executing them in the game.
     *
     * @param playerSelf The PlayerEntity to base this ImaginativePlayer on.
     */
    public ImaginativePlayer(PlayerEntity playerSelf) {
        super(playerSelf.name);
        ruleManager = RuleManager.getInstance();
        this.player = playerSelf;
        this.currentPhase = player.currentPhase;
        this.placedStones = player.placedStones;
        for (Stone stone : player.stones) {
            Stone stoneCopy = new Stone(this);
            stoneCopy.setNode(stone.getNode());
            this.addStone(stoneCopy);
        }
    }

    /**
     * Returns a list of all possible moves for this player.
     * This method generates moves based on the current phase of the game.
     *
     * @return A list of Node arrays representing all possible moves.
     * @see #settingPhaseMoves()
     * @see #movingPhaseMoves()
     * @see #jumpingPhaseMoves()
     */
    public List<Node[]> getAllPossibleMoves() {
        List<Node[]> possibleMoves = new ArrayList<>();

        if (this.getPhase() instanceof SettingPhase) {
            possibleMoves = settingPhaseMoves();
        } else if (this.getPhase() instanceof MovingPhase) {
            possibleMoves = movingPhaseMoves();
        } else if (this.getPhase() instanceof JumpingPhase) {
            possibleMoves = jumpingPhaseMoves();
        }

        return possibleMoves;
    }

    /**
     * Helper method for the {@link #getAllPossibleMoves()} method.
     * <p>
     * Returns a list of all possible moves for the setting phase.
     *
     * @return A list of Node arrays representing all possible moves in the setting phase.
     */
    private List<Node[]> settingPhaseMoves() {
        List<Node[]> possibleMoves = new ArrayList<>();
        for (int i = 0; i <= 23; i++) {
            Node node = BoardManager.getInstance().getNode(i);
            if (ruleManager.isMoveValid(this, null, node)) {
                possibleMoves.add(new Node[]{null, node});
            }
        }

        return possibleMoves;
    }

    /**
     * Helper method for the {@link #getAllPossibleMoves()} method.
     * <p>
     * Returns a list of all possible moves for the moving phase.
     *
     * @return A list of Node arrays representing all possible moves in the moving phase.
     */
    private List<Node[]> movingPhaseMoves() {
        List<Node[]> possibleMoves = new ArrayList<>();
        for (Stone stone : this.getRemainingStones()) {
            for (Node neighbor : stone.getNode().getNeighbors()) {
                if (ruleManager.isMoveValid(this, stone.getNode(), neighbor)) {
                    possibleMoves.add(new Node[]{stone.getNode(), neighbor});
                }
            }
        }

        return possibleMoves;
    }

    /**
     * Helper method for the {@link #getAllPossibleMoves()} method.
     * <p>
     * Returns a list of all possible moves for the jumping phase.
     *
     * @return A list of Node arrays representing all possible moves in the jumping phase.
     */
    private List<Node[]> jumpingPhaseMoves() {
        List<Node[]> possibleMoves = new ArrayList<>();
        for (Stone stone : this.getRemainingStones()) {
            for (int i = 0; i <= 23; i++) {
                Node node = BoardManager.getInstance().getNode(i);
                if (ruleManager.isMoveValid(this, stone.getNode(), node)) {
                    possibleMoves.add(new Node[]{stone.getNode(), node});
                }
            }
        }

        return possibleMoves;
    }

    /**
     * Returns a list of all possible removals for the opponent.
     * This method checks which stones can be removed based on the current game state.
     *
     * @param opponent The PlayerEntity representing the opponent.
     * @return A list of Node objects representing all possible removals.
     */
    public List<Node> getAllPossibleRemovals(PlayerEntity opponent) {
        List<Node> possibleRemovals = new ArrayList<>();
        for (Stone stone : opponent.getRemainingStones()) {
            if (ruleManager.isRemovalValid(this, stone.getNode())) {
                possibleRemovals.add(stone.getNode());
            }
        }
        return possibleRemovals;
    }
}
