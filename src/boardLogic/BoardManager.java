package boardLogic;

import players.*;
import view.*;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Manages the state of the game board and interactions with it in the game.
 * Contains logic for initializing the board, managing node connections,
 * checking for valid moves, placing and removing stones, and detecting mills.
 * Implements the Observable pattern to notify observers of changes to the
 * board state (e.g., for updating the GUI or game logic).
 */
public class BoardManager extends Observable {
    public static final String WHITE = "white";
    public static final String WHITE_LOG = "WHITE";
    public static final String BLACK_LOG = "BLACK";

    // region Fields
    private static BoardManager instance;
    private Map<Integer, Node> nodes;
    private final int[][] neighbors = {{1, 9}, {0, 2, 4}, {1, 14}, {4, 10}, {1, 3, 5, 7}, {4, 13},
            {7, 11}, {4, 6, 8}, {7, 12},
            {0, 10, 21}, {3, 9, 11, 18}, {6, 10, 15}, {8, 13, 17}, {5, 12, 14, 20}, {2, 13, 23}, {11, 16},
            {15, 17, 19},
            {12, 16}, {10, 19}, {16, 18, 20, 22}, {13, 19}, {9, 22}, {19, 21, 23}, {14, 22}};
    public final List<int[]> potentialMills;
    // endregion

    // region Initialization

    /**
     * Initializes the board
     *
     * <pre>
     * 0 --------- 1 --------- 2
     * |           |           |
     * |   3 ----- 4 ----- 5   |
     * |   |       |       |   |
     * |   |   6 - 7 - 8   |   |
     * |   |   |       |   |   |
     * 9 - 10- 11      12- 13- 14
     * |   |   |       |   |   |
     * |   |   15- 16- 17  |   |
     * |   |       |       |   |
     * |   18----- 19----- 20  |
     * |           |           |
     * 21--------- 22--------- 23
     * </pre>
     */
    private BoardManager() {
        nodes = new HashMap<>();
        potentialMills = new ArrayList<>();

        initializeBoard();
        initializeMills();
    }

    /**
     * Creates a new Instance of this Manager, if there is none already.
     *
     * @return Instance if one already existed, creates a new one otherwise.
     */
    public static BoardManager getInstance() {
        if (instance == null) {
            instance = new BoardManager();
        }
        return instance;
    }

    /**
     * Initializes the board by creating {@link Node} and establishing connections between them.
     * <p>
     * This method performs the following operations:
     * 1. Creates 24 nodes, each with a unique ID, and adds them to the node dictionary.
     * 2. Connects nodes based on a predefined neighbor structure by calling the `connectNodes` method.
     * Connections are added in such a way to prevent duplicate associations.
     *
     * @see #connectNodes(Map, int, int)
     */
    public void initializeBoard() {
        // Adding all nodes to the dictionary
        for (int i = 0; i < 24; i++) {
            nodes.put(i, new Node(i));
        }

        // Connecting all nodes
        for (int node = 0; node < neighbors.length; node++) {
            for (int neighbor : neighbors[node]) {
                // Prevent duplicates
                if (node < neighbor) {
                    connectNodes(nodes, node, neighbor);
                }
            }
        }
    }

    /**
     * Initializes the potential mills on the game board by defining all possible combinations
     * of node groupings that form a mill. A mill is formed when three stones of the same color
     * align on specific positions.
     * <p>
     * This method groups nodes into horizontal and vertical mills:
     * - Horizontal mills are defined by sets of three consecutive nodes in a row.
     * - Vertical mills are defined by sets of three consecutive nodes in a column.
     * <p>
     * These predefined mill structures are used in gameplay mechanics to determine
     * if a player has formed a mill during their turn.
     */
    private void initializeMills() {
        // Horizontal mills
        potentialMills.add(new int[]{0, 1, 2});
        potentialMills.add(new int[]{3, 4, 5});
        potentialMills.add(new int[]{6, 7, 8});
        potentialMills.add(new int[]{9, 10, 11});
        potentialMills.add(new int[]{12, 13, 14});
        potentialMills.add(new int[]{15, 16, 17});
        potentialMills.add(new int[]{18, 19, 20});
        potentialMills.add(new int[]{21, 22, 23});

        // Vertical mills
        potentialMills.add(new int[]{0, 9, 21});
        potentialMills.add(new int[]{3, 10, 18});
        potentialMills.add(new int[]{6, 11, 15});
        potentialMills.add(new int[]{1, 4, 7});
        potentialMills.add(new int[]{16, 19, 22});
        potentialMills.add(new int[]{8, 12, 17});
        potentialMills.add(new int[]{5, 13, 20});
        potentialMills.add(new int[]{2, 14, 23});
    }

