package view;

import boardLogic.GameManager;
import difficulties.DifficultyStrategy;
import players.CPUPlayer;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

/**
 * This class creates the board screen panel for the game, which includes the game board,
 * timer settings, and control buttons.
 */
public class BoardScreenPanel {
    public static JLabel gamePrompt;
    public static JLabel turnTime;
    public static GUIBoard guiBoard;

    /**
     * Creates the Board Screen JPanel
     *
     * @param cardLayout The CardLayout for switching between panels.
     * @param cardPanel  The main panel containing all cards.
     * @param mode       The game mode (e.g., "singleplayer", "multiplayer").
     * @param difficulty The difficulty strategy for the game.
     * @return The board screen panel.
     * @see #createTopPanel(String, DifficultyStrategy)
     * @see #createBoardPanel()
     * @see #createButtonPanel(CardLayout, JPanel)
     */
    public static JPanel createBoardScreen(CardLayout cardLayout, JPanel cardPanel, String mode, DifficultyStrategy difficulty) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(20, 35, 40));
        panel.setOpaque(false);

        // Adding all components
        panel.add(createTopPanel(mode, difficulty));
        panel.add(createBoardPanel());
        panel.add(createButtonPanel(cardLayout, cardPanel));
        panel.setName("Board");

        return panel;
    }

    /**
     * Helper method for the {@link #createBoardScreen(CardLayout, JPanel, String, DifficultyStrategy)} method.
     * <p>
     * Creates the top panel containing the timer settings and game prompt.
     *
     * @param mode       The game mode (e.g., "singleplayer", "multiplayer").
     * @param difficulty The difficulty strategy for the game.
     * @return The top panel with timer settings and game prompt.
     * @see #createSliderPanel(String, DifficultyStrategy)
     * @see #createTimePromptPanel()
     */
    private static JPanel createTopPanel(String mode, DifficultyStrategy difficulty) {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 0, 100));

        JPanel sliderPanel = createSliderPanel(mode, difficulty);
        topPanel.add(sliderPanel);
        JPanel promptPanel = createTimePromptPanel();
        topPanel.add(promptPanel);


        return topPanel;
    }

    /**
     * Helper method for the {@link #createTopPanel(String, DifficultyStrategy)} method.
     * <p>
     * Creates a panel with a slider for setting the time per turn and a button to start the game.
     *
     * @param mode       The game mode (e.g., "singleplayer", "multiplayer").
     * @param difficulty The difficulty strategy for the game.
     * @return The slider panel with time settings and start button.
     * @see #createStartButton(String, DifficultyStrategy)
     */
    private static JPanel createSliderPanel(String mode, DifficultyStrategy difficulty) {
        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.X_AXIS));
        sliderPanel.setOpaque(false);

        // Configure slider label
        JLabel sliderLabel = new JLabel("Bedenkzeit: 30s");
        sliderLabel.setForeground(GUI.TIMER_COLOR);
        sliderLabel.setFont(GUI.TIMER_FONT);
        sliderLabel.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 50));

        // Configure slider
        GUI.slider = new JSlider(10, 60, 30);
        GUI.slider.setMajorTickSpacing(10);
        GUI.slider.setPaintTicks(true);
        GUI.slider.setPaintLabels(true);
        GUI.slider.setOpaque(false);
        GUI.slider.setForeground(GUI.TIMER_COLOR);
        GUI.slider.setFont(GUI.TIMER_FONT);
        GUI.slider.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 50));

        GUI.slider.addChangeListener(e -> {
            sliderLabel.setText("Bedenkzeit: " + GUI.slider.getValue() + "s");
            turnTime.setText("Verbleibende Zeit: " + GUI.slider.getValue() + "s");
        });

        // Adding all components
        sliderPanel.add(sliderLabel);
        sliderPanel.add(GUI.slider);
        sliderPanel.add(createStartButton(mode, difficulty));

        return sliderPanel;
    }

    /**
     * Helper method for the {@link #createSliderPanel(String, DifficultyStrategy)} method.
     * <p>
     * Creates a button to start the game with the specified mode and difficulty.
     *
     * @param mode       The game mode (e.g., "singleplayer", "multiplayer").
     * @param difficulty The difficulty strategy for the game.
     * @return The button to start the game.
     */
    private static JButton createStartButton(String mode, DifficultyStrategy difficulty) {
        GameManager gameManager = GameManager.getInstance();

        JButton startButton = new JButton("Spiel starten");
        StyleComponents.styleButton(startButton);
        startButton.addActionListener(e -> {
            if (!gameManager.getGameStatus()) {
                gameManager.setTimePerTurn(GUI.slider.getValue());
                GUI.slider.setEnabled(false);
                gameManager.startGame(mode, difficulty);
            } else {
                System.out.println("[WARNING] - Game is already running!");
            }
        });

        return startButton;
    }

    /**
     * Helper method for the {@link #createTopPanel(String, DifficultyStrategy)} method.
     * <p>
     * Creates a panel with a prompt message and a label to display the remaining time.
     *
     * @return The panel with game prompt and remaining time label.
     */
    private static JPanel createTimePromptPanel() {
        JPanel promptTimePanel = new JPanel();
        promptTimePanel.setLayout(new BoxLayout(promptTimePanel, BoxLayout.X_AXIS));
        promptTimePanel.setOpaque(false);

        gamePrompt = new JLabel("Bedenkzeit einstellen und Spiel starten");
        gamePrompt.setForeground(GUI.TURN_TIMER_COLOR);
        gamePrompt.setFont(GUI.TURN_TIMER_FONT);
        gamePrompt.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 50));

        turnTime = new JLabel("Verbleibende Zeit: 30s");
        turnTime.setForeground(GUI.TURN_TIMER_COLOR);
        turnTime.setFont(GUI.TURN_TIMER_FONT);
        turnTime.setAlignmentX(Component.CENTER_ALIGNMENT);
        turnTime.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

        promptTimePanel.add(gamePrompt);
        promptTimePanel.add(Box.createHorizontalGlue());
        promptTimePanel.add(turnTime);

        return promptTimePanel;
    }

    /**
     * Helper method for the {@link #createBoardScreen(CardLayout, JPanel, String, DifficultyStrategy)} method.
     * <p>
     * Creates the board panel containing the game board.
     *
     * @return The board panel with the game board.
     */
    private static JPanel createBoardPanel() {
        JPanel boardPanel = new JPanel();
        boardPanel.setPreferredSize(new Dimension(900, 475));
        boardPanel.setMinimumSize(new Dimension(900, 475));
        boardPanel.setMaximumSize(new Dimension(900, 475));
        boardPanel.setOpaque(false);

        guiBoard = new GUIBoard();
        boardPanel.add(guiBoard, BorderLayout.CENTER);
        GameManager.getInstance().initializeListeners(guiBoard);

        return boardPanel;
    }

    /**
     * Helper method for the {@link #createBoardScreen(CardLayout, JPanel, String, DifficultyStrategy)} method.
     * <p>
     * Creates the button panel containing control buttons for the game.
     *
     * @param cardLayout The CardLayout for switching between panels.
     * @param cardPanel  The main panel containing all cards.
     * @return The button panel with control buttons.
     */
    private static JPanel createButtonPanel(CardLayout cardLayout, JPanel cardPanel) {
        GameManager gameManager = GameManager.getInstance();

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        // Necessary positions settings
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 5, 0, 5);

        String[] buttonTexts = {"undo", "redo", "speichern", "beenden"};

        // Add actions
        for (int i = 0; i < buttonTexts.length; i++) {
            JButton button = new JButton(buttonTexts[i]);
            StyleComponents.styleBoardButton(button);

            int buttonIndex = i;
            button.addActionListener(e -> {
                switch (buttonIndex) {
                    case 0:
                        if (!guiBoard.getRemoveMode() && !(gameManager.getCurrentPlayer() instanceof CPUPlayer)) {
                            gameManager.undoMove();
                        }
                        break;
                    case 1:
                        if (!guiBoard.getRemoveMode()) {
                            gameManager.redoMove();
                        }
                        break;
                    case 2:
                        if (!gameManager.getGameStatus()) {
                            break;
                        }

                        gameManager.stopTurnTimer();
                        JFileChooser chooser = new JFileChooser();
                        chooser.setDialogTitle("Spiel speichern");
                        chooser.setFileFilter(
                                new FileNameExtensionFilter("JSON-Dateien", "json"));
                        int toSave = chooser.showSaveDialog(null);

                        if (toSave == JFileChooser.APPROVE_OPTION) {
                            File selectedFile = chooser.getSelectedFile();
                            if (!selectedFile.getName().endsWith(".json")) {
                                selectedFile = new File(selectedFile.getAbsolutePath() + ".json");
                            }

                            gameManager.saveGame(selectedFile);
                        }

                        break;
                    case 3:
                        cardLayout.show(cardPanel, "Menu");
                        gameManager.resetGame();
                        break;
                    default:
                        break;
                }
            });

            // Position
            gbc.gridx = i % 6;
            gbc.gridy = 0;
            buttonPanel.add(button, gbc);
        }

        return buttonPanel;
    }
}
