package boardLogic;

import players.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a node in the game board.
 * Each node can contain a stone and has a list of neighboring nodes.
 * Nodes are uniquely identified by their ID.
 */
public class Node {
    // region Fields
    private final int id;
    private Stone stone;
    private final List<Node> neighbors;
    // endregion

    /**
     * Constructs a node with the given ID.
     * Initializes the node with no {@link Stone} and an empty list of neighbors.
     *
     * @param id The unique identifier of the node.
     */
    public Node(int id) {
        this.id = id;
        this.stone = null;
        this.neighbors = new LinkedList<>();
    }

    /**
     * Checks whether this node contains a {@link Stone} or not.
     *
     * @return true if a stone is placed on this node, false otherwise.
     */
    public boolean isOccupied() {
        return stone != null;
    }

    /**
     * Adds a given node to the list of neighbors.
     *
     * @param node The node that gets added to the neighbors.
     */
    public void addNeighbor(Node node) {
        neighbors.add(node);
    }

    /**
     * Getter for the list containing all neighbor nodes.
     *
     * @return List containing all neighbor nodes
     */
    public List<Node> getNeighbors() {
        return neighbors;
    }

    /**
     * Assigns the given {@link Stone} to this node and vice versa. Can only possess one
     * stone at a time.
     *
     * @param stone The stone that gets assigned to this node.
     * @throws IllegalStateException if the node is already occupied.
     */
    public void placeStone(Stone stone) {
        if (this.stone != null) {
            throw new IllegalStateException("Node is occupied and rule did not catch error!");
        }
        this.stone = stone;
        stone.setNode(this);
    }

    /**
     * Removes the assigned stone from this node.
     *
     * @throws IllegalStateException if the node doesn't contain a stone
     */
    public void removeStone() {
        if (this.stone == null) {
            throw new IllegalStateException("Node is empty and rule did not catch error!");
        }
        stone = null;
    }

    /**
     * Getter for the node's ID.
     *
     * @return The current ID of the node.
     */
    public int getID() {
        return id;
    }

    /**
     * Getter for the node's stone.
     *
     * @return The current stone of the node. Null if not assigned.
     */
    public Stone getStone() {
        return stone;
    }

    /**
     * Creates a deep copy of this node including its stone (if present),
     * but without neighbor references. Neighbors must be connected separately.
     *
     * @param currentPlayer The copied current player.
     * @param opponent      The copied opponent player.
     * @return A new Node with the same ID and copied stone.
     */
    public Node deepCopy(PlayerEntity currentPlayer, PlayerEntity opponent) {
        Node copy = new Node(this.id);

        if (this.stone != null) {
            PlayerEntity owner = this.stone.getOwner();
            PlayerEntity newOwner = owner.getName().equalsIgnoreCase(currentPlayer.getName()) ? currentPlayer : opponent;
            Stone copiedStone = this.stone.deepCopy(newOwner);
            copy.placeStone(copiedStone);
        }

        return copy;
    }
}
