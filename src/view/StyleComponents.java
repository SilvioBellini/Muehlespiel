package view;

import javax.swing.*;
import java.awt.*;

/**
 * This class is used to style different components in the GUI.
 * It provides methods to style buttons with specific colors, fonts, and hover effects.
 */
public class StyleComponents {
    /**
     * Styles a button for general purposes by changing its colors, font, opacity and border.
     * Also changes the behaviour on hovering over the button.
     *
     * @param button The button that should get styled.
     */
    public static void styleButton(JButton button) {
        // Style
        button.setPreferredSize(new Dimension(180, 50));
        button.setBackground(GUI.BACKGROUND_COLOR);
        button.setForeground(GUI.BUTTON_COLOR);
        button.setFont(GUI.BUTTON_FONT);
        button.setText(button.getText().toUpperCase());
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createLineBorder(GUI.BUTTON_COLOR, 3));
        button.setFocusPainted(false);

        // Hover
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(GUI.BUTTON_COLOR);
                button.setForeground(GUI.BACKGROUND_COLOR);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(GUI.BACKGROUND_COLOR);
                button.setForeground(GUI.BUTTON_COLOR);
            }
        });
    }

    /**
     * Styles a button for the board by changing its colors, font, opacity and border.
     * Also changes the behaviour on hovering over the button.
     *
     * @param button The button that should get styled.
     */
    public static void styleBoardButton(JButton button) {
        // Style
        button.setPreferredSize(new Dimension(130, 40));
        button.setMinimumSize(new Dimension(130, 40));
        button.setMaximumSize(new Dimension(130, 40));
        button.setBackground(GUI.BACKGROUND_COLOR);
        button.setForeground(GUI.BUTTON_COLOR);
        button.setFont(GUI.BUTTON_FONT);
        button.setText(button.getText().toUpperCase());
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createLineBorder(GUI.BUTTON_COLOR, 3));
        button.setFocusPainted(false);

        // Hover
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(GUI.BUTTON_COLOR);
                button.setForeground(GUI.BACKGROUND_COLOR);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(GUI.BACKGROUND_COLOR);
                button.setForeground(GUI.BUTTON_COLOR);
            }
        });
    }

    /**
     * Styles a button for the game over message by changing its colors, font, opacity and border.
     * Also changes the behaviour on hovering over the button.
     *
     * @param button The button that should get styled.
     */
    public static void styleGameOverMsgButton(JButton button) {
        // Style
        button.setPreferredSize(new Dimension(200, 50));
        button.setMinimumSize(new Dimension(200, 50));
        button.setMaximumSize(new Dimension(200, 50));
        button.setBackground(GUI.GAME_OVER_BUTTON_COLOR);
        button.setForeground(GUI.GAME_OVER_BACKGROUND_COLOR);
        button.setFont(GUI.BUTTON_FONT);
        button.setText(button.getText().toUpperCase());
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createLineBorder(GUI.GAME_OVER_BUTTON_COLOR, 3));
        button.setFocusPainted(false);

        // Hover
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(GUI.GAME_OVER_BACKGROUND_COLOR);
                button.setForeground(GUI.GAME_OVER_BUTTON_COLOR);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(GUI.GAME_OVER_BUTTON_COLOR);
                button.setForeground(GUI.GAME_OVER_BACKGROUND_COLOR);
            }
        });
    }
}
