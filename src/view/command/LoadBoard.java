package view.command;


import model.ladybug.LadyBug;
import model.board.GameBoard;
import view.Command;
import view.Result;
import view.configuration.Configuration;
import view.configuration.SetupExecuter;
import view.configuration.loader.BoardLoader;
import view.configuration.loader.BugsLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.StringJoiner;


/**
 * A command that loads a game board and ladybugs configuration from a file.
 * It uses {@link BoardLoader} to initialize the game board and {@link BugsLoader}
 * to initialize the ladybugs from the file contents.
 *
 * @author ubpst
 */
public class LoadBoard implements Command<SetupExecuter<Configuration, ?>> {

    private static final String ERROR_UNREADABLE_FILE = "unreadable file uploaded";

    private final BoardLoader boardLoader;
    private final BugsLoader bugsLoader;
    private final Path path;

    /**
     * Creates a new {@code LoadBoard} command that loads a configuration from the given file path.
     *
     * @param path the path to the file containing the board and ladybug definitions
     */
    public LoadBoard(Path path) {
        this.path = path;
        this.boardLoader = new BoardLoader();
        this.bugsLoader = new BugsLoader();
    }

    @Override
    public Result execute(SetupExecuter<Configuration, ?> handle) {
        List<String> fileLines;

        try {
            fileLines = Files.readAllLines(path);
        } catch (IOException e) {
            return Result.error(ERROR_UNREADABLE_FILE);
        }

        List<LadyBug> ladyBugs = this.bugsLoader.load(fileLines);
        GameBoard gameBoard = this.boardLoader.load(fileLines);

        if (gameBoard.isEmptyBoard() || ladyBugs.isEmpty()) {
            return handle.boardError(getBoardLines(fileLines));
        }

        handle.configurate(gameBoard, ladyBugs);
        return Result.success(getBoardLines(fileLines));
    }

    private String getBoardLines(List<String> lines) {
        StringJoiner joiner = new StringJoiner(System.lineSeparator());
        for (String line : lines) {
            joiner.add(line);
        }
        return joiner.toString();
    }
}
