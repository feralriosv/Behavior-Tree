package game;

/**
 * Provides the functionality for game clients.
 *
 * @author ubpst
 * @author Programmieren-Team
 */
public interface GameClient {
    /**
     * Creates the game.
     * @return the game to be created.
     */
    Game createGame();

    /**
     * Executes a players action on a given game instance.
     * @param game the game in question.
     */
    void executeAction(Game game);

    /**
     * Registers if the game was ended forcefully or not.
     * @return true if it was ended, false if not.
     */
    boolean wasQuit();
}