    /**
     * Connects two nodes on the board by adding each {@link Node} to the other's list of neighbors.
     *
     * @param board A map containing node IDs as keys and their corresponding Node objects as values.
     * @param id1   The ID of the first node to be connected.
     * @param id2   The ID of the second node to be connected.
     * @see Node#addNeighbor(Node)
     */
    private void connectNodes(Map<Integer, Node> board, int id1, int id2) {
        // Getting the nodes by their ID
        Node node1 = board.get(id1);
        Node node2 = board.get(id2);

        // Adding each other to their neighbors
        if (node1 != null && node2 != null) {
            node1.addNeighbor(node2);
            node2.addNeighbor(node1);
        }
    }
    // endregion

    // region Events

    /**
     * Places a {@link Stone} on the specified {@link Node} if the move is valid according to the rules.
     * The method verifies move validity, places the stone on the target node,
     * updates the GUI, and notifies observers if the placement is successful.
     *
     * @param player The player attempting to place the stone.
     * @param stone  The stone being placed on the board.
     * @param toNode The node where the stone is to be placed.
     * @return true if the stone is successfully placed, false otherwise.
     */
    public boolean placeStone(PlayerEntity player, Stone stone, Node toNode) {
        RuleManager ruleManager = RuleManager.getInstance();

        // Check if the stone can be placed correctly
        if (!ruleManager.isMoveValid(player, stone.getNode(), toNode)) {
            System.out.println("[RULE] -> Invalid move.");
            return false;
        }

        // Place stone and update GUI
        toNode.placeStone(stone);
        notifyObservers();

        System.out.println("[" + (player.getName().equalsIgnoreCase(WHITE) ?
                WHITE_LOG : BLACK_LOG) +
                "] - Placed stone on node " + toNode.getID());
        return true;
    }

    /**
     * Removes a {@link Stone} from the specified target {@link Node} if the removal is valid
     * according to the game rules. Updates the game state by removing the stone
     * from the node and the opponent player's list, and transitions the opponent
     * to the next game phase if necessary.
     *
     * @param remover    The player attempting to remove the stone.
     * @param targetNode The node from which the stone is to be removed.
     * @return true if the stone was successfully removed, false otherwise.
     */
    public boolean removeStone(PlayerEntity remover, Node targetNode) {
        boolean stoneRemoved = false;
        if (RuleManager.getInstance().isRemovalValid(remover, targetNode)) {
            Stone targetStone = targetNode.getStone();
            PlayerEntity opponent = targetStone.getOwner();
            targetNode.removeStone();
            opponent.removeStone(targetStone);
            opponent.getPhase().nextPhase();
            stoneRemoved = true;
            System.out.println("[" + (remover.getName().equalsIgnoreCase(WHITE) ? WHITE_LOG : BLACK_LOG) +
                    "] - Removed stone on node " + targetNode.getID());
        }
        return stoneRemoved;
    }

    /**
     * Moves a {@link Stone} from one {@link Node} to another if the move is valid according to the rules.
     * This method checks the move validity, transfers the stone from the source node to the target node,
     * updates the game state, logs the move, and notifies observers of the change.
     *
     * @param player   The player attempting to move the stone.
     * @param fromNode The node from which the stone is being moved.
     * @param toNode   The node to which the stone is being moved.
     * @return true if the stone is successfully moved, false otherwise.
     */
    public boolean moveStone(PlayerEntity player, Node fromNode, Node toNode) {
        RuleManager ruleManager = RuleManager.getInstance();

        // Check if the stone can be moved correctly
        if (!ruleManager.isMoveValid(player, fromNode, toNode)) {
            return false;
        }

        // Move the stone and update GUI
        Stone stone = fromNode.getStone();
        toNode.placeStone(stone);
        fromNode.removeStone();
        System.out.println("[" + (player.getName().equalsIgnoreCase(WHITE) ?
                WHITE_LOG : BLACK_LOG) + "] - Move stone from " + fromNode.getID() + " to " +
                toNode.getID());


        notifyObservers();

        return true;
    }
    // endregion

    // region Setters / Getters

    /**
     * Retrieves the current game board, represented as a mapping of node IDs to {@link Node} instances.
     *
     * @return A map where the key is the unique identifier (ID) of each node, and the value is the corresponding {@link Node} instance.
     */
    public Map<Integer, Node> getBoard() {
        return nodes;
    }

    /**
     * Retrieves the {@link Node} object associated with the specified ID from the game board.
     *
     * @param id The unique identifier of the node to retrieve.
     * @return The {@link Node} object if it exists in the board; otherwise, null.
     */
    public Node getNode(int id) {
        return nodes.get(id);
    }


