package boardLogic;

import phases.*;
import players.*;
import view.*;

import java.util.ArrayList;
import java.util.List;

/**
 * The RuleManager is responsible for checking the validity of moves and
 * managing game rules.
 * It checks if moves are valid, counts mills formed, and determines if the game
 * is over.
 */
public class RuleManager {
    private static RuleManager instance;
    private String updateGUIErrorPrompt = "";

    /**
     * Creates a new Instance of this Manager, if there is none already.
     *
     * @return Instance if one already existed, creates a new one otherwise.
     */
    public static RuleManager getInstance() {
        if (instance == null) {
            instance = new RuleManager();
        }
        return instance;
    }

    //region Move Validation

    /**
     * Checks if the intended move is valid for the current game phase.
     *
     * @param player   The player that intends to perform a move.
     * @param fromNode The origin node (can be null in SettingPhase).
     * @param toNode   The target node.
     * @return {@code true} if move is valid, {@code false} otherwise.
     * @see #checkGeneralErrors(PlayerEntity, Node, Node)
     * @see #checkSpecificErrors(PlayerEntity, Node, Node)
     */
    public boolean isMoveValid(PlayerEntity player, Node fromNode, Node toNode) {
        String errors = "";
        errors += checkGeneralErrors(player, fromNode, toNode);
        errors += checkSpecificErrors(player, fromNode, toNode);

        // Show error prompt in GUI if there are errors
        if (!updateGUIErrorPrompt.isEmpty() && player instanceof Player) {
            GUIPrompt.updateErrorPrompt(updateGUIErrorPrompt);
        }

        // No errors occured
        if (errors.isEmpty()) {
            return true;
        }

        if (player instanceof Player) {
            System.out.print("[RULE] - ");
            System.out.print(errors);
        }
        return false;
    }

    /**
     * Helper method for {@link #isMoveValid(PlayerEntity, Node, Node)} method.
     * <p>
     * Checks for general errors that can occur in any game phase.
     *
     * @param player   The player that intends to perform a move.
     * @param fromNode The origin node (can be null in SettingPhase).
     * @param toNode   The target node.
     * @return A string containing error messages, empty if no errors occurred.
     */
    private String checkGeneralErrors(PlayerEntity player, Node fromNode, Node toNode) {
        String errors = "";

        if (fromNode != null && !(player.getPhase() instanceof SettingPhase)) {
            // No stone on fromNode
            if (!fromNode.isOccupied()) {
                errors += "No stone is placed on node (id=" + fromNode.getID() + ")!\n";
                updateGUIErrorPrompt = "Wähle ein Feld mit einem eigenen Stein.";
            }

            // Stone on fromNode does not belong to the player
            if (fromNode.getStone().getOwner() == GameManager.getInstance().getOpponent()) {
                errors += "The stone on node (id=" + fromNode.getID()
                        + ") does not belong to the player whose turn it is (" + player.getName() + "!\n";
                updateGUIErrorPrompt = "Du kannst nur deine eigenen Steine bewegen.";
            }
        }

        // Placing stone onto itself
        if (fromNode == toNode && !(player.getPhase() instanceof SettingPhase)) {
            errors += "You can not place a stone onto itself!\n";
            updateGUIErrorPrompt = "Du kannst keinen Stein auf dasselbe Feld setzen.";
        }

        // Placing stone onto another stone
        if (fromNode != toNode && toNode.isOccupied()) {
            errors += "You can not place a stone onto another stone!\n";
            updateGUIErrorPrompt = "Dieses Feld ist bereits besetzt.";
        }

        return errors;
    }

    /**
     * Helper method for {@link #isMoveValid(PlayerEntity, Node, Node)} method.
     * <p>
     * Checks for specific errors that can occur in certain game phases.
     *
     * @param player   The player that intends to perform a move.
     * @param fromNode The origin node (can be null in SettingPhase).
     * @param toNode   The target node.
     * @return A string containing error messages, empty if no errors occurred.
     */
    private String checkSpecificErrors(PlayerEntity player, Node fromNode, Node toNode) {
        String errors = "";
        GamePhase currentPhase = player.getPhase();

        if (currentPhase instanceof MovingPhase && fromNode != null) {
            // Stone not moved to a neighbor
            if (!fromNode.getNeighbors().contains(toNode)) {
                errors += "Can only move to neighbors! A stone can not be moved from " + fromNode.getID() + " to "
                        + toNode.getID() + "!\n";
                updateGUIErrorPrompt = "Du darfst nur auf benachbarte Felder ziehen.";
            }
        }
        // Performed move when game was already over
        else if (currentPhase instanceof GameOverPhase) {
            errors += "Game is already over. You must not make moves!";
            updateGUIErrorPrompt = "Das Spiel ist bereits vorbei.";
        } else if (!(currentPhase instanceof SettingPhase || currentPhase instanceof JumpingPhase)) {
            errors += "Player is not in a legal phase! Player " + player.getName() + " is in phase "
                    + player.getPhase().getClass().getSimpleName() + "!\n";
        }

        return errors;
    }

