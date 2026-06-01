package persistance;

import java.util.Stack;

/**
 * This class is responsible for managing the history of moves made in the game.
 * It allows for undoing and redoing moves, as well as storing the current state of the game.
 * <p>
 * The history is represented by two stacks: one for undoing moves and another for redoing them.
 */
public class History {
    private Stack<Move> undoMoves = new Stack<>();
    private Stack<Move> redoMoves = new Stack<>();

    /**
     * Stores a performed move containing the state of the game to a Stack.
     * <p>
     * Clears the redo stack, as this method gets only called when performing a new move,
     * invalidating the other "reality".
     *
     * @param move The move that should be stored.
     */
    public void addMove(Move move) {
        undoMoves.push(move);
        clearRedo();
    }

    /**
     * Undoes the last move by popping it from the undo stack and pushing it onto the redo stack.
     * <p>
     * Returns the new top of the undo stack, representing the previous game state.
     * If there are not enough moves to undo, returns null.
     *
     * @return The previous game state (second last move), or null if undo is not possible.
     */
    public Move undoMove() {
        if(undoMoves.size() > 1) {
            Move undoMove = undoMoves.pop();
            redoMoves.push(undoMove);
            return undoMoves.peek();
        }
        return null;
    }

    /**
     * Redoes the last undone move by popping it from the redo stack and pushing it back onto the undo stack.
     * <p>
     * Returns the move that was redone to restore the next game state.
     * If there is no move to redo, returns null.
     *
     * @return The move representing the next game state, or null if redo is not possible.
     */
    public Move redoMove() {
        if (!redoMoves.empty()) {
            Move redoMove = redoMoves.pop();
            undoMoves.push(redoMove);
            return redoMove;
        }
    
        return null;
    }

    /**
     * Clears the redo stack by removing every element.
     */
    public void clearRedo() {
        if (!redoMoves.isEmpty()) {
            redoMoves.clear();
        }
    }

    /**
     * Getter for the undo moves stack.
     *
     * @return The stack containing the moves that can be undone.
     */
    public Stack<Move> getUndoMoves() {
        return undoMoves;
    }

    /**
     * Getter for the redo moves stack.
     *
     * @return The stack containing the moves that can be redone.
     */
    public Stack<Move> getRedoMoves() {
        return redoMoves;
    }
}
