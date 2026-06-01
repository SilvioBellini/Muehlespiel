package view;

import boardLogic.*;
import phases.SettingPhase;
import players.PlayerEntity;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * This class is responsible for rendering and managing the graphical
 * representation of the game board in a Nine Men's Morris game.
 * <p>
 * It handles the visual components of the board, including drawing the board structure,
 * placing and removing stones, highlighting potential moves, and responding to user interactions.
 * This class forms the visual bridge between the game's logic and the user interface.
 */
public class GUIBoard extends JPanel {
    // region Fields
    private Integer fromNodeId = null;
    private Node fromNode = null;
    private boolean removeMode = false;

    private static final String WHITE_NAME = "white";
    private static final String BLACK_NAME = "black";

    // Listeners
    private Consumer<Node[]> moveListener = null;
    private Consumer<Node> removeListener = null;

    // Positions
    private final Map<Integer, Point> nodePositions = new HashMap<>();
    private static final int BOARD_SIZE = 400;
    private int offsetX;
    private int offsetY;

    // Drag & Drop
    private Point dragPosition = null;
    private Color draggedColor = null;
    private static final int STONE_RADIUS = 15;

    // Stones on the side of the board
    private final List<Point> whiteStoneSlots = new ArrayList<>();
    private final List<Point> blackStoneSlots = new ArrayList<>();
    private static final int MAX_STONES = 9;

    // Colors
    private static final Color BEIGE = new Color(16, 104, 166);

    // endregion

    // region Initialization