    /**
     * Checks if the removal of a stone is valid.
     * A stone can only be removed if it is not part of a mill, unless all remaining
     * stones of the opponent are part of a mill.
     *
     * @param remover    The player that intends to remove a stone.
     * @param targetNode The node where the stone to be removed is located.
     * @return {@code true} if the removal is valid, {@code false} otherwise.
     */
    public boolean isRemovalValid(PlayerEntity remover, Node targetNode) {
        boolean removalValid = false;
        Stone targetStone;
        PlayerEntity opponent;

        // Only remove stone, if the targetNode is valid
        if (targetNode.isOccupied() && targetNode.getStone().getOwner() != remover) {
            targetStone = targetNode.getStone();
            opponent = targetStone.getOwner();
            boolean freeStones = false;

            // Check if the opponent has a stone, that is not part of a mill
            for (Stone stone : opponent.getRemainingStones()) {
                if (countMillsFormed(stone) == 0) {
                    freeStones = true;
                    break;
                }
            }

            // Remove stone only if the target is not part of a mill
            // Exception if all remaining stones of the opponent are part of a mill
            if (countMillsFormed(targetStone) == 0 || !freeStones) {
                removalValid = true;
            }
        }
        return removalValid;
    }
    // endregion

    //region Mill Counting

    /**
     * Counts the new mills formed with the newly placed or moved stone.
     * Only gets called when a move is made, otherwise multiple mills would be
     * formed.
     *
     * @param lastPlacedStone The stone that potentially formed one or two mills.
     * @return The amount of new mills formed.
     * @throws IllegalStateException if the amount of newly formed mills is greater
     *                               than 2.
     */
    public int countMillsFormed(Stone lastPlacedStone) {
        BoardManager boardManager = BoardManager.getInstance();
        PlayerEntity player = lastPlacedStone.getOwner();

        int placedPos = lastPlacedStone.getNode().getID();
        int millCount = 0;

        for (int[] mill : boardManager.potentialMills) {
            // Extract potential mills that contain the position of the newly placed stone
            boolean includesPlaced = false;
            for (int pos : mill) {
                if (pos == placedPos) {
                    includesPlaced = true;
                    break;
                }
            }
            if (!includesPlaced)
                continue;

            // Check if all members of the potential mill are possessed by the current
            // player
            List<Integer> millStones = new ArrayList<>();
            int ownershipCounter = 0;
            for (int pos : mill) {

                Stone stone = boardManager.getNode(pos).getStone();
                if (stone != null && stone.getOwner().getName().equalsIgnoreCase(player.getName())) {
                    ownershipCounter++;
                    millStones.add(pos);
                }

                for (Stone playerStone : player.getRemainingStones()) {
                    if (playerStone.getNode().getID() == pos && !millStones.contains(pos)) {
                        ownershipCounter++;
                        millStones.add(playerStone.getNode().getID());
                    }
                }
            }
            // Add one formed mill to the counter
            if (ownershipCounter == 3) {
                millCount++;
                if (millCount > 2) {
                    throw new IllegalStateException("More than 2 mills formed — invalid state.");
                }
            }
        }

        return millCount;

    }

    /**
     * This method is specifically used to identify mills formed for planning a move by the CPU.
     * It simulates a move by temporarily placing a stone on the target node and
     * checking how many mills would be formed.
     * <p>
     * It does not affect the actual game state and is used for planning purposes.
     *
     * @param move   The move to be simulated, containing the from and to nodes.
     * @param player The player who is making the move.
     * @return The number of mills formed by the simulated move.
     */
    public int countMillsFormed(Node[] move, PlayerEntity player) {
        Node fromNode = move[0];
        Node toNode = move[1];
        Stone stone = null;
        if (fromNode != null) {
            stone = BoardManager.getInstance().getNode(fromNode.getID()).getStone();
            fromNode.removeStone();
        } else {
            stone = new Stone(player);
            player.addStone(stone);
        }
        stone.setNode(BoardManager.getInstance().getNode(toNode.getID()));
        toNode.placeStone(stone);

        int formedMills = countMillsFormed(stone);

        if (fromNode != null) {
            stone.setNode(BoardManager.getInstance().getNode(fromNode.getID()));
            fromNode.placeStone(stone);
        } else {
            player.removeStone(stone);
        }
        toNode.removeStone();

        return formedMills;
    }

