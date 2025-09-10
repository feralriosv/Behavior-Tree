package model;

/**
 * Implements the procedure of the game. The procedure is designed to work event-based.
 * See {@link GameClient} for more information about those events.
 *
 * @author Programmieren-Team
 * @author ubpst
 */
public class GameRunner {

    private final GameClient client;

    /**
     * Creates a new runner for a given client.
     * @param client the client for the game.
     */
    public GameRunner(GameClient client) {
        this.client = client;
    }

    /**
     * Starts the game procedure. The game will run and call {@link GameClient events} accordingly.
     *
     * <p>If the provided {@link Game} instance of the {@link GameClient#createGame()} event was {@code null}, the procedure will return
     * immediately.</p>
     *
     * <p>This method blocks the current thread until the game is finished. This is the case if and only if {@link Game#isFinished()} on
     * the provided instance, or the {@link GameClient#wasQuit()} event returned {@code true}. In those cases, the
     * {@link GameClient#onFinish(Game)} event is called immediately before returning.</p>
     */
    public void start() {
        Game game = this.client.createGame();
        if (game == null) {
            return;
        }

        while (!client.wasQuit()) {
            this.client.executeAction(game);
        }
    }
}
