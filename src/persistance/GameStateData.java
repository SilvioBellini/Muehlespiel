package persistance;

import java.util.List;

/**
 * Represents a snapshot of the game state at a particular moment.
 */
public class GameStateData {
    public final PlayerData whiteData;
    public final PlayerData blackData;
    public final boolean currentIsWhite;
    public final List<MoveData> undoMoves;
    public final List<MoveData> redoMoves;
    public final int noMillTurnCounter;
    public final int timePerTurn;
    public final int timeLeft;
    public final String gameMode;

    /**
     * Creates a new GameStateData capturing the current game state.
     *
     * @param whiteData          The data of the white player.
     * @param blackData          The data of the black player.
     * @param currentIsWhite     Indicates if it's currently the white player's turn.
     * @param undoMoves          The list of moves that can be undone.
     * @param redoMoves          The list of moves that can be redone.
     * @param noMillTurnCounter  The number of consecutive turns without a formed mill.
     * @param timePerTurn        The time allocated for each turn in seconds.
     * @param timeLeft           The remaining time for the current player in seconds.
     * @param gameMode           The mode of the game (e.g., "singleplayer" or "multiplayer").
     */
    public GameStateData(PlayerData whiteData, PlayerData blackData, boolean currentIsWhite,
                         List<MoveData> undoMoves, List<MoveData> redoMoves, int noMillTurnCounter,
                         int timePerTurn, int timeLeft, String gameMode) {
        this.whiteData = whiteData;
        this.blackData = blackData;
        this.currentIsWhite = currentIsWhite;
        this.undoMoves = undoMoves;
        this.redoMoves = redoMoves;
        this.noMillTurnCounter = noMillTurnCounter;
        this.timePerTurn = timePerTurn;
        this.timeLeft = timeLeft;
        this.gameMode = gameMode;
    }
}
