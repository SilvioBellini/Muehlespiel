package persistance;

import java.util.Map;

/**
 * Represents a serializable snapshot of a move from the history.
 */
public class MoveData {
    public int placedStonesWhite;
    public int placedStonesBlack;
    public Map<Integer, Integer> boardSnapshot;
    public boolean currentIsWhite;
    public String phaseWhite;
    public String phaseBlack;
    public int noMillTurnCounter;
    public int timeLeft;

    /**
     * Creates a new MoveData instance capturing the state of the game at a specific move.
     *
     * @param placedStonesWhite The number of stones placed by the white player.
     * @param placedStonesBlack The number of stones placed by the black player.
     * @param boardSnapshot     A snapshot of the board state at this move.
     * @param currentIsWhite    Indicates if it is currently the white player's turn.
     * @param phaseWhite        The game phase for the white player at this move.
     * @param phaseBlack        The game phase for the black player at this move.
     * @param noMillTurnCounter The number of consecutive turns without forming a mill.
     * @param timeLeft          The remaining time for the current player in seconds.
     */
    public MoveData(int placedStonesWhite, int placedStonesBlack, Map<Integer, Integer> boardSnapshot, boolean currentIsWhite, String phaseWhite, String phaseBlack, int noMillTurnCounter, int timeLeft) {
        this.placedStonesWhite = placedStonesWhite;
        this.placedStonesBlack = placedStonesBlack;
        this.boardSnapshot = boardSnapshot;
        this.currentIsWhite = currentIsWhite;
        this.phaseWhite = phaseWhite;
        this.phaseBlack = phaseBlack;
        this.noMillTurnCounter = noMillTurnCounter;
        this.timeLeft = timeLeft;
    }
}
