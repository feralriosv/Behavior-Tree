package view;

import game.Game;
import game.GameClient;
import view.command.ModelKeyword;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;


/**
 * Command-line interface client for the Ladybug game.
 * <p>
 * Wraps a {@link CommandExecuter} to handle input, output, and user commands
 * from a {@link Scanner} source. Implements {@link GameClient} to integrate
 * with the game lifecycle and {@link AutoCloseable} for resource cleanup.
 * </p>
 *
 * @author ubpst
 */

public class CLILadybugClient implements GameClient, AutoCloseable {

    private final PrintStream defaultStream;
    private final CommandExecuter<Game, ?> executer;
    private final Scanner scanner;
    private boolean wasQuit = false;

    /**
     * Constructs a new CLI client for the Ladybug game.
     *
     * @param inputSource the input stream from which user commands are read
     * @param defaultOutputStream the stream for normal command output
     * @param errorStream the stream for error messages
     */
    public CLILadybugClient(InputStream inputSource, PrintStream defaultOutputStream, PrintStream errorStream) {
        this.scanner = new Scanner(inputSource);
        this.defaultStream = defaultOutputStream;
        this.executer = new CommandExecuter<>(this.scanner, defaultOutputStream, errorStream, ModelKeyword.class);
    }


    @Override
    public Game createGame() {
        return new GameFactory(this.executer).createGame();
    }

    @Override
    public void executeAction(Game game) {
        this.executer.setModel(game);
        this.executer.handleUserInput();
        this.wasQuit = !this.executer.isRunning();
    }

    @Override
    public boolean wasQuit() {
        return this.wasQuit;
    }

    @Override
    public void close() {
        this.scanner.close();
    }
}