    /**
     * Retrieves a list of {@link Node} objects that belong to the same ring as the specified node.
     * Rings are predefined groups of connected nodes on the game board, categorized as
     * outer, middle, and inner rings.
     *
     * @param nodeID The unique identifier of the node whose ring nodes are to be retrieved.
     *               Must correspond to a valid node on the board.
     * @return A list of {@link Node} objects that are part of the same ring as the specified node.
     * If the node does not belong to any predefined ring, an empty list is returned.
     */
    public List<Node> getRingNodes(int nodeID) {
        BoardManager boardManager = BoardManager.getInstance();

        // Ringe definieren
        int[][] rings = {
                {0, 1, 2, 9, 14, 21, 22, 23}, // äußerer Ring
                {3, 4, 5, 10, 13, 18, 19, 20}, // mittlerer Ring
                {6, 7, 8, 11, 12, 15, 16, 17} // innerer Ring
        };

        for (int[] ring : rings) {
            for (int id : ring) {
                if (id == nodeID) {
                    // Ring gefunden → Nodes sammeln
                    List<Node> ringNodes = new ArrayList<>();
                    for (int ringID : ring) {
                        ringNodes.add(boardManager.getNode(ringID));
                    }
                    return ringNodes;
                }
            }
        }

        return new ArrayList<>(); // nodeID gehört zu keinem bekannten Ring
    }

    /**
     * Sets up the board with a new configuration by deep copying the provided board and establishing
     * the necessary connections between nodes. The method clears the existing nodes, creates a deep copy
     * of the provided nodes, and reconnects all node neighbors.
     *
     * @param newBoard      A map containing the new configuration of nodes where the key is the node ID
     *                      and the value is the corresponding {@link Node}.
     * @param currentPlayer The player currently taking their turn. Used as context for the deep copy of nodes.
     * @param opponent      The opposing player. Used as context for the deep copy of nodes.
     */
    public void setBoard(Map<Integer, Node> newBoard, PlayerEntity currentPlayer, PlayerEntity opponent) {
        nodes.clear();

        for (Map.Entry<Integer, Node> entry : newBoard.entrySet()) {
            Node originalNode = entry.getValue();
            Node copyNode = originalNode.deepCopy(currentPlayer, opponent);
            nodes.put(entry.getKey(), copyNode);
        }

        for (int node = 0; node < neighbors.length; node++) {
            for (int neighbor : neighbors[node]) {
                // Prevent duplicates
                if (node < neighbor) {
                    connectNodes(nodes, node, neighbor);
                }
            }
        }
    }
    // endregion

    /**
     * Prints the current state of the game board, displaying each node's ID
     * and the owner of the stone at that node, if one exists.
     *
     * @param board A map where the keys are unique node IDs and the values are
     *              {@link Node} instances representing the nodes on the game board.
     *              Each {@link Node} may contain a stone whose owner's information
     *              is printed alongside the node ID.
     */
    public void printBoard(Map<Integer, Node> board) {
        System.out.println("-- BOARD -- ");
        for (Node node : board.values()) {
            if (node.getStone() != null) {
                String owner = (node.getStone() == null) ? "-" : node.getStone().getOwner().getName();
                String line = String.format("ID: %2d, Owner: %s", node.getID(), owner);
                System.out.println(line);
            }

        }

    }

    /**
     * Creates a deep copy of the current game board and reconnects the copied nodes based on
     * the neighbor structure. The method deep copies each {@link Node} using the provided player
     * context and reconstructs the neighbor connections in the copied board.
     *
     * @param currentPlayer The player currently taking their turn. Used as context for the deep copy of nodes.
     * @param opponent      The opposing player. Used as context for the deep copy of nodes.
     * @return A map representing the copied game board, where the key is the node ID and the value is the
     * corresponding {@link Node} instance in the new board.
     */
    public Map<Integer, Node> copyBoard(PlayerEntity currentPlayer, PlayerEntity opponent) {
        Map<Integer, Node> copiedBoard = new HashMap<>();

        for (Map.Entry<Integer, Node> entry : nodes.entrySet()) {
            Node originalNode = entry.getValue();
            Node copyNode = originalNode.deepCopy(currentPlayer, opponent);
            copiedBoard.put(entry.getKey(), copyNode);
        }

        for (int node = 0; node < neighbors.length; node++) {
            for (int neighbor : neighbors[node]) {
                // Prevent duplicates
                if (node < neighbor) {
                    connectNodes(copiedBoard, node, neighbor);
                }
            }
        }
        return copiedBoard;
    }

    /**
     * Resets the game board to its initial state.
     * <p>
     * This method performs the following operations:
     * 1. Clears all existing nodes on the board.
     * 2. Reinitializes the game board with default settings and connections between nodes by invoking {@link #initializeBoard()}.
     * 3. Notifies all observers about the changes in the board state.
     * <p>
     * It is typically used to start a new game or to reset the board after a game concludes.
     */
    public void reset() {
        nodes.clear();
        initializeBoard();
        notifyObservers();
    }
}
