package boardLogic;

import difficulties.DifficultyStrategy;
import persistance.*;
import phases.GamePhase;
import phases.SettingPhase;
import players.CPUPlayer;
import players.Player;
import players.PlayerEntity;
import view.BoardScreenPanel;
import view.GUI;
import view.GUIBoard;
import view.GUIPrompt;

import java.io.File;
import java.util.Map;

/**
 * Manages the game flow, including player turns, moves, and game state.
 * <p>
 * This class is a singleton and provides methods to start, run, and reset the game,
 * as well as to handle player moves and game over conditions.
 * <p>
 * It also manages the game timer and provides functionality for saving and loading
 * game states.
 */
public class GameManager {
    // region Fields
    // Managers
    private static GameManager instance;
    private final BoardManager boardManager;
    private final RuleManager ruleManager;
    private final GUI guiManager;
    private History history;
    private GamePersistenceManager persistenceManager;
    private TurnTimer timer;

    // Players
    private PlayerEntity playerWhite;
    private PlayerEntity playerBlack;
    private PlayerEntity currentPlayer;
    private volatile Node[] playerMove; // Store current move input
    private volatile Node playerRemove; // Store removal move input

    private Map<Integer, Node> boardCopy;

    // Loss Cause

    /**
     * Represents the various causes by which a player can lose the game.
     * <p>
     * This enumeration is used to determine and describe the condition under which
     * the game ended in a loss for a player.
     *
     * <p>Possible loss causes include:</p>
     * <ul>
     *   <li>{@link #NONE} – No loss has occurred (e.g., game still ongoing).</li>
     *   <li>{@link #STONECOUNT} – The player has fewer than three stones remaining.</li>
     *   <li>{@link #BLOCKED} – The player has no valid moves available (all stones are blocked).</li>
     *   <li>{@link #STALEMATE} – No mills have been formed for 20 turns.</li>
     *   <li>{@link #TIMEOUT} – The player ran out of time to perform his move.</li>
     * </ul>
     */
    public enum LossCause {
        NONE, STONECOUNT, BLOCKED, STALEMATE, TIMEOUT
    }

    public LossCause lossCause = LossCause.NONE;
    private PlayerEntity winner;

    // Game State
    private Thread gameThread;
    private boolean gameStatus; // true when game is running
    private String mode;
    private int timePerTurn = 30;
    private int noMillTurnCounter = 0;
    public static final String WHITE_NAME = "white";
    // endregion

    // region Initialization

    /**
     * Private constructor to enforce singleton pattern.
     * Initializes managers and sets up the game state.
     */
    private GameManager() {
        boardManager = BoardManager.getInstance();
        ruleManager = RuleManager.getInstance();
        guiManager = GUI.getInstance();

        history = new History();
        persistenceManager = new GamePersistenceManager();
    }

    /**
     * Returns the singleton instance of {@code GameManager}.
     * <p>
     * If the instance is not yet created, it initializes a new one.
     *
     * @return The singleton instance of GameManager.
     */
    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    /**
     * Initializes listeners for player moves and removals on the provided GUI board.
     * <p>
     * This method sets up the move and remove listeners on the {@link GUIBoard} to
     * capture player input.
     *
     * @param guiBoard The GUI board to which the listeners should be attached.
     */
    public void initializeListeners(GUIBoard guiBoard) {
        guiBoard.setMoveListener(move -> {
            this.playerMove = move;
            synchronized(this) {
                this.notify();
            }
        });

        guiBoard.setRemoveListener(move -> {
            this.playerRemove = move;
            synchronized(this) {
                this.notify();
            }
        });
    }

    // endregion

    // region Gameflow

    /**
     * Gets called when the startbutton gets pressed and initializes the game in a
     * separate thread.
     * <p>
     * Creates players according to chosen {@code mode} and sets the {@code gameStatus} to {@code true},
     * indicating the game is running.
     * <p>
     * Adds an initial move to the history allowing to undo until the very start of
     * the game.
     *
     * @param mode       Can either be Singleplayer (Player and CPU) or Multiplayer
     *                   (Two players).
     * @param difficulty The difficulty of the CPU player in Singleplayer mode.
     */
    public void startGame(String mode, DifficultyStrategy difficulty) {
        this.mode = mode;

        // Starting Singleplayer mode with a Player and a CPUPlayer
        if (mode.equalsIgnoreCase("singleplayer")) {
            playerWhite = new Player(WHITE_NAME);
            playerBlack = new CPUPlayer("black", playerWhite);
            ((CPUPlayer) playerBlack).setDifficulty(difficulty);
            gameStatus = true;
            currentPlayer = playerWhite;
            startTurnTimer();
        }
        // Starting Multiplayer mode with 2 Players
        else if (mode.equalsIgnoreCase("multiplayer")) {
            playerWhite = new Player(WHITE_NAME);
            playerBlack = new Player("black");
            gameStatus = true;
            currentPlayer = playerWhite;
            startTurnTimer();
        } else {
            System.out.println("[ERROR] - Invalid mode!");
        }

        // Adding the initial state of the game to the history, allowing to undo until
        boardCopy = boardManager.copyBoard(currentPlayer, getOpponent());

        Move initialMove = new Move(0, 0, boardCopy, true, new SettingPhase(playerWhite), new SettingPhase(playerBlack), 0, timer.getTimeLeft());
        history.addMove(initialMove);

        guiManager.update();

        gameThread = new Thread(this::runGame);
        gameThread.start();
    }

    /**
     * Controls the game loop, alternating between players and handling their moves.
     * <p>
     * Waits for player input (or CPU moves) using {@link #getMoveInput(boolean)}
     * and
     * processes the selected move. If a mill is formed, it waits for a stone to be
     * removed
     * via {@link #getRemoveInput(boolean)}.
     * <p>
     * After each move (and removal, if applicable), it checks whether the game is
     * over
     * and saves the current state in the {@code History}.
     * <p>
     * <strong>Important:</strong> This method may be run in a separate thread,
     * which can be interrupted
     * during waiting phases (e.g. when calling {@code wait()} for player input). In
     * such cases,
     * the input methods may return {@code null}. Callers must explicitly check for
     * {@code null}
     * values before accessing move arrays (e.g. {@code move[0]} or {@code move[1]})
     * to avoid
     * {@link NullPointerException}.
     *
     * @see #getMoveInput(boolean)
     * @see #getRemoveInput(boolean)
     * @see #saveMove()
     * @see #resetGame()
     */
    public void runGame() {
        Node[] move;
        Node remove;

        while (gameStatus) {
            boolean isPlayer = currentPlayer instanceof Player;
            GUIPrompt.updateGamePrompt(currentPlayer, WHITE_NAME);

            // Handle moves of players
            move = getMoveInput(isPlayer);

            if (move.length == 0) {
                System.out.println("[INFO] - Game thread interrupted during move input.");
                return;
            }

            Node fromNode = move[0];
            Node toNode = move[1];

            // Perform move according to choices of players
            handleMove(currentPlayer, fromNode, toNode);

            boardManager.notifyObservers();
            if (!(currentPlayer.getPhase() instanceof SettingPhase)) {
                noMillTurnCounter++;
            }

            // Handle removal of stones
            int millCount = ruleManager.countMillsFormed(toNode.getStone());
            while (millCount > 0) {
                GUIPrompt.updateRemovePrompt(currentPlayer, WHITE_NAME);
                noMillTurnCounter = 0;
                BoardScreenPanel.guiBoard.setRemoveMode(true);
                remove = getRemoveInput(isPlayer);

                if (remove == null) {
                    System.out.println("[INFO] - Game thread interrupted during removal.");
                    return;
                }

                // Perform remove according to choices of players
                if (!boardManager.removeStone(currentPlayer, remove)) {
                    continue;
                }

                BoardScreenPanel.guiBoard.setRemoveMode(false);
                boardManager.notifyObservers();
                millCount--;
            }

            if (ruleManager.isGameOver(playerWhite, playerBlack)) {
                gameOver();
                return;
            }

            saveMove();
            switchPlayer();
        }
    }

