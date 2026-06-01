package view;

import boardLogic.BoardManager;
import boardLogic.GameManager;
import difficulties.DifficultyStrategy;
import players.PlayerEntity;

import javax.swing.*;
import java.awt.*;

/**
 * This class represents the main GUI of the game.
 * It initializes the main window, menu bar, and different screens
 * and uses the GUIBoard class to display the game board.
 */
public class GUI implements Observer {
    // region Fields
    // Name constants
    private static final String BOARD_SCREEN_NAME = "Board";
    private static final String HOME_SCREEN_NAME = "Home";
    private static final String MENU_SCREEN_NAME = "Menu";
    private static final String NEW_GAME_SCREEN_NAME = "newGame";
    private static final String DIFFICULTY_SCREEN_NAME = "Difficulty";
    private static final String WINNER = "Sieger: ";
    private static final String DEFAULT_FONT = "Arial";

    // GUI Components
    private JFrame window;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JMenuBar menuBar;
    public static JSlider slider;

    private static GUI instance;

    // Color-Picker for RGB Values : rgb(0, 153, 255)
    public static final Color BACKGROUND_COLOR = new Color(17, 35, 38);
    public static final Color BUTTON_COLOR = new Color(250, 240, 220);
    public static final Color TIMER_COLOR = new Color(250, 240, 220);
    public static final Color TURN_TIMER_COLOR = new Color(255, 230, 0);
    public static final Color GAME_OVER_BACKGROUND_COLOR = new Color(47, 47, 47);
    public static final Color GAME_OVER_BUTTON_COLOR = new Color(0, 255, 0);

    // Fonts
    public static final Font BUTTON_FONT = new Font(DEFAULT_FONT, Font.BOLD, 20);
    public static final Font TIMER_FONT = new Font(DEFAULT_FONT, Font.BOLD, 16);
    public static final Font TURN_TIMER_FONT = new Font(DEFAULT_FONT, Font.BOLD, 20);

    // Paths to resources
    public static final String DRAW_GIF_PATH = "/resources/draw.gif";
    public static final String WIN_GIF_PATH = "/resources/win.gif";

    // Game logic
    public static String mode;
    public static DifficultyStrategy difficulty;
    // endregion

    // region Initialization

    /**
     * Initializes the GUI.
     * Creates the window, main panel and all different screens.
     */
    public GUI() {
        // Create window
        window = new JFrame("Mühle");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(900, 750);
        window.setResizable(false);
        window.setLocationRelativeTo(null);
        window.setBackground(BACKGROUND_COLOR);

        // Create menu bar
        menuBar = new JMenuBar();
        menuBar.add(view.MenuBar.createHelpMenu(window));
        menuBar.add(MenuBar.createCreditsMenu(window));

        // Main panel GUI
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout); // Cardlayout for multiple screens
        cardPanel.setBackground(BACKGROUND_COLOR);

        // Screens
        JPanel homeScreen = HomeScreenPanel.createHomeScreen(cardLayout, cardPanel);
        JPanel menuScreen = MenuScreenPanel.createMenuScreen(cardLayout, cardPanel);
        JPanel newGameScreen = NewGameScreenPanel.createNewGameScreen(cardLayout, cardPanel);
        JPanel difficultyScreen = DifficultyScreenPanel.createDifficultyScreen(cardLayout, cardPanel);

        // Adding all components
        cardPanel.add(homeScreen, HOME_SCREEN_NAME);
        cardPanel.add(menuScreen, MENU_SCREEN_NAME);
        cardPanel.add(newGameScreen, NEW_GAME_SCREEN_NAME);
        cardPanel.add(difficultyScreen, DIFFICULTY_SCREEN_NAME);

        window.setJMenuBar(menuBar);
        window.add(cardPanel);
        window.setVisible(true);