    // endregion

    //region Game Over Checks

    /**
     * Checks if the game is over and sets the winner if so.
     * <p>Conditions for game over:
     * <ol>
     *   <li>A player has only 2 stones left (is in GameOverPhase).</li>
     *   <li>No mill was formed for 20 turns.</li>
     *   <li>A player can't move a stone to a valid position anymore (is in MovingPhase).</li>
     *   <li>The turn timer has run out.</li>
     * </ol>
     *
     * @param playerWhite The first player.
     * @param playerBlack The second player.
     * @return {@code true} if the game is over, {@code false} otherwise.
     * @see #isPlayerGameOver(PlayerEntity)
     * @see #isPlayerBlocked(PlayerEntity)
     * @see #isStalemate()
     */
    public boolean isGameOver(PlayerEntity playerWhite, PlayerEntity playerBlack) {
        GameManager gameManager = GameManager.getInstance();

        // Player black won
        if (isPlayerGameOver(playerWhite) || isPlayerBlocked(playerWhite)) {
            System.out.println("\n-- BLACK HAS WON! --");
            gameManager.setWinner(playerBlack);
            return true;
        }
        // Player white won
        if (isPlayerGameOver(playerBlack) || isPlayerBlocked(playerBlack)) {
            System.out.println("\n-- WHITE HAS WON! --");
            gameManager.setWinner(playerWhite);
            return true;
        }

        // Draw
        if (isStalemate()) {
            System.out.println("\n-- DRAW --");
            gameManager.setWinner(null);
            return true;
        }

        return false;
    }

    /**
     * Helper method for {@link #isGameOver(PlayerEntity, PlayerEntity)} method.
     * <p>
     * Checks if the player is in the game over phase (only 2 stones left).
     * Sets the loss cause to STONECOUNT if so.
     *
     * @param player The player that gets checked.
     * @return {@code true} if the player is in game over phase, {@code false} otherwise.
     */
    private boolean isPlayerGameOver(PlayerEntity player) {
        if (player.getRemainingStones().size() <= 2 && GameManager.getInstance().getOpponent().allStonesPlaced()) {
            GameManager.getInstance().lossCause = GameManager.LossCause.STONECOUNT;
            player.setPhase(new GameOverPhase(player));
            return true;
        }
        return false;
    }

    /**
     * Helper method for {@link #isGameOver(PlayerEntity, PlayerEntity)} method.
     * <p>
     * Checks if the player can perform any valid moves (is blocked).
     * Sets the loss cause to BLOCKED if the player is blocked.
     *
     * @param player The player that gets checked.
     * @return {@code true} if the player can't do any valid moves anymore, {@code false} otherwise.
     */
    public boolean isPlayerBlocked(PlayerEntity player) {
        // Player can only be blocked in the moving phase
        if (!(player.getPhase() instanceof MovingPhase)) {
            return false;
        }

        // Check for each stone if a neighbor node is occupied
        for (Stone stone : player.getRemainingStones()) {
            for (Node neighbor : stone.getNode().getNeighbors()) {
                if (!(neighbor.isOccupied())) {
                    return false;
                }
            }
        }

        GameManager.getInstance().lossCause = GameManager.LossCause.BLOCKED;
        return true;
    }

    /**
     * Helper method for {@link #isGameOver(PlayerEntity, PlayerEntity)} method.
     * <p>
     * Checks how many turns no mill was formed for.
     * Sets the loss cause to STALEMATE if there was no mill for 20 turns.
     *
     * @return true if there was no mill for 20 turns, false otherwise.
     */
    public boolean isStalemate() {
        GameManager gameManager = GameManager.getInstance();
        boolean stalemate = gameManager.getNoMillTurnCounter() >= 20;

        if (stalemate) {
            gameManager.lossCause = GameManager.LossCause.STALEMATE;
        }
        return stalemate;
    }
    // endregion
}