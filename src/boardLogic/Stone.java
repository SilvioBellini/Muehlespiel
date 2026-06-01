package boardLogic;

import players.*;

/**
 * Represents a stone in the game, which is owned by a player and placed on a node.
 */
public class Stone {
    private PlayerEntity owner;
    private Node currentNode;

    /**
     * Creates a stone and sets its owner.
     *
     * @param owner The player that owns this stone.
     */
    public Stone(PlayerEntity owner) {
        this.owner = owner;
    }

    /**
     * Sets the node the stone is placed on.
     *
     * @param node The target node.
     */
    public void setNode(Node node) {
        currentNode = node;
    }

    /**
     * Getter for the node the stone currently is placed on.
     *
     * @return The current node.
     */
    public Node getNode() {
        return currentNode;
    }

    /**
     * Getter for the owner of the stone.
     *
     * @return The owner of the stone.
     */
    public PlayerEntity getOwner() {
        return owner;
    }

    /**
     * Creates a deep copy of this stone, assigning a new owner reference.
     *
     * @param newOwner The copied player to whom the new stone will belong.
     * @return A deep copy of this stone.
     */
    public Stone deepCopy(PlayerEntity newOwner) {
        Stone copy = new Stone(newOwner);

        if (this.currentNode != null) {
            copy.setNode(this.currentNode);
        } 
        
        return copy;
    }

}
