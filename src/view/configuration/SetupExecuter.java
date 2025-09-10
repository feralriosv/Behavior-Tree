package view.configuration;

import model.ladybug.LadyBug;
import model.board.GameBoard;
import model.decisiontree.DecisionTree;
import view.CommandExecuter;
import view.ConsoleIORessources;
import view.Keyword;
import view.Result;
import view.command.SetupKeyword;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The {@code SetupExecuter} is responsible for managing the setup phase of the game.
 * It configures the game board, registers ladybugs, and sets up decision trees.
 * This class extends {@link CommandExecuter} to handle user commands during setup
 * and ensures the configuration is complete before proceeding.
 *
 * @param <V> the type of value handled by this executer
 * @param <K> the keyword type used for setup commands
 *
 * @author ubpst
 */
public final class SetupExecuter<V, K extends Enum<K> & Keyword<SetupExecuter<V, ?>>> extends CommandExecuter<SetupExecuter<V, ?>, K> {

    private static final String BOARD_ERROR_MESSAGE = "cannot play with the loaded board";
    private static final String TREE_ERROR_MESSAGE = "there was a problem with the loaded tree";

    private GameBoard gameBoard;
    private List<LadyBug> registeredBugs;
    private List<DecisionTree> trees;

    private SetupExecuter(ConsoleIORessources ioRessources, Class<K> keywordClass) {
        super(ioRessources, keywordClass);
        setModel(this);
    }

    /**
     * Configures the game by setting the game board and registering the ladybugs.
     *
     * @param gameBoard the game board to use for the setup
     * @param ladyBugs the list of ladybugs to register
     */
    public void configurate(GameBoard gameBoard, List<LadyBug> ladyBugs) {
        this.gameBoard = gameBoard;
        this.registeredBugs = new ArrayList<>(ladyBugs);
    }

    /**
     * Configures the game by setting the decision trees.
     *
     * @param decisionTrees the list of decision trees to be used
     */
    public void configurate(List<DecisionTree> decisionTrees) {
        List<DecisionTree> validTrees = new ArrayList<>();
        for (DecisionTree tree : decisionTrees) {
            if (!tree.isUnplayableTree()) {
                validTrees.add(tree);
            }
        }

        this.trees = Collections.unmodifiableList(validTrees);

        if (this.registeredBugs != null && this.trees.size() < this.registeredBugs.size()) {
            this.registeredBugs = new ArrayList<>(this.registeredBugs.subList(0, this.trees.size()));
        }
    }

    /**
     * Builds and returns the current configuration using the stored parameters.
     *
     * @return a new {@link Configuration} assembled from the current setup state
     */
    public Configuration getConfig() {
        return new Configuration(this.gameBoard, this.registeredBugs, this.trees);
    }

    /**
     * Starts the setup. This method will block while waiting for an input. The setup will continue as long as the provided input
     * has not been accepted
     */
    @Override
    public void handleUserInput() {
        while (isRunning() && !isCompleted()) {
            super.handleUserInput();
        }
    }

    /**
     * Prints the current board (or other accumulated output) and then returns a failure Result.
     *
     * @param boardString  text representation of the board to display; ignored if null/empty
     * @return a failure {@link Result} carrying the error message
     */
    public Result boardError(String boardString) {
        printOnDefault(boardString);
        return Result.error(BOARD_ERROR_MESSAGE);
    }

    /**
     * Handles a failure related to loading or processing a decision tree during setup.
     *
     * @param treeBlock the textual representation of the problematic decision tree to display;
     *                  may be null or empty if unavailable
     * @return result
     */
    public Result treeError(String treeBlock) {
        printOnDefault(treeBlock);
        return Result.error(TREE_ERROR_MESSAGE);
    }

    /**
     * Creates a new dialog instance only accepting input matching the provided regex.
     *
     * @param ioRessources another command executer to use its ressources when requesting input or printing errors
     * @return a new setup instance
     */
    public static SetupExecuter<Configuration, SetupKeyword> createSetup(ConsoleIORessources ioRessources) {
        return new SetupExecuter<>(ioRessources, SetupKeyword.class);
    }

    /**
     * Returns the maximum number of decision trees allowed, which corresponds to the number of registered ladybugs.
     *
     * @return maximum number of decision trees allowed
     */
    public int getMaxTreesAllowed() {
        return this.registeredBugs.size();
    }

    /**
     * Checks if the game board has been configured.
     *
     * @return true if the game board is set, false otherwise
     */
    public boolean isBoardConfigurated() {
        return this.gameBoard != null;
    }

    private boolean isCompleted() {
        return this.gameBoard != null
                && this.registeredBugs != null
                && this.trees != null
                && this.trees.size() == this.registeredBugs.size();
    }
}
