package view;

import boardLogic.GameManager;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

/**
 * This class creates the menu screen panel for the game, which includes buttons
 * for starting a new game, loading a game, and returning to the home screen.
 */
public class MenuScreenPanel {
    private static final String BOARD_NAME = "Board";

    /**
     * Creates the Menu Screen JPanel
     *
     * @param cardLayout The CardLayout used for switching screens.
     * @param cardPanel  The JPanel that contains all the screens.
     * @return The menu screen panel.
     * @throws IllegalStateException if an unexpected button index is encountered.
     */
    public static JPanel createMenuScreen(CardLayout cardLayout, JPanel cardPanel) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        // Buttons
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);

        // Necessary positions settings
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0; // same row

        // Create Buttons
        String[] buttonTexts = {"neues spiel", "spiel laden", "zurück"};

        // Add actions
        for (int i = 0; i < buttonTexts.length; i++) {
            JButton button = new JButton(buttonTexts[i]);
            StyleComponents.styleButton(button);

            int buttonIndex = i;
            button.addActionListener(e -> {
                switch (buttonIndex) {
                    case 0:
                        cardLayout.show(cardPanel, "newGame");
                        break;
                    case 1:
                        JPanel newBoardPanel = BoardScreenPanel.createBoardScreen(cardLayout, cardPanel, "singleplayer", GUI.difficulty);
                        newBoardPanel.setName(BOARD_NAME);
                        cardPanel.add(newBoardPanel, BOARD_NAME);

                        JFileChooser fileChooser = new JFileChooser();
                        fileChooser.setDialogTitle("Spielstand laden");
                        fileChooser.setFileFilter(new FileNameExtensionFilter("JSON-Dateien", "json"));

                        int toLoad = fileChooser.showOpenDialog(null);
                        if (toLoad == JFileChooser.APPROVE_OPTION) {
                            File selectedFile = fileChooser.getSelectedFile();
                            cardLayout.show(cardPanel, BOARD_NAME);
                            GUI.slider.setEnabled(false);
                            GameManager.getInstance().loadGame(selectedFile);
                        }
                        break;
                    case 2:
                        cardLayout.show(cardPanel, "Home");
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

        // Adding all components
        panel.add(HomeScreenPanel.createHomeLogo());
        panel.add(HomeScreenPanel.createHomeImage());
        panel.add(buttonPanel);

        return panel;
    }

}
