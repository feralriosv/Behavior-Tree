package view.configuration;

import model.ladybug.LadyBug;
import model.board.GameBoard;
import model.decisiontree.DecisionTree;
import view.CommandExecuter;
import view.Keyword;
import view.Result;
import view.command.SetupKeyword;

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

    private final Configuration configuration;

    private SetupExecuter(CommandExecuter<?, ?> ioRessources, Class<K> keywordClass) {
        super(ioRessources, keywordClass);
        this.configuration = new Configuration();
        setModel(this);
    }

    /**
     * Configures the game by setting the game board and registering the ladybugs.
     *
     * @param gameBoard the game board to use for the setup
     * @param ladyBugs the list of ladybugs to register
     */
    public void configurate(GameBoard gameBoard, List<LadyBug> ladyBugs) {
        this.configuration.setGameBoard(gameBoard);
        this.configuration.setRegisteredBugs(ladyBugs);
    }

    /**
     * Configures the game by setting the decision trees.
     *
     * @param decisionTrees the list of decision trees to be used
     */
    public void configurate(List<DecisionTree> decisionTrees) {
        for (DecisionTree decisionTree : decisionTrees) {
            if (!decisionTree.isUnplayableTree()) {
                this.configuration.setTrees(decisionTrees);
            }
        }
    }

    /**
     * Returns the maximum number of decision trees allowed, which corresponds to the number of registered ladybugs.
     *
     * @return maximum number of decision trees allowed
     */
    public int getMaxTreesAllowed() {
        return this.configuration.getRegisteredBugs().size();
    }

    /**
     * Checks if the game board has been configured.
     *
     * @return true if the game board is set, false otherwise
     */
    public boolean isBoardConfigurated() {
        return this.configuration.getGameBoard() != null;
    }

    /**
     * Starts the setup. This method will block while waiting for an input. The setup will continue as long as the provided input
     * has not been accepted
     */
    @Override
    public void handleUserInput() {
        while (isRunning() && !this.configuration.isCompleted()) {
            super.handleUserInput();
        }
    }

    /**
     * Prints the current board (or other accumulated output) and then returns a failure Result.
     *
     * @param displayObjext  text representation of the board to display; ignored if null/empty
     * @param errorMessage the specific error message to display
     * @return a failure {@link Result} carrying the error message
     */
    public Result configFailure(String displayObjext, String errorMessage) {
        printOnDefault(displayObjext);
        return Result.error(errorMessage);
    }

    /**
     * Returns the current configuration managed by this setup executer.
     *
     * @return the configuration instance
     */
    public Configuration getConfiguration() {
        return this.configuration;
    }

    /**
     * Creates a new dialog instance only accepting input matching the provided regex.
     *
     * @param ioRessources another command executer to use its ressources when requesting input or printing errors
     *
     * @return a new setup instance
     */
    public static SetupExecuter<Configuration, SetupKeyword> createSetup(CommandExecuter<?, ?> ioRessources) {
        return new SetupExecuter<>(ioRessources, SetupKeyword.class);
    }
}
