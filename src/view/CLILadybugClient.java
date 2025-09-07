package view;

import model.Game;
import model.GameClient;
import view.command.ModelKeyword;

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

    private final CommandExecuter<Game, ?> executer;
    private final Scanner scanner;
    private boolean wasQuit = false;

    /**
     * Constructs a new CLI client for the Ladybug game.
     *
     * @param ioRessources abstraction that provides the input source and output streams
     *                     used to read user commands and print messages
     */
    public CLILadybugClient(IORessources ioRessources) {
        this.scanner = ioRessources.inputSource();
        this.executer = new CommandExecuter<>(ioRessources, ModelKeyword.class);
    }


    @Override
    public Game createGame() {
        return new GameFactory(this.executer.getIoRessources()).createGame();
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
