package view;

import difficulties.EasyDifficulty;
import difficulties.HardDifficulty;
import difficulties.MediumDifficulty;

import javax.swing.*;
import java.awt.*;

/**
 * This class creates the Difficulty Screen JPanel for the game.
 * It allows users to select a difficulty level for singleplayer mode.
 */
public class DifficultyScreenPanel {
    private static final String SINGLEPLAYER_MODE = "singleplayer";
    private static final String BOARD_NAME = "Board";

    /**
     * Creates the Difficulty Screen JPanel.
     *
     * @return The difficulty screen panel.
     * @see #createButtonPanel(CardLayout, JPanel)
     */
    public static JPanel createDifficultyScreen(CardLayout cardLayout, JPanel cardPanel) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JPanel buttonPanel = createButtonPanel(cardLayout, cardPanel);

        // Adding all components
        panel.add(HomeScreenPanel.createHomeLogo());
        panel.add(HomeScreenPanel.createHomeImage());
        panel.add(buttonPanel);

        return panel;
    }

    /**
     * Helper method for {@link #createDifficultyScreen(CardLayout, JPanel)} method.
     * <p>
     * Creates the button panel for the Difficulty Screen.
     *
     * @param cardLayout The CardLayout used for switching panels.
     * @param cardPanel  The main panel containing all cards.
     * @return The button panel with difficulty selection buttons.
     * @throws IllegalStateException if an unexpected button index is encountered.
     */
    private static JPanel createButtonPanel(CardLayout cardLayout, JPanel cardPanel) {
        // Buttons
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);

        // Necessary positions settings
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0; // same row

        String[] buttonTexts = {"leicht", "mittel", "schwer", "zurück"};

        // Add actions
        for (int i = 0; i < buttonTexts.length; i++) {
            JButton button = new JButton(buttonTexts[i]);
            StyleComponents.styleButton(button);

            int buttonIndex = i;
            button.addActionListener(e -> {
                switch (buttonIndex) {
                    case 0:
                        GUI.mode = SINGLEPLAYER_MODE;
                        GUI.difficulty = new EasyDifficulty();
                        cardPanel.add(BoardScreenPanel.createBoardScreen(cardLayout, cardPanel, GUI.mode, GUI.difficulty), BOARD_NAME);
                        cardLayout.show(cardPanel, BOARD_NAME);
                        break;
                    case 1:
                        GUI.mode = SINGLEPLAYER_MODE;
                        GUI.difficulty = new MediumDifficulty();
                        cardPanel.add(BoardScreenPanel.createBoardScreen(cardLayout, cardPanel, GUI.mode, GUI.difficulty), BOARD_NAME);
                        cardLayout.show(cardPanel, BOARD_NAME);
                        break;
                    case 2:
                        GUI.mode = SINGLEPLAYER_MODE;
                        GUI.difficulty = new HardDifficulty();
                        cardPanel.add(BoardScreenPanel.createBoardScreen(cardLayout, cardPanel, GUI.mode, GUI.difficulty), BOARD_NAME);
                        cardLayout.show(cardPanel, BOARD_NAME);
                        break;
                    case 3:
                        cardLayout.show(cardPanel, "newGame");
                        break;
                    default:
                        throw new IllegalStateException("Unexpected button index: " + buttonIndex);
                }
            });

            // Position
            gbc.gridx = i;
            gbc.insets = new Insets(0, 0, 0, (i < buttonTexts.length - 1) ? 20 : 0);
            buttonPanel.add(button, gbc);
        }

        return buttonPanel;
    }
}