        BoardManager.getInstance().addObserver(this);
    }

    /**
     * Creates a new Instance of this Manager, if there is none already.
     *
     * @return Instance if one already existed, creates a new one otherwise.
     */
    public static GUI getInstance() {
        if (instance == null) {
            instance = new GUI();
        }
        return instance;
    }
    // endregion

    // region Events

    /**
     * Updates the text displaying the remaining time to perform a move.
     *
     * @param timeLeft The value that should get displayed.
     */
    public void updateTimer(int timeLeft) {
        BoardScreenPanel.turnTime.setText("Verbleibende Zeit: " + timeLeft + "s");
    }

    /**
     * Updates the text displaying the current game status.
     *
     * @param text  The text that should get displayed.
     * @param color The color of the text.
     */
    public void updateStatus(String text, Color color) {
        BoardScreenPanel.gamePrompt.setText(text);
        BoardScreenPanel.gamePrompt.setForeground(color);
    }

    /**
     * Displays a dialog with the game over message, winner and loser information.
     *
     * @param winner The player who won the game.
     */
    public void showGameOverMsg(PlayerEntity winner) {
        GameManager gameManager = GameManager.getInstance();

        // Create a dialog
        JDialog dialog = new JDialog();
        dialog.setTitle("Game Over");
        dialog.setModal(true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(600, 600);
        dialog.setLocationRelativeTo(null);
        dialog.setResizable(false);

        // Create main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(GAME_OVER_BACKGROUND_COLOR);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Create components
        JLabel topText = new JLabel("GAME OVER", SwingConstants.CENTER);
        topText.setFont(new Font(DEFAULT_FONT, Font.BOLD, 70));
        topText.setForeground(Color.WHITE);
        topText.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel gifLabel = new JLabel();
        gifLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        gifLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gifLabel.setPreferredSize(new Dimension(250, 250));
        gifLabel.setMaximumSize(new Dimension(250, 250));
        gifLabel.setMinimumSize(new Dimension(250, 250));

        JLabel winnerText = new JLabel();
        winnerText.setFont(new Font(DEFAULT_FONT, Font.BOLD, 50));
        winnerText.setForeground(Color.WHITE);
        winnerText.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel infoText = new JLabel();
        infoText.setFont(new Font(DEFAULT_FONT, Font.BOLD, 20));
        infoText.setForeground(Color.WHITE);
        infoText.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        infoText.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoText.setHorizontalAlignment(SwingConstants.CENTER);

        // Create button
        JButton newGameButton = new JButton("Neues Spiel");
        newGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        StyleComponents.styleGameOverMsgButton(newGameButton);

        newGameButton.addActionListener(e -> {
            dialog.dispose();
            gameManager.resetGame();
            cardLayout.show(cardPanel, BOARD_SCREEN_NAME);
        });

        // Load GIFs
        Image winGif = (new ImageIcon(getClass().getResource(WIN_GIF_PATH))).getImage().getScaledInstance(250, 250,
                Image.SCALE_DEFAULT);
        Image drawGif = (new ImageIcon(getClass().getResource(DRAW_GIF_PATH))).getImage().getScaledInstance(250, 250,
                Image.SCALE_DEFAULT);

        // Set winner and loser text based on the winner
        String winnerOutput = "";
        String loserOutput = "";
        if (winner != null) {
            if (winner.getName().equalsIgnoreCase("white")) {
                winnerOutput = "Weiss";
                loserOutput = "Schwarz";
            } else {
                winnerOutput = "Schwarz";
                loserOutput = "Weiss";
            }
        }

        // Set the GIF and text based on the game outcome
        switch (gameManager.lossCause) {
            case GameManager.LossCause.STONECOUNT:
                gifLabel.setIcon(new ImageIcon(winGif));
                winnerText.setText(WINNER + winnerOutput);
                infoText.setText(loserOutput + " hat weniger als 3 Steine");
                break;
            case GameManager.LossCause.BLOCKED:
                gifLabel.setIcon(new ImageIcon(winGif));
                winnerText.setText(WINNER + winnerOutput);
                infoText.setText(loserOutput + " wurde blockiert. Keine Züge mehr möglich");
                break;
            case GameManager.LossCause.STALEMATE:
                gifLabel.setIcon(new ImageIcon(drawGif));
                winnerText.setText("Unentschieden");
                infoText.setText("Während 20 Zügen wurde keine Mühle gebildet");
                break;
            case GameManager.LossCause.TIMEOUT:
                gifLabel.setIcon(new ImageIcon(winGif));
                winnerText.setText(WINNER + winnerOutput);
                infoText.setText("Spielzeit von " + loserOutput + " abgelaufen");
                break;
            default:
                throw new IllegalStateException("Unexpected loss cause: " + gameManager.lossCause);
        }

        // Add all components
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(topText);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(gifLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(winnerText);
        mainPanel.add(infoText);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(newGameButton);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    /**
     * This method gets called, whenever the board gets updated (by its observable).
     * Repaints the GUI.
     */
    @Override
    public void update() {
        BoardScreenPanel.guiBoard.repaint();
    }

    /**
     * Resets the game state and the GUI components to their initial state.
     * This method is called when a new game starts or when the user wants to reset the current game.
     *
     * @throws IllegalStateException if the GUIBoard cannot be found in the current board panel.
     */
    public void reset() {
        slider.setValue(30);
        slider.setEnabled(true);

        // Remove the current board panel
        Component[] components = cardPanel.getComponents();
        for (Component comp : components) {
            if (comp.getName() != null && comp.getName().equals(BOARD_SCREEN_NAME)) {
                cardPanel.remove(comp);
                break;
            }
        }

        // Create a new board panel
        JPanel newBoardPanel = BoardScreenPanel.createBoardScreen(cardLayout, cardPanel, mode, difficulty);
        newBoardPanel.setName(BOARD_SCREEN_NAME);
        cardPanel.add(newBoardPanel, BOARD_SCREEN_NAME);

        GUIBoard newGuiBoard = findGuiBoardIn(newBoardPanel);
        if (newGuiBoard != null) {
            newGuiBoard.resetState();
            GameManager.getInstance().initializeListeners(newGuiBoard);
        } else {
            throw new IllegalStateException("[ERROR] - GUIBoard could not be found in the BoardScreen!");
        }
    }

    private GUIBoard findGuiBoardIn(Container container) {
        for (Component c : container.getComponents()) {
            if (c instanceof GUIBoard)
                return (GUIBoard) c;
            if (c instanceof Container) {
                GUIBoard found = findGuiBoardIn((Container) c);
                if (found != null)
                    return found;
            }
        }
        return null;
    }
    // endregion

    public static void main(String[] args) {
        GameManager.getInstance();
    }
}
