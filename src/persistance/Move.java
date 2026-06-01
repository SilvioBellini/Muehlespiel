package persistance;

import boardLogic.*;
import phases.*;

import java.util.Map;

/**
 * Represents a snapshot of the game state at a particular moment.
 * Used for undo and redo functionality in the {@link History}.
 */
public class Move {
    // region Fields
    private final int placedStonesWhite;
    private final int placedStonesBlack;
    private final Map<Integer, Node> boardSnapshot;
    private final boolean currentIsWhite;
    private final GamePhase phaseWhite;
    private final GamePhase phaseBlack;
    private final int noMillTurnCounter;
    private final int timeLeft;
    // endregion

    /**
     * Constructs a Move object representing the game state at a specific moment.
     *
     * @param placedStonesWhite The number of stones placed by the white player.
     * @param placedStonesBlack The number of stones placed by the black player.
     * @param boardSnapshot A snapshot of the board state at this move.
     * @param currentIsWhite Indicates if it is currently the white player's turn.
     * @param phaseWhite The game phase for the white player.
     * @param phaseBlack The game phase for the black player.
     * @param noMillTurnCounter The number of turns without a formed mill.
     * @param timeLeft The time left for the move in seconds.
     */
    public Move(int placedStonesWhite, int placedStonesBlack, Map<Integer, Node> boardSnapshot, boolean currentIsWhite, GamePhase phaseWhite, GamePhase phaseBlack, int noMillTurnCounter, int timeLeft) {
        this.placedStonesWhite = placedStonesWhite;
        this.placedStonesBlack = placedStonesBlack;
        this.boardSnapshot = boardSnapshot;
        this.currentIsWhite = currentIsWhite;
        this.phaseWhite = phaseWhite;
        this.phaseBlack = phaseBlack;
        this.noMillTurnCounter = noMillTurnCounter;
        this.timeLeft = timeLeft;
    }

    // region Getters

    /**
     * Returns the number of stones the white player has already placed.
     *
     * @return The amount of placed stones.
     */
    public int getPlacedStonesWhite() {
        return placedStonesWhite;
    }

    /**
     * Returns the number of stones the black player has already placed.
     *
     * @return The amount of placed stones.
     */
    public int getPlacedStonesBlack() {
        return placedStonesBlack;
    }

    /**
     * Returns the snapshot of the board when this move was made.
     *
     * @return A map representing the board state.
     */
    public Map<Integer, Node> getBoardSnapshot() {
        return boardSnapshot;
    }

    /**
     * Returns whether it is currently the white player's turn.
     *
     * @return {@code true} if it is white's turn, {@code false} otherwise.
     */
    public boolean getCurrentIsWhite() {
        return currentIsWhite;
    }

    /**
     * Returns the game phase for the white player at the time of this move.
     *
     * @return The game phase for white.
     */
    public GamePhase getPhaseWhite() {
        return phaseWhite;
    }

    /**
     * Returns the game phase for the black player at the time of this move.
     *
     * @return The game phase for black.
     */
    public GamePhase getPhaseBlack() {
        return phaseBlack;
    }

    /**
     * Returns the number of turns without a formed mill at the time of this move.
     *
     * @return The no mill turn counter.
     */
    public int getNoMillTurnCounter() {
        return noMillTurnCounter;
    }

    /**
     * Returns the time left for the move in seconds.
     *
     * @return The remaining time.
     */
    public int getTimeLeft() {
        return timeLeft;
    }
    // endregion
}