    /**
     * Helpermethod for {@link #runGame()}.
     * <p>
     * Waits for a valid player input (via drag and drop) or performs the CPU move.
     *
     * @param isPlayer {@code true} if the current player is human, {@code false} if it's a
     *                 CPUPlayer.
     * @return A Node array [fromNode, toNode], or null if interrupted
     */
    private Node[] getMoveInput(boolean isPlayer) {
        if (isPlayer) {
            playerMove = null;

            synchronized(this) {
                while (playerMove == null) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return new Node[0];
                    }
                }
            }
            return playerMove;
        } else {
            return ((CPUPlayer) currentPlayer).makeMove();
        }
    }

    /**
     * Helpermethod for {@link #runGame()}
     * <p>
     * Waits for the selection of an opponent's stone to remove, or lets the CPU
     * automatically choose a stone to remove.
     *
     * @param isPlayer true if the current player is human
     * @return The Node selected for removal, or null if interrupted
     */
    private Node getRemoveInput(boolean isPlayer) {
        if (isPlayer) {
            playerRemove = null;

            synchronized(this) {
                while (playerRemove == null) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return null;
                    }
                }
            }
            return playerRemove;
        } else {
            return ((CPUPlayer) currentPlayer).makeRemove();
        }
    }

    /**
     * Switches the current player to the opponent.
     * <p>
     * Updates the timer for the new current player and resets the timer.
     */
    public void switchPlayer() {
        currentPlayer = getOpponent();
        guiManager.updateTimer(timePerTurn);
        timer.resetTimer();
    }

    /**
     * Delegates the move done by a player to the current phase the player is in.
     * Saves the last move and switches the player, if the move was successful.
     *
     * @param player   The player making a move.
     * @param fromNode The origin node.
     * @param toNode   The target node.
     */
    public void handleMove(PlayerEntity player, Node fromNode, Node toNode) {
        player.getPhase().handleMove(fromNode, toNode);
    }

    /**
     * Gets called when a loss condition gets fulfilled.
     * Leads to the Game over screen with the winner as a parameter.
     */
    public void gameOver() {
        stopTurnTimer();
        gameStatus = false;
        guiManager.showGameOverMsg(winner);
    }

    /**
     * Resets the game and prepares it to be started again.
     * <p>
     * This method interrupts the current game thread to stop ongoing operations.
     * As a result, methods waiting for player input in {@link #runGame()} (such as
     * {@code getMoveInput}
     * or {@code getRemoveInput}) may return {@code null}.
     * <p>
     * Callers of {@link #runGame()} must therefore explicitly handle {@code null}
     * values
     * to avoid {@link NullPointerException}, especially when accessing move arrays
     * (e.g. {@code move[0]}).
     *
     * @see #runGame()
     */
    public void resetGame() {
        if (gameStatus) {
            playerWhite.setPlacedStones(0);
            playerBlack.setPlacedStones(0);
        }

        stopTurnTimer();
        gameStatus = false;
        noMillTurnCounter = 0;
        history = new History();

        playerMove = null;
        playerRemove = null;
        lossCause = LossCause.NONE;

        boardManager.reset();
        guiManager.reset();

        if (gameThread != null && gameThread.isAlive()) {
            gameThread.interrupt();
        }

    }
    // endregion

    // region Setters / Getters

    /**
     * Setter for the player that has won the game.
     *
     * @param winner The player that has won.
     */
    public void setWinner(PlayerEntity winner) {
        this.winner = winner;
    }

    /**
     * Getter for the player that is currently able to perform a move.
     *
     * @return playerWhite or playerBlack
     */
    public PlayerEntity getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Getter for the player that is not currently able to perform a move.
     * Helper method for switching players.
     *
     * @return playerWhite if the current player is playerBlack and vice versa.
     */
    public PlayerEntity getOpponent() {
        if (currentPlayer == playerWhite) {
            return playerBlack;
        } else {
            return playerWhite;
        }
    }

    /**
     * Getter for the amount of turns that no mill was formed.
     *
     * @return The value of the noMillTurnCounter.
     */
    public int getNoMillTurnCounter() {
        return noMillTurnCounter;
    }

    /**
     * Setter for the time a player has to perform a move before he loses the game.
     *
     * @param timePerTurn The new time a player shall have to perform a move.
     */
    public void setTimePerTurn(int timePerTurn) {
        this.timePerTurn = timePerTurn;
    }

    /**
     * Getter for the current status of the game.
     *
     * @return true if the game is running, false otherwise.
     */
    public boolean getGameStatus() {
        return gameStatus;
    }

    public PlayerEntity getPlayerWhite() {
        return playerWhite;
    }

    public PlayerEntity getPlayerBlack() {
        return playerBlack;
    }
    // endregion

    // region Timer

    /**
     * Starts a new timer with the set time per turn.
     * Updates the timer label on every tick (time per tick set in the timer class).
     * When the time runs out, the game is over and the current player loses.
     */
    public void startTurnTimer() {
        timer = new TurnTimer(timePerTurn, () -> guiManager.updateTimer(timer.getTimeLeft()), // Happens each Tick
                () -> { // Happens when the time runs out
                    lossCause = LossCause.TIMEOUT;
                    winner = getOpponent();
                    gameOver();
                });

        timer.startTimer();
    }

    /**
     * Stops the timer, if it is initialized correctly.
     */
    public void stopTurnTimer() {
        if (timer != null) {
            timer.stopTimer();
        }
    }
    // endregion

    // region Save / Restore Moves

    /**
     * Undoes a move, restoring the state before the last performed move, if there
     * is a undo move.
     *
     * @see #loadMove(Move)
     */
    public void undoMove() {
        Move lastMove;
        if (playerBlack instanceof CPUPlayer) {
            for (int i = 0; i < 2; i++) {
                lastMove = history.undoMove();
                if (lastMove != null) {
                    loadMove(lastMove);
                }
            }
        } else {
            lastMove = history.undoMove();
            if (lastMove != null) {
                loadMove(lastMove);
            }
        }
    }

    /**
     * Redoes a move, restoring the state that was undone prior, if there is a move
     * to redo.
     *
     * @see #loadMove(Move)
     */
    public void redoMove() {
        Move nextMove;
        if (playerBlack instanceof CPUPlayer) {
            for (int i = 0; i < 2; i++) {
                nextMove = history.redoMove();
                if (nextMove != null) {
                    loadMove(nextMove);
                }
            }
        } else {
            nextMove = history.redoMove();
            if (nextMove != null) {
                loadMove(nextMove);
            }
        }
    }

    /**
     * Adds the current state of the game to the History (Memento).
     * This includes both players, the current state of the board, the number of
     * moves no mill was formed
     * and the time left for the move.
     */
    public void saveMove() {
        int placedStonesWhite = playerWhite.getPlacedStones();
        int placedStonesBlack = playerBlack.getPlacedStones();
        boardCopy = boardManager.copyBoard(getCurrentPlayer(), getOpponent());
        boolean currentIsWhite = !(currentPlayer.getName().equalsIgnoreCase(WHITE_NAME));
        GamePhase phaseWhite = playerWhite.getPhase();
        GamePhase phaseBlack = playerBlack.getPhase();

        Move move = new Move(placedStonesWhite, placedStonesBlack, boardCopy, currentIsWhite, phaseWhite, phaseBlack, noMillTurnCounter, timer.getTimeLeft());
        history.addMove(move);
    }

    /**
     * Helper-Method for undoing or redoing a move.
     * Resets all Parameters that got changed after performing a move.
     *
     * @param move The move that should be loaded. Can be an undo or redo move.
     * @throws IllegalArgumentException if the move is null.
     * @see #undoMove()
     * @see #redoMove()
     */
    public void loadMove(Move move) {
        if (move == null) {
            throw new IllegalArgumentException("[FATAL] loadMove wurde mit null aufgerufen – verbotener Pfad!");
        }

        currentPlayer = (move.getCurrentIsWhite() ? playerWhite : playerBlack);
        PlayerEntity opponent = getOpponent();

        // 1. Restore the Board
        Map<Integer, Node> boardFromMove = move.getBoardSnapshot();
        boardManager.setBoard(boardFromMove, currentPlayer, opponent);

        // 2. Restore the player fields from the actual board
        // clear stone list
        currentPlayer.getRemainingStones().clear();
        opponent.getRemainingStones().clear();

        // build stone list
        for (Node node : boardManager.getBoard().values()) {
            Stone stone = node.getStone();
            if (stone != null) {
                PlayerEntity owner = stone.getOwner();
                (owner.getName().equalsIgnoreCase(currentPlayer.getName()) ? currentPlayer : opponent).addStone(stone);
            }
        }

        // 3. Set the placedStones based on stone list size
        playerWhite.setPlacedStones(move.getPlacedStonesWhite());
        playerBlack.setPlacedStones(move.getPlacedStonesBlack());

        // 4. Checks if the Players was in another Phase before saveMove
        playerWhite.setPhase(move.getPhaseWhite());
        playerBlack.setPhase(move.getPhaseBlack());

        // 5. Restore the primitive Fields
        noMillTurnCounter = move.getNoMillTurnCounter();
        timer.setTimeLeft(move.getTimeLeft());

        boardManager.notifyObservers();
        GUIPrompt.updateGamePrompt(currentPlayer, WHITE_NAME);
    }

    // endregion

    // region Save / Restore Game

    /**
     * Saves the current game state to a JSON file.
     * <p>
     * This method creates a {@link GameStateData} object containing the current players,
     * their phases, the board state, and other relevant information.
     * <p>
     * After saving, it restarts the turn timer to ensure the game continues
     * correctly.
     *
     * @param targetFile The target file to which the game state should be saved.
     */
    public void saveGame(File targetFile) {
        GameStateData gameState = persistenceManager.createGameState(
                playerWhite, playerBlack, currentPlayer, history, noMillTurnCounter,
                timePerTurn, timer.getTimeLeft(), mode
        );
        persistenceManager.saveGameState(gameState, targetFile);
        startTurnTimer();
    }

    /**
     * Loads a game state from a JSON file.
     * <p>
     * This method reads the saved game state from the specified file and reconstructs
     * the players, their phases, and the board state.
     * <p>
     * After loading, it updates the {@link GUI} and starts the game thread to continue
     * gameplay.
     *
     * @param file The JSON file containing the saved game state.
     */
    public void loadGame(File file) {
        GameStateData data = persistenceManager.restoreGameState(file);

        PlayerEntity loadedWhite = persistenceManager.fromPlayerData(data.whiteData, null, true);
        PlayerEntity loadedBlack = persistenceManager.fromPlayerData(data.blackData, loadedWhite, true);
        this.playerWhite = loadedWhite;
        this.playerBlack = loadedBlack;

        if (data.currentIsWhite) {
            this.currentPlayer = loadedWhite;
        } else {
            this.currentPlayer = loadedBlack;
        }

        noMillTurnCounter = data.noMillTurnCounter;
        timePerTurn = data.timePerTurn;
        mode = data.gameMode;
        GUI.mode = data.gameMode;

        // Reconstruct history
        history = new History();
        for (MoveData moveData : data.undoMoves) {
            history.getUndoMoves().add(persistenceManager.fromMoveData(moveData, loadedWhite, loadedBlack));
        }
        for (MoveData moveData : data.redoMoves) {
            history.getRedoMoves().add(persistenceManager.fromMoveData(moveData, loadedWhite, loadedBlack));
        }

        gameStatus = true;
        startTurnTimer();

        undoMove();
        redoMove();

        gameThread = new Thread(this::runGame);
        gameThread.start();

    }
    // endregion
}