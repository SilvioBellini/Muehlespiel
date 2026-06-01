package difficulties;

import boardLogic.*;
import phases.*;
import players.*;

import java.util.*;
import java.util.function.Supplier;

/**
 * The DifficultyStrategy class serves as an abstract representation of the
 * decision-making logic for determining moves and removals in a game. It provides
 * methods and utility functionality to evaluate and execute strategies based on
 * the current game state and opponent behavior. Subclasses implement specific
 * difficulty levels by overriding the abstract methods for move calculation and
 * piece removal.
 */
public abstract class DifficultyStrategy {
    private final Random random = new Random();

    /**
     * Calculates the optimal move for a player based on a specific difficulty strategy.
     * This method generates a set of possible moves
     *
     * @see EasyDifficulty
     * @see MediumDifficulty
     * @see HardDifficulty
     */
    public abstract Node[] calculateMove(ImaginativePlayer player, ImaginativePlayer opponent);

    /**
     * Determines the node from which a stone should be removed as part of the strategy.
     * This method is intended to analyze the game state and decide on the optimal removal
     * of an opponent's stone, adhering to the rules and the strategy being implemented.
     *
     * @param player   The player executing the removal.
     * @param opponent The opponent whose stone might be removed.
     * @return The node representing the stone to be removed, or null if no valid removal is found.
     * @see EasyDifficulty
     * @see MediumDifficulty
     * @see HardDifficulty
     */
    public abstract Node calculateRemoval(ImaginativePlayer player, ImaginativePlayer opponent);

    /**
     * Iterates through a list of move-generating tactics and returns the first non-null move set produced
     * by any of the tactics. If a tactic is successful, the corresponding tactics name is logged.
     *
     * @param tactics An array of map entries, where each entry consists of a string representing
     *                the strategy's name and a supplier that generates a potential move set.
     * @return An array of {@link Node} representing the first valid move set found, or null if no
     * strategy produces a valid move.
     */
    @SafeVarargs
    protected final Node[] tryMoves(Map.Entry<String, Supplier<Node[]>>... tactics) {
        for (Map.Entry<String, Supplier<Node[]>> tactic : tactics) {
            Node[] move = tactic.getValue().get();
            if (move != null) {
                System.out.println("[CPU] - Strategy used: " + tactic.getKey());
                return move;
            }
        }
        return null;
    }

    /**
     * Creates a map entry containing a tactic name and its corresponding move generation function.
     *
     * @param name   The name of the tactic.
     * @param tactic A supplier that provides an array of {@link Node} representing possible moves for the tactic.
     * @return A map entry where the key is the tactic name and the value is the supplier function for move generation.
     */
    protected Map.Entry<String, Supplier<Node[]>> entry(String name, Supplier<Node[]> tactic) {
        return new AbstractMap.SimpleEntry<>(name, tactic);
    }

    /**
     * Checks if the given id is present in the specified mill array.
     *
     * @param id   The id to check for in the mill array.
     * @param mill An array of integers representing the mill.
     * @return {@code true} if the id is found in the mill array, {@code false} otherwise.
     */
    private boolean isInMill(int id, int[] mill) {
        for (int m : mill) {
            if (m == id)
                return true;
        }
        return false;
    }

    /**
     * Selects a random move from the given list of moves. The method retrieves one
     * move array by randomly choosing an index from the provided list of possible moves.
     *
     * @param moves A list of arrays of {@link Node}, where each array represents a
     *              potential move set.
     * @return An array of {@link Node} representing a randomly chosen move from the list.
     */
    protected Node[] randomMove(List<Node[]> moves) {
        return moves.get(random.nextInt(moves.size()));
    }

    /**
     * Searches through a list of player moves to identify a mill formation for the specified player.
     * A mill consists of a set of nodes where the player has successfully aligned their stones
     * according to the game's rules, as determined by the {@link RuleManager}.
     *
     * @param playerMoves A list of arrays of {@link Node}, each representing a potential move set for the player.
     * @param player      The player whose moves are being analyzed for mill formation.
     * @return An array of {@link Node} representing the first found mill for the player, or null if no mill is found.
     */
    protected Node[] findMill(List<Node[]> playerMoves, ImaginativePlayer player) {
        for (Node[] playerMove : playerMoves) {
            if (RuleManager.getInstance().countMillsFormed(playerMove, player) >= 1) {
                return playerMove;
            }
        }
        return null;

    }

    /**
     * Identifies a potential move to block the opponent from forming a mill.
     * The method evaluates the player's possible moves and compares them against the opponent's
     * moves and their almost mill nodes to determine an optimal blocking strategy.
     *
     * @param playerMoves   A list of arrays of {@link Node}, representing the possible moves
     *                      for the current player.
     * @param opponentMoves A list of arrays of {@link Node}, representing the possible moves
     *                      for the opponent player.
     * @param opponent      The opponent player whose strategy is being anticipated.
     * @return An array of {@link Node} representing the blocking move, or null if no such move
     * exists.
     */
    protected Node[] blockMill(List<Node[]> playerMoves, List<Node[]> opponentMoves, ImaginativePlayer opponent) {
        List<Integer> almostMills = findAlmostMills(opponent);
        for (Node[] move : playerMoves) {
            if (almostMills.contains(move[1].getID())) {
                if (opponent.getPhase() instanceof JumpingPhase) {
                    return move;
                }
                for (Node[] opponentMove : opponentMoves) {
                    if (opponentMove[1].getID() == move[1].getID()) {
                        return move;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Identifies all "almost mill" positions for the given player.
     * An "almost mill" is defined as a configuration where a player has two stones in a potential mill,
     * and one unoccupied position could complete the mill.
     *
     * @param player The player for whom the almost mill positions are being determined.
     * @return A list of integers representing the IDs of the unoccupied positions that could
     * complete a mill for the player.
     */
    private List<Integer> findAlmostMills(ImaginativePlayer player) {
        List<Integer> playerStonesID = new ArrayList<>();
        List<Integer> playerAlmostMills = new ArrayList<>();

        // IDs aller Steine des Spielers sammeln
        for (Stone stone : player.getRemainingStones()) {
            if (stone.getNode() != null) {
                playerStonesID.add(stone.getNode().getID());
            }
        }

        // Finde alle Almost-Mills (2 besetzt, 1 frei)
        for (int[] mill : BoardManager.getInstance().potentialMills) {
            int owned = 0;
            int freeCount = 0;
            int freePos = -1;

            for (int pos : mill) {
                if (playerStonesID.contains(pos)) {
                    owned++;
                } else {
                    Node node = BoardManager.getInstance().getNode(pos);
                    if (!node.isOccupied()) {
                        freeCount++;
                        freePos = pos;
                    }
                }
            }

            if (owned == 2 && freeCount == 1) {
                playerAlmostMills.add(freePos);
            }
        }

        return playerAlmostMills;
    }

    /**
     * Identifies a potential move for clustering stones based on the player's strategy.
     * The method evaluates the provided moves to locate a node associated with the player
     * that is near unoccupied nodes, prioritizing positions with neighboring stones that
     * belong to the player.
     *
     * @param playerMoves A list of arrays of {@link Node}, where each array represents
     *                    a potential move set for the player.
     * @param player      The player whose move strategy is being analyzed and optimized.
     * @return An array of {@link Node} representing the identified optimal move for clustering,
     * or null if no such move is found.
     */
    protected Node[] clusterMove(List<Node[]> playerMoves, ImaginativePlayer player) {
        // Find a Node in playerMoves which has a stone of the same player and another
        // unoccupied node as a neighbor.
        for (Node[] playerMove : playerMoves) {
            Node targetNode = playerMove[1];
            for (Node nodeNeighbor : targetNode.getNeighbors()) {
                if (!nodeNeighbor.isOccupied()) {
                    int unoccupiedNeighborCount = 0;
                    int stoneNeighbor = 0;
                    for (Node nodeNeighborNeighbor : nodeNeighbor.getNeighbors()) {
                        if (!nodeNeighborNeighbor.isOccupied()) {
                            unoccupiedNeighborCount++;
                        } else if (nodeNeighborNeighbor.isOccupied()) {
                            for (Stone stone : player.getRemainingStones()) {
                                if (stone.getNode().getID() == nodeNeighborNeighbor.getID())
                                    stoneNeighbor++;
                            }
                        }
                    }
                    if (stoneNeighbor >= 2 && unoccupiedNeighborCount >= 1) {
                        for (Node[] move : playerMoves) {
                            if (move[1].getID() == nodeNeighbor.getID())
                                return move;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Identifies a potential "Zwick Mill" move for the specified player. A Zwick Mill refers to a
     * situation where a player can form a mill by leveraging shared positions across multiple
     * potential mills containing a single player's stone and no opponent's stones. This method
     * evaluates the player's potential moves and determines an optimal move based on shared
     * positions in the player's almost mills.
     *
     * @param playerMoves A list of arrays of {@link Node}, where each array represents a potential
     *                    move set for the player.
     * @param player      The player whose moves and potential mill configurations are being analyzed.
     * @return An array of {@link Node} representing the identified "Zwick Mill" move, or null if no
     * such move is found.
     */
    protected Node[] findZwickMill(List<Node[]> playerMoves, ImaginativePlayer player) {
        if (!(player.getPhase() instanceof SettingPhase && player.getPlacedStones() <= 7
                && player.getPlacedStones() >= 2)) {
            return null;
        }

        List<Integer> placedStonesID = new ArrayList<>();
        for (Stone stone : player.getRemainingStones()) {
            placedStonesID.add(stone.getNode().getID());
        }

        // gather mills that only contains a single stone from the player, none from the
        // opponent
        List<int[]> singleStoneMills = new ArrayList<>();
        for (int[] mill : BoardManager.getInstance().potentialMills) {
            int stoneCounter = 0;
            boolean opponentInMill = false;
            for (int pos : mill) {
                if (placedStonesID.contains(pos)) {
                    stoneCounter++;
                } else { // if it does not contain but is occupied, it belongs to the opponent
                    if (BoardManager.getInstance().getNode(pos).isOccupied()) {
                        opponentInMill = true;
                    }
                }
            }
            if (stoneCounter == 1 && !opponentInMill) {
                singleStoneMills.add(mill);
            }
        }

        // extract positions which are shared in singleStoneMills
        Map<Integer, Integer> posCountMap = new HashMap<>();
        for (int[] mill : singleStoneMills) {
            for (int pos : mill) {
                posCountMap.put(pos, posCountMap.getOrDefault(pos, 0) + 1);
            }
        }
        List<Integer> sharedMillPos = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : posCountMap.entrySet()) {
            if (entry.getValue() >= 2) {
                sharedMillPos.add(entry.getKey());
            }
        }

        // find a player move which is in sharedMillPos
        for (Node[] move : playerMoves) {
            if (sharedMillPos.contains(move[1].getID())) {
                return move;
            }
        }

        return null;
    }

    /**
     * Identifies a potential move to block an opponent's "Zwick Mill" formation. A "Zwick Mill" refers
     * to a situation where the opponent can form a mill by leveraging shared positions across multiple
     * potential mills. This method evaluates the player's possible moves and compares them against the
     * opponent's moves and their "Zwick Mill" to determine an optimal blocking strategy.
     *
     * @param playerMoves   A list of arrays of {@link Node}, representing the possible moves for the current player.
     * @param opponentMoves A list of arrays of {@link Node}, representing the possible moves for the opponent player.
     * @param opponent      The opponent player whose strategy is being anticipated.
     * @return An array of {@link Node} representing the blocking move, or null if no such move exists.
     */
    protected Node[] blockZwickmill(List<Node[]> playerMoves, List<Node[]> opponentMoves,
                                    ImaginativePlayer opponent) {
        if (!(opponent.getPhase() instanceof SettingPhase && opponent.getPlacedStones() <= 7
                && opponent.getPlacedStones() >= 2)) {
            return null;
        }

        Node[] zwickMill = findZwickMill(opponentMoves, opponent);
        if (zwickMill == null) {
            return null;
        }

        for (Node[] move : playerMoves) {
            if (move[1].getID() == zwickMill[1].getID()) {
                return move;
            }
        }

        return null;
    }

    /**
     * Identifies and executes a move to block an opponent's potential trap during the setting phase of the game.
     * This method examines specific corner positions on the board and determines if the opponent is attempting
     * to control a critical area by placing stones. If a potential trap is detected, the method suggests a move
     * for the player to block the trap.
     *
     * @param playerMoves A list of possible moves the player can make. Each move is represented as an array of
     *                    {@code Node[]}, where the first node represents the origin and the second node represents
     *                    the destination of the move.
     * @param player      The player making the decision. This must be an instance of {@code ImaginativePlayer} who is
     *                    in the Phases.SettingPhase of the game and has placed stones fewer than or equal to 8.
     * @return An array of {@code Node[]} representing the suggested move to block the opponent's trap.
     * Returns {@code null} if no suitable blocking move is found or if the conditions for the
     * method execution are not met.
     */
    protected Node[] blockBeginnerTrap(List<Node[]> playerMoves, ImaginativePlayer player) {
        if (!(player.getPhase() instanceof SettingPhase && player.getPlacedStones() <= 8)) {
            return null;
        }

        BoardManager boardManager = BoardManager.getInstance();

        int[][] cornerPairs = {{0, 23}, {2, 21}, {3, 20}, {5, 18}, {6, 17}, {8, 15}};

        for (int[] pair : cornerPairs) {
            int corner0 = pair[0];
            int corner1 = pair[1];

            Node corner0Node = boardManager.getNode(corner0);
            Node corner1Node = boardManager.getNode(corner1);

            boolean corner0Opponent = false; // is coner0 occupied by opponent
            if (corner0Node.isOccupied() && !(corner0Node.getStone().getOwner().getName().equalsIgnoreCase(player.getName()))) {
                corner0Opponent = true;
            }
            boolean corner1Opponent = false; // is coner1 occupied by opponent
            if (corner1Node.isOccupied() && !(corner1Node.getStone().getOwner().getName().equalsIgnoreCase(player.getName()))) {
                corner1Opponent = true;
            }

            // get the corner occupied by the opponent, set playerCorner to the opposite
            // corner
            int opponentCorner = -1;
            int playerCorner = -1;
            if (corner0Opponent) {
                opponentCorner = corner0;
                playerCorner = corner1;
            }
            if (corner1Opponent) {
                opponentCorner = corner1;
                playerCorner = corner0;
            }

            if (corner0Opponent || corner1Opponent) {
                boolean ringEmpty = true;
                for (Node ringNode : boardManager.getRingNodes(corner0)) {
                    if (ringNode.getID() != opponentCorner && ringNode.isOccupied()) {
                        ringEmpty = false;
                    }
                }
                if (ringEmpty) {
                    for (Node[] move : playerMoves) {
                        if (move[1].getID() == playerCorner) {
                            return move;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Attempts to find a move for the given player that completes an almost-formed mill
     * by transferring a stone from another fully-formed mill. The method evaluates
     * current player moves, identifies almost-completed mills, and prioritizes moves
     * that can leverage stones already forming part of other mills.
     *
     * @param playerMoves a list of potential moves for the player, where each move is
     *                    represented as an array of two nodes (source and destination)
     * @param player      the imaginative player executing the move, including details
     *                    such as game phase and placed stones
     * @return an array of two nodes representing the move (source node and target node)
     * to complete an almost-formed mill, or null if no suitable move is found
     */
    protected Node[] useFiggerMill(List<Node[]> playerMoves, ImaginativePlayer player) {
        if (!(player.getPhase() instanceof MovingPhase)) {
            return null;
        }

        List<int[]> playerMills = new ArrayList<>();
        List<int[]> playerAlmostMills = new ArrayList<>();
        List<Integer> playerStonesID = new ArrayList<>();

        // Gather IDs from the nodes player placed stones on.
        for (Stone stone : player.getRemainingStones()) {
            if (stone.getNode() != null) {
                playerStonesID.add(stone.getNode().getID());
            }
        }

        // Search full and almost full mills
        for (int[] mill : BoardManager.getInstance().potentialMills) {
            int owned = 0;
            int free = -1;

            for (int pos : mill) {
                if (playerStonesID.contains(pos)) {
                    owned++;
                } else {
                    Node node = BoardManager.getInstance().getNode(pos);
                    if (!node.isOccupied()) {
                        free = pos;
                    }
                }
            }

            if (owned == 3) {
                playerMills.add(mill);
            } else if (owned == 2 && free != -1) {
                playerAlmostMills.add(mill);
            }
        }

        // Try to complete a full mill by moving a stone from an existing mill
        for (int[] mill : playerAlmostMills) {
            for (int pos : mill) {
                Node targetNode = BoardManager.getInstance().getNode(pos);
                if (targetNode.isOccupied()) {
                    continue;
                }

                for (Node[] move : playerMoves) {
                    Node fromNode = move[0];
                    Node toNode = move[1];

                    // Target node must fit in the almost mill
                    if (toNode.getID() != pos) {
                        continue;
                    }

                    int fromID = fromNode.getID();

                    // Check if the fromNode is part of another, already formed mill
                    boolean fromInAnotherMill = false;
                    for (int[] fullMill : playerMills) {
                        if (isInMill(fromID, fullMill) && !Arrays.equals(fullMill, mill)) {
                            fromInAnotherMill = true;
                            break;
                        }
                    }

                    if (fromInAnotherMill) {
                        return new Node[]{fromNode, toNode};
                    }
                }
            }
        }

        return null;
    }

    /**
     * Determines and returns a move that opens a mill by relocating a player's stone
     * while ensuring that the target node does not have an opponent's stone as a neighbor.
     * This method primarily operates outside of the "SettingPhase" of the game.
     *
     * @param playerMoves a list of moves, where each move is represented as an array of two nodes.
     *                    The first node is the current position, and the second node is the target position of the stone.
     * @param player      the imaginative player for whom the move is being determined.
     * @param opponent    the opposing imaginative player whose stones need to be avoided while opening a mill.
     * @return the first valid move from the player's moves that opens a mill without an opponent's neighboring stone,
     * or null if no such move is found.
     */
    protected Node[] openMill(List<Node[]> playerMoves, ImaginativePlayer player, ImaginativePlayer opponent) {
        if (player.getPhase() instanceof SettingPhase) {
            return null;
        }

        // Extract moves from player which move a stone out of a mill
        List<Node[]> fromMillMoves = new ArrayList<>();
        for (Node[] move : playerMoves) {
            for (int[] mill : BoardManager.getInstance().potentialMills) {
                boolean isMill = false;
                for (int pos : mill) {
                    Node node = BoardManager.getInstance().getNode(pos);
                    if (node.isOccupied() && node.getStone().getOwner().getName().equalsIgnoreCase(player.getName())) {
                        isMill = true;
                    }
                }
                if (isMill) {
                    fromMillMoves.add(move);
                }
            }
        }

        // Return a move which does not have stone from the opponent as a neighbor to
        // the target node
        for (Node[] move : fromMillMoves) {
            List<Node> neighbors = move[0].getNeighbors();
            boolean opponentNeighbor = false;
            for (Node neighbor : neighbors) {
                if (neighbor.isOccupied() && neighbor.getStone().getOwner().getName().equalsIgnoreCase(opponent.getName())) {
                    opponentNeighbor = true;
                }
            }
            if (!opponentNeighbor) {
                return move;
            }
        }

        return null;
    }

    /**
     * Randomly selects and removes a node from the provided list of removable nodes.
     *
     * @param removals the list of nodes eligible for removal
     * @return the randomly selected and removed node
     */
    protected Node randomRemoval(List<Node> removals) {
        return removals.get(random.nextInt(removals.size()));
    }


    /**
     * Identifies and returns the most strategic stone to remove from the opponent.
     * Evaluates potential opponent moves and selects the removable stone whose
     * removal reduces the highest threat by disrupting mills.
     *
     * @param removableStones A list of nodes representing stones that can be removed.
     * @param opponentMoves   A list of opponent's potential moves, each represented
     *                        as a two-element array of nodes.
     * @param opponent        The imaginative player representing the opponent whose potential
     *                        moves and threat levels are to be analyzed.
     * @return The node representing the stone that should be removed to minimize threats,
     * or null if no suitable stone is identified.
     */
    protected Node removeMillThreat(List<Node> removableStones, List<Node[]> opponentMoves,
                                    ImaginativePlayer opponent) {
        Node bestTarget = null;
        int highestThreat = 0;

        for (Node removable : removableStones) {
            int threatScore = 0;

            for (Node[] move : opponentMoves) {
                if (move[0] == null) {
                    continue;
                }

                if (removable.getID() == move[0].getID()) {
                    int millsFormed = RuleManager.getInstance().countMillsFormed(move, opponent);
                    threatScore += millsFormed;
                }
            }

            if (threatScore > highestThreat) {
                highestThreat = threatScore;
                bestTarget = removable;
            }
        }

        return bestTarget;
    }
}
