package players;

import boardLogic.Node;
import difficulties.DifficultyStrategy;


/**
 * Represents a CPU player in the game, which operates based on a specific strategy.
 * The CPU player uses a provided difficulty strategy to calculate its move and removal operations.
 * It simulates actions as if it were a human player, with a slight delay to mimic real-time decisions.
 */
public class CPUPlayer extends PlayerEntity {
    private DifficultyStrategy strategy;
    private PlayerEntity opponent;

    /**
     * Constructs a CPU player with the specified name and an opposing player.
     * The CPU player uses its strategy to calculate moves and removals during gameplay.
     *
     * @param name     The name of the CPU player.
     * @param opponent The opponent player that the CPU player will compete against.
     */
    public CPUPlayer(String name, PlayerEntity opponent) {
        super(name);
        this.opponent = opponent;
        this.strategy = null;
    }

    /**
     * Calculates the next move for the current CPU player based on the defined strategy.
     * This method creates imaginative representations of the CPU player and its opponent
     * to simulate possible moves and evaluate them using the assigned strategy.
     * <p>
     * To mimic the behavior of a human player, the method includes a delay before
     * determining and returning the calculated move.
     * <p>
     * If the sleeping thread is interrupted, an informational message is logged,
     * and the method returns null.
     *
     * @return An array of Node objects representing the calculated move, or null if
     * the process is interrupted.
     */
    public Node[] makeMove() {
        ImaginativePlayer imaginativeSelf = new ImaginativePlayer(this);
        ImaginativePlayer imaginativeOpponent = new ImaginativePlayer(opponent);
        Node[] calculatedMove = strategy.calculateMove(imaginativeSelf, imaginativeOpponent);
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            System.out.println("[INFO] - Sleep interrupted, exiting early.");
            return new Node[0];
        }
        return calculatedMove;
    }

    /**
     * Calculates the next removal action for the current CPU player based on the defined strategy.
     * This method creates imaginative representations of the CPU player and its opponent
     * to simulate possible removal scenarios and evaluate them using the assigned strategy.
     * <p>
     * To mimic the behavior of a human player, the method includes a delay before
     * determining and returning the calculated removal.
     * <p>
     * If the sleeping thread is interrupted, an informational message is logged,
     * and the method returns null.
     *
     * @return A Node object representing the calculated removal action, or null if
     * the process is interrupted.
     */
    public Node makeRemove() {
        ImaginativePlayer imaginativeSelf = new ImaginativePlayer(this);
        ImaginativePlayer imaginativeOpponent = new ImaginativePlayer(opponent);
        Node calculatedRemoval = strategy.calculateRemoval(imaginativeSelf, imaginativeOpponent);
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            System.out.println("[INFO] - Sleep interrupted, exiting early.");
            return null;
        }
        return calculatedRemoval;
    }

    /**
     * Sets the difficulty level of the CPU player by assigning a new strategy.
     * The strategy determines how the CPU player calculates moves and removals during gameplay.
     *
     * @param strategy The difficulty strategy to be applied to the CPU player's behavior.
     */
    public void setDifficulty(DifficultyStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Retrieves the difficulty strategy currently assigned to the CPU player.
     * The difficulty strategy determines how the CPU player makes decisions during gameplay.
     *
     * @return The DifficultyStrategy assigned to the CPU player.
     */
    public DifficultyStrategy getDifficulty() {
        return strategy;
    }
}