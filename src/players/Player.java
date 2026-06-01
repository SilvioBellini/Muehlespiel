package players;

/**
 * This class represents a human player in the game.
 * It extends the PlayerEntity class and provides a constructor to set the player's name.
 */
public class Player extends PlayerEntity {
    /**
     * Constructor for the Player class. Sets the player's name.
     *
     * @param name The name of the player.
     */
    public Player(String name) {
        super(name);
    }
}
