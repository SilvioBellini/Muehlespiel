package view;

import players.PlayerEntity;

import java.awt.*;

/**
 * This class is responsible for updating the GUI prompts based on the current game state.
 */
public class GUIPrompt {
    private static GUI guiManager = GUI.getInstance();

    /**
     * Updates the game prompt based on the current player's phase and name.
     * The color is set to yellow.
     *
     * @param currentPlayer The player whose turn it is.
     * @param whiteName     The name of the player playing with white pieces.
     */
    public static void updateGamePrompt(PlayerEntity currentPlayer, String whiteName) {
        String text = "";

        // Player text
        if (currentPlayer.getName().equalsIgnoreCase(whiteName)) {
            text += "Weiss: ";
        } else {
            text += "Schwarz: ";
        }

        // Phase text
        switch (currentPlayer.getPhase().getClass().getSimpleName()) {
            case "SettingPhase":
                text += "Stein setzen";
                break;

            case "MovingPhase":
                text += "Stein ziehen";
                break;

            case "JumpingPhase":
                text += "Stein bewegen (Sprung erlaubt)";
                break;

            case "GameOverPhase":
                break;

            default:
                break;
        }
        guiManager.updateStatus(text, new Color(255, 230, 0));
    }

    /**
     * Updated the prompt in the case an error occurs.
     * The color is set to red.
     *
     * @param text The error message to display.
     */
    public static void updateErrorPrompt(String text) {
        guiManager.updateStatus(text, new Color(255, 0, 0));
    }

    /**
     * Updates the prompt for removing a piece.
     * The color is set to blue.
     *
     * @param currentPlayer The player whose turn it is.
     * @param whiteName     The name of the player playing with white pieces.
     */
    public static void updateRemovePrompt(PlayerEntity currentPlayer, String whiteName) {
        String text = "";

        // player text
        if (currentPlayer.getName().equalsIgnoreCase(whiteName)) {
            text = "Mühle Weiss : Stein entfernen";
        } else {
            text = "Mühle Schwarz : Stein entfernen";
        }

        guiManager.updateStatus(text, new Color(0, 153, 255));
    }
}
