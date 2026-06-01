package view;

import javax.swing.*;
import java.awt.*;

/**
 * This class creates the New Game Screen JPanel.
 * It contains buttons for singleplayer, multiplayer, and back to the main menu.
 */
public class NewGameScreenPanel {
    /**
     * Creates the New Game Screen JPanel.
     *
     * @param cardLayout The CardLayout used for switching screens.
     * @param cardPanel  The JPanel that contains all the screens.
     * @return A JPanel representing the New Game Screen.
     */
    public static JPanel createNewGameScreen(CardLayout cardLayout, JPanel cardPanel) {
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
     * Creates a button panel with buttons for singleplayer, multiplayer, and back to the main menu.
     *
     * @param cardLayout The CardLayout used for switching screens.
     * @param cardPanel  The JPanel that contains all the screens.
     * @return A JPanel containing the buttons.
     * @throws IllegalStateException if an unexpected button index is encountered.
     */
    private static JPanel createButtonPanel(CardLayout cardLayout, JPanel cardPanel) {
        // Buttons
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);

        // Necessary positions settings
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0; // same row

        String[] buttonTexts = {"einzelspieler", "mehrspieler", "zurück"};

        // Add actions
        for (int i = 0; i < buttonTexts.length; i++) {
            JButton button = new JButton(buttonTexts[i]);
            StyleComponents.styleButton(button);

            int buttonIndex = i;
            button.addActionListener(e -> {
                switch (buttonIndex) {
                    case 0:
                        cardLayout.show(cardPanel, "Difficulty");
                        break;
                    case 1:
                        GUI.mode = "multiplayer";
                        GUI.difficulty = null;
                        cardPanel.add(BoardScreenPanel.createBoardScreen(cardLayout, cardPanel, GUI.mode, GUI.difficulty), "Board");
                        cardLayout.show(cardPanel, "Board");
                        break;
                    case 2:
                        cardLayout.show(cardPanel, "Menu");
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
