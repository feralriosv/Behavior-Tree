package view.command;

import model.decisiontree.DecisionTree;
import view.Command;
import view.Result;
import view.configuration.Configuration;
import view.configuration.loader.LoadingException;
import view.configuration.SetupExecuter;
import view.configuration.loader.TreeLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A command that loads decision trees from one or more files.
 * It validates that the game board is already configured, ensures the number of trees
 * does not exceed the maximum allowed, and then parses the trees using {@link TreeLoader}.
 *
 * @author ubpst
 */
public class LoadTrees implements Command<SetupExecuter<Configuration, ?>> {

    private static final String ERROR_MISSING_BOARD_SETUP = "board set up is missing";
    private static final String ERROR_TOO_MUCH_TREES = "too much trees were inputed (max allowed %d)";
    private static final String ERROR_UNREADABLE_FILE = "an unreadable file was detected";

    private final List<Path> paths;

    /**
     * Creates a new {@code LoadTrees} command to load decision trees from the given file paths.
     *
     * @param paths a list of paths to the files containing decision tree definitions
     */
    public LoadTrees(List<Path> paths) {
        this.paths = Collections.unmodifiableList(paths);
    }

    @Override
    public Result execute(SetupExecuter<Configuration, ?> handle) {
        if (!handle.isBoardConfigurated()) {
            return Result.error(ERROR_MISSING_BOARD_SETUP);
        } else if (handle.getMaxTreesAllowed() == 0) {
            return Result.error("no lady bug registered");
        }

        List<List<String>> treeBlocks;

        try {
            treeBlocks = loadLineBlocks();
        } catch (LoadingException e) {
            return Result.error(e.getMessage());
        }

        int maxAllowedTrees = handle.getMaxTreesAllowed();
        if (treeBlocks.size() > maxAllowedTrees) {
            return Result.error(ERROR_TOO_MUCH_TREES.formatted(maxAllowedTrees));
        }

        List<DecisionTree> decisionTrees = new ArrayList<>();
        TreeLoader parser = new TreeLoader();

        for (List<String> block : treeBlocks) {
            DecisionTree loadedTree;

            try {
                loadedTree = parser.load(block);
            } catch (LoadingException e) {
                return Result.error(e.getMessage());
            }

            decisionTrees.add(loadedTree);
        }

        handle.configurate(decisionTrees);
        return Result.success(joinTreesOutput(treeBlocks));
    }

    private static String joinTreesOutput(List<List<String>> treeBlocks) {
        StringBuilder sb = new StringBuilder();
        for (List<String> block : treeBlocks) {
            if (!sb.isEmpty()) {
                sb.append(System.lineSeparator());
            }
            String blockOutput = String.join(System.lineSeparator(), block);
            sb.append(blockOutput);
        }
        return sb.toString();
    }

    private List<List<String>> loadLineBlocks() throws LoadingException {
        List<List<String>> pathLines = new ArrayList<>();
        for (Path path : paths) {
            List<String> currentLines;
            try {
                currentLines = Files.readAllLines(path);
            } catch (IOException e) {
                throw new LoadingException(ERROR_UNREADABLE_FILE);
            }
            pathLines.add(currentLines);
        }
        return pathLines;
    }
}
