package view;

import javax.swing.*;
import java.awt.*;

/**
 * This class creates the Home Screen JPanel for the game.
 * It serves as the starting point for the game, allowing users to navigate to the menu.
 */
public class HomeScreenPanel {
    public static final String LOGO_PATH = "/resources/muehle_logo.png";
    public static final String BOARD_IMAGE_PATH = "/resources/muehle_board.png";

    /**
     * Creates the Home Screen JPanel.
     *
     * @param cardLayout The CardLayout used for switching panels.
     * @param cardPanel  The main panel containing all cards.
     * @return The home screen panel.
     * @see #createHomeLogo()
     * @see #createHomeImage()
     */
    public static JPanel createHomeScreen(CardLayout cardLayout, JPanel cardPanel) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        // Buttons
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);

        JButton startButton = new JButton("spiel starten");
        StyleComponents.styleButton(startButton);

        startButton.addActionListener(e -> {
            cardLayout.show(cardPanel, "Menu");
        });

        buttonPanel.add(startButton, new GridBagConstraints());

        // Adding all components
        panel.add(createHomeLogo());
        panel.add(createHomeImage());
        panel.add(buttonPanel);

        return panel;
    }

    /**
     * Creates a specific logo which is shown on multiple Screens.
     * This method is only used to avoid code duplicates.
     *
     * @return The created logo as a label.
     */
    public static JLabel createHomeLogo() {

        JLabel logo = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(HomeScreenPanel.class.getResource(LOGO_PATH));
            Image scaled = icon.getImage().getScaledInstance(450, 180, Image.SCALE_SMOOTH);
            logo.setIcon(new ImageIcon(scaled));
            logo.setAlignmentX(Component.CENTER_ALIGNMENT);
            logo.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));
        } catch (Exception e) {
            System.out.println("[ERROR] - Image not found: " + e.getMessage());
        }
        return logo;
    }

    /**
     * Creates a specific image which is shown on multiple Screens.
     * This method is only used to avoid code duplicates.
     *
     * @return The created image as a label.
     */
    public static JLabel createHomeImage() {

        JLabel image = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(HomeScreenPanel.class.getResource(BOARD_IMAGE_PATH));
            Image scaled = icon.getImage().getScaledInstance(350, 250, Image.SCALE_SMOOTH);
            image.setIcon(new ImageIcon(scaled));
            image.setAlignmentX(Component.CENTER_ALIGNMENT);
            image.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));
        } catch (Exception e) {
            System.out.println("[ERROR] - Image not found: " + e.getMessage());
        }
        return image;
    }
}