    public GUIBoard() {
        GameManager gameManager = GameManager.getInstance();

        setPreferredSize(new Dimension(700, 500));
        setOpaque(false);
        initNodePositions();
        initStoneSlots();

        addMouseListener(new MouseAdapter() {
            /**
             * Handles mouse press events for selecting nodes or picking up stones.
             * If the remove mode is active, it allows the player to remove a stone.
             *
             * @param e The mouse event containing the click position.
             */
            @Override
            public void mousePressed(MouseEvent e) {
                if (!gameManager.getGameStatus()) {
                    return;
                }

                if (!removeMode) {
                    PlayerEntity currentPlayer = gameManager.getCurrentPlayer();
                    if (handleStonePickup(e, currentPlayer)) {
                        return;
                    }
                    handleNodeSelection(e, currentPlayer);
                } else {
                    signalPlayerRemove(e.getPoint());
                }
            }

            /**
             * Handles mouse release events for executing player moves.
             * If a stone is being dragged, it checks if the release position is valid.
             *
             * @param e The mouse event containing the release position.
             */
            @Override
            public void mouseReleased(MouseEvent e) {
                if (!gameManager.getGameStatus() || draggedColor == null) {
                    return;
                }

                tryExecutePlayerMove(e);
                repaint();  // When the player "loses" the stone without snapping into a valid Node

                // Reset dragging information
                dragPosition = null;
                draggedColor = null;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            /**
             * Handles mouse drag events to update the position of the dragged stone.
             * If a stone is being dragged, it updates the drag position and repaints the board.
             *
             * @param e The mouse event containing the current position of the mouse.
             */
            @Override
            public void mouseDragged(MouseEvent e) {
                if (!gameManager.getGameStatus()) {
                    return;
                }

                if (draggedColor != null) {
                    dragPosition = e.getPoint();
                    repaint();
                }
            }
        });
    }

    /**
     * Initialize the Map with 24 Entries, with a specific position (Point) on the Board
     * The points are necessary for Mouse listeners.
     */
    private void initNodePositions() {
        nodePositions.put(0, new Point(0, 0));
        nodePositions.put(1, new Point(200, 0));
        nodePositions.put(2, new Point(400, 0));
        nodePositions.put(3, new Point(50, 50));
        nodePositions.put(4, new Point(200, 50));
        nodePositions.put(5, new Point(350, 50));
        nodePositions.put(6, new Point(100, 100));
        nodePositions.put(7, new Point(200, 100));
        nodePositions.put(8, new Point(300, 100));
        nodePositions.put(9, new Point(0, 200));
        nodePositions.put(10, new Point(50, 200));
        nodePositions.put(11, new Point(100, 200));
        nodePositions.put(12, new Point(300, 200));
        nodePositions.put(13, new Point(350, 200));
        nodePositions.put(14, new Point(400, 200));
        nodePositions.put(15, new Point(100, 300));
        nodePositions.put(16, new Point(200, 300));
        nodePositions.put(17, new Point(300, 300));
        nodePositions.put(18, new Point(50, 350));
        nodePositions.put(19, new Point(200, 350));
        nodePositions.put(20, new Point(350, 350));
        nodePositions.put(21, new Point(0, 400));
        nodePositions.put(22, new Point(200, 400));
        nodePositions.put(23, new Point(400, 400));
    }

    /**
     * Initialize the Array with 9 Stones, with a specific position (Point) on the Board
     * The points are necessary for Mouse listeners.
     */
    private void initStoneSlots() {
        for (int i = 0; i < 9; i++) {
            whiteStoneSlots.add(new Point(25, 80 + i * 42));
            blackStoneSlots.add(new Point(675, 80 + i * 42));
        }
    }
    // endregion

    // region Mouse Interaction

    /**
     * Handles the logic for picking up a stone from the side of the board.
     * Checks if the current player is trying to pick up their own stone.
     *
     * @param e             The mouse event containing the click position.
     * @param currentPlayer The player who is currently taking their turn.
     * @return {@code true} if a stone was successfully picked up, {@code false} otherwise.
     */
    private boolean handleStonePickup(MouseEvent e, PlayerEntity currentPlayer) {
        int stonesLeft = MAX_STONES - currentPlayer.getPlacedStones();

        // Checks if the white player wants to move his own stone
        if (currentPlayer.getName().equalsIgnoreCase(WHITE_NAME)) {
            for (int i = 0; i < stonesLeft; i++) {
                if (whiteStoneSlots.get(i).distance(e.getPoint()) < STONE_RADIUS) {
                    draggedColor = Color.WHITE;
                    dragPosition = e.getPoint();
                    return true;
                }
            }
        }

        // Checks if the black player wants to move his own stone
        if (currentPlayer.getName().equalsIgnoreCase(BLACK_NAME)) {
            for (int i = 0; i < stonesLeft; i++) {
                if (blackStoneSlots.get(i).distance(e.getPoint()) < STONE_RADIUS) {
                    draggedColor = Color.BLACK;
                    dragPosition = e.getPoint();
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Handles the logic for selecting a node on the board.
     * Checks if the node has been clicked and gets it if so. Not necessary for the setting phase.
     *
     * @param e             The mouse event containing the click position.
     * @param currentPlayer The player who is currently taking their turn.
     */
    private void handleNodeSelection(MouseEvent e, PlayerEntity currentPlayer) {
        if (currentPlayer.getPhase() instanceof SettingPhase) {
            return;
        }

        for (Map.Entry<Integer, Point> entry : nodePositions.entrySet()) {
            int guiNodeId = entry.getKey();
            Point p = new Point(offsetX + entry.getValue().x, offsetY + entry.getValue().y);

            if (p.distance(e.getPoint()) < STONE_RADIUS) {
                Node node = BoardManager.getInstance().getNode(guiNodeId);

                // Valid node clicked
                if (node.isOccupied() && node.getStone().getOwner() == currentPlayer) {
                    fromNodeId = guiNodeId;
                    dragPosition = e.getPoint();
                    draggedColor = node.getStone().getOwner().getName().equals(WHITE_NAME)
                            ? Color.WHITE : Color.BLACK;
                }
                break;
            }
        }
    }

    /**
     * Attempts to execute a player move by checking if the mouse was released over a valid node.
     * If a valid node is found, it signals the player move.
     *
     * @param e The mouse event containing the release position.
     */
    private void tryExecutePlayerMove(MouseEvent e) {
        BoardManager boardManager = BoardManager.getInstance();
        GameManager gameManager = GameManager.getInstance();

        // Checks if a stone has been released over a node and gets it if so
        for (Map.Entry<Integer, Point> entry : nodePositions.entrySet()) {
            int guiNodeId = entry.getKey();
            Point p = new Point(offsetX + entry.getValue().x, offsetY + entry.getValue().y);

            if (p.distance(e.getPoint()) < STONE_RADIUS) {
                Node toNode = boardManager.getNode(guiNodeId);

                // Checks if the stone was played from outside the edge of the game board or not
                if (fromNodeId != null) {
                    fromNode = boardManager.getNode(fromNodeId);
                }
                PlayerEntity currentPlayer = gameManager.getCurrentPlayer();

                // Check if move is valid
                if (RuleManager.getInstance().isMoveValid(currentPlayer, fromNode, toNode)) {
                    signalPlayerMove(fromNode, toNode);
                    fromNode = null;
                    break;
                }
            }
        }
    }
    // endregion

    // region Move / Remove Handlers

    /**
     * Sets a listener that will be notified when a complete move (from and to node) is selected.
     *
     * @param listener A consumer that accepts a Node array of length 2: [fromNode, toNode].
     */
    public void setMoveListener(Consumer<Node[]> listener) {
        this.moveListener = listener;
    }

    /**
     * Signals that the player has selected a move.
     * This should be called once both mousePressed and mouseReleased have been processed.
     *
     * @param from The node the stone is moved from.
     * @param to   The node the stone is moved to.
     */
    public void signalPlayerMove(Node from, Node to) {
        if (moveListener != null) {
            repaint();
            moveListener.accept(new Node[]{from, to});
        } else {
            System.out.println("[ERROR] - No moveListener was registered");
        }
    }

    /**
     * Sets a listener that will be notified when a complete move (remove) is selected.
     *
     * @param listener A consumer that accepts a Node to remove its stone.
     */
    public void setRemoveListener(Consumer<Node> listener) {
        this.removeListener = listener;
    }

    /**
     * Changes the remove mode to a desired state. Can be used to activate or deactivate the remove mode.
     * When the remove mode is active, a player can remove a stone by clicking on it.
     *
     * @param toState The target state of the removeMode. <p>
     *                True: Remove mode is on. False: Remove mode is off.
     */
    public void setRemoveMode(boolean toState) {
        removeMode = toState;
    }

    /**
     * Returns the current state of the remove mode.
     *
     * @return {@code true} if the remove mode is active, {@code false} otherwise.
     */
    public boolean getRemoveMode() {
        return removeMode;
    }

    /**
     * When a mill is formed the player chooses one stone over the gui
     *
     * @param targetPoint - The Point(Stone) the Player wants to remove
     */
    public void signalPlayerRemove(Point targetPoint) {
        BoardManager boardManager = BoardManager.getInstance();

        for (Map.Entry<Integer, Point> entry : nodePositions.entrySet()) {
            int nodeId = entry.getKey();
            Point guiPoint = new Point(offsetX + entry.getValue().x, offsetY + entry.getValue().y);

            if (guiPoint.distance(targetPoint) < 15) {
                Node targetNode = boardManager.getNode(nodeId);

                if (removeListener != null) {
                    removeMode = false;
                    repaint();
                    removeListener.accept(targetNode);
                }

                return;
            }
        }
    }
    // endregion

    //region GUI Rendering

    /**
     * When GUI-Board is initialized, this method will be called automatically to draw the Lines, Nodes and Stones.
     * Can also be manually called by {@link #repaint()}
     *
     * @param g the {@code Graphics} object
     * @see #drawBoardFrame(Graphics2D)
     * @see #drawNodes(Graphics2D)
     * @see #drawPlacedStones(Graphics2D)
     * @see #drawStonePlaceholders(Graphics2D)
     * @see #drawRemainingStones(Graphics2D)
     * @see #drawDraggedStone(Graphics2D)
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        offsetX = (getWidth() - BOARD_SIZE) / 2;
        offsetY = (getHeight() - BOARD_SIZE) / 2;

        drawBoardFrame(g2);
        drawNodes(g2);
        drawPlacedStones(g2);
        drawStonePlaceholders(g2);
        drawRemainingStones(g2);
        drawDraggedStone(g2);
    }

    /**
     * Helper method for {@link #paintComponent(Graphics)} method.
     * <p>
     * Draws the frame of the board with lines and rectangles.
     *
     * @param g2 The Graphics2D object used for drawing.
     */
    private void drawBoardFrame(Graphics2D g2) {
        g2.setColor(Color.LIGHT_GRAY);
        g2.setStroke(new BasicStroke(2));

        g2.drawRect(offsetX, offsetY, 400, 400);
        g2.drawRect(offsetX + 50, offsetY + 50, 300, 300);
        g2.drawRect(offsetX + 100, offsetY + 100, 200, 200);
        g2.drawLine(offsetX + 200, offsetY, offsetX + 200, offsetY + 100);
        g2.drawLine(offsetX + 200, offsetY + 300, offsetX + 200, offsetY + 400);
        g2.drawLine(offsetX, offsetY + 200, offsetX + 100, offsetY + 200);
        g2.drawLine(offsetX + 300, offsetY + 200, offsetX + 400, offsetY + 200);
    }

    /**
     * Helper method for {@link #paintComponent(Graphics)} method.
     * <p>
     * Draws the nodes on the board based on their positions.
     *
     * @param g2 The Graphics2D object used for drawing.
     */
    private void drawNodes(Graphics2D g2) {
        for (Point p : nodePositions.values()) {
            int x = offsetX + p.x;
            int y = offsetY + p.y;
            g2.fillOval(x - 5, y - 5, 10, 10);
        }
    }

    /**
     * Helper method for {@link #paintComponent(Graphics)} method.
     * <p>
     * Draws the placed stones on the board, including their borders and filling colors.
     *
     * @param g2 The Graphics2D object used for drawing.
     */
    private void drawPlacedStones(Graphics2D g2) {
        for (Map.Entry<Integer, Point> entry : nodePositions.entrySet()) {
            int id = entry.getKey();
            Point p = new Point(offsetX + entry.getValue().x, offsetY + entry.getValue().y);

            Node node = BoardManager.getInstance().getNode(id);
            if (node != null && node.isOccupied()) {
                Stone stone = node.getStone();
                PlayerEntity owner = stone.getOwner();

                boolean isWhite = owner.getName().equalsIgnoreCase(WHITE_NAME);
                Color fillColor = isWhite ? Color.WHITE : Color.BLACK;

                // Border
                g2.setColor(BEIGE);
                g2.fillOval(p.x - STONE_RADIUS - 4, p.y - STONE_RADIUS - 4, (STONE_RADIUS + 4) * 2, (STONE_RADIUS + 4) * 2);

                // Filling
                g2.setColor(fillColor);
                g2.fillOval(p.x - STONE_RADIUS, p.y - STONE_RADIUS, STONE_RADIUS * 2, STONE_RADIUS * 2);
            }
        }
    }

    /**
     * Helper method for {@link #paintComponent(Graphics)} method.
     * <p>
     * Draws placeholders for stones that can be placed on the board.
     * These are small circles indicating where stones can be displayed.
     *
     * @param g2 The Graphics2D object used for drawing.
     */
    private void drawStonePlaceholders(Graphics2D g2) {
        int placeholderRadius = 5;
        g2.setColor(Color.WHITE);
        for (Point p : whiteStoneSlots) {
            g2.fillOval(p.x - placeholderRadius, p.y - placeholderRadius, placeholderRadius * 2, placeholderRadius * 2
            );
        }
        for (Point p : blackStoneSlots) {
            g2.fillOval(p.x - placeholderRadius, p.y - placeholderRadius, placeholderRadius * 2, placeholderRadius * 2
            );
        }
    }

    /**
     * Helper method for {@link #paintComponent(Graphics)} method.
     * <p>
     * Draws the remaining stones that can be placed by each player.
     * Displays how many stones are left for each player.
     *
     * @param g2 The Graphics2D object used for drawing.
     * @see #drawReserveStones(Graphics2D, int, List, Color)
     */
    private void drawRemainingStones(Graphics2D g2) {
        GameManager gameManager = GameManager.getInstance();
        PlayerEntity playerWhite = gameManager.getPlayerWhite();
        PlayerEntity playerBlack = gameManager.getPlayerBlack();

        if (playerWhite == null || playerBlack == null) {
            return;
        }

        int whiteLeft = 9 - playerWhite.getPlacedStones();
        int blackLeft = 9 - playerBlack.getPlacedStones();

        drawReserveStones(g2, whiteLeft, whiteStoneSlots, Color.WHITE);
        drawReserveStones(g2, blackLeft, blackStoneSlots, Color.BLACK);
    }

    /**
     * Helper method for the {@link #drawRemainingStones(Graphics2D)} method.
     * <p>
     * It draws the stones that are left for each player in their respective slots.
     *
     * @param g2         The Graphics2D object used for drawing.
     * @param stonesLeft The number of stones left for the player.
     * @param stoneSlots The list of points where the stones should be drawn.
     * @param fillColor  The color to fill the stones with (white or black).
     */
    private void drawReserveStones(Graphics2D g2, int stonesLeft, List<Point> stoneSlots, Color fillColor) {
        for (int i = 0; i < stonesLeft; i++) {
            Point p = stoneSlots.get(i);
            // Border
            g2.setColor(BEIGE);
            g2.fillOval(p.x - STONE_RADIUS - 3, p.y - STONE_RADIUS - 3, (STONE_RADIUS + 3) * 2, (STONE_RADIUS + 3) * 2);

            // Filling
            g2.setColor(fillColor);
            g2.fillOval(p.x - STONE_RADIUS, p.y - STONE_RADIUS, STONE_RADIUS * 2, STONE_RADIUS * 2);
        }
    }

    /**
     * Helper method for the {@link #paintComponent(Graphics)} method.
     * <p>
     * Draws the stone that is currently being dragged by the player.
     *
     * @param g2 The Graphics2D object used for drawing.
     */
    private void drawDraggedStone(Graphics2D g2) {
        if (draggedColor != null && dragPosition != null) {
            g2.setColor(BEIGE);
            g2.fillOval(dragPosition.x - STONE_RADIUS - 3, dragPosition.y - STONE_RADIUS - 3, (STONE_RADIUS + 3) * 2, (STONE_RADIUS + 3) * 2);

            g2.setColor(draggedColor);
            g2.fillOval(dragPosition.x - STONE_RADIUS, dragPosition.y - STONE_RADIUS, STONE_RADIUS * 2, STONE_RADIUS * 2);
        }
    }

    /**
     * Resets the state of the GUIBoard, clearing any selected nodes and removing the dragged stone.
     * This is useful for resetting the board after restarting a game.
     */
    public void resetState() {
        fromNodeId = null;
        fromNode = null;
        removeMode = false;
        dragPosition = null;
        draggedColor = null;
    }

    // endregion
}
