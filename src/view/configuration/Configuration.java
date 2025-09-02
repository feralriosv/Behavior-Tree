package view.configuration;

import model.board.GameBoard;
import model.ladybug.LadyBug;
import model.decisiontree.DecisionTree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Holds the configuration of a game before it starts.
 *
 * @author ubpst
 */
public class Configuration {
    private GameBoard gameBoard;
    private List<LadyBug> registeredBugs;
    private List<DecisionTree> trees;

    /**
     * Checks whether this configuration is complete.
     *
     * @return true if the configuration is complete, false otherwise
     */
    public GameBoard getGameBoard() {
        return this.gameBoard;
    }

    /**
     * Returns the list of registered ladybugs for this configuration.
     *
     * @return unmodifiable list of registered ladybugs
     */
    public List<LadyBug> getRegisteredBugs() {
        return Collections.unmodifiableList(this.registeredBugs);
    }

    /**
     * Returns the list of decision trees associated with this configuration.
     *
     * @return unmodifiable list of decision trees
     */
    public List<DecisionTree> getTrees() {
        return Collections.unmodifiableList(trees);
    }

    /**
     * Sets the game board for this configuration.
     *
     * @param gameBoard the game board to be used
     */
    protected void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

    /**
     * Returns the list of registered ladybugs for this configuration.
     *
     * @param registeredBugs list of ladybugs to register
     */
    protected void setRegisteredBugs(List<LadyBug> registeredBugs) {
        this.registeredBugs = new ArrayList<>(registeredBugs);
    }

    /**
     * Sets the list of decision trees for this configuration.
     * If more ladybugs are registered than trees available, the extra ladybugs are removed.
     *
     * @param trees the list of decision trees
     */
    protected void setTrees(List<DecisionTree> trees) {
        this.trees = Collections.unmodifiableList(trees);

        if (tooMuchLadyBugs()) {
            int toRemove = registeredBugs.size() - trees.size();
            for (int i = 0; i < toRemove; i++) {
                this.registeredBugs.removeLast();
            }
        }
    }

    /**
     * Standard Description.
     *
     * @return standard return.
     */
    protected boolean isCompleted() {
        return this.gameBoard != null && this.trees != null && this.registeredBugs != null;
    }

    private boolean tooMuchLadyBugs() {
        return this.trees.size() < this.registeredBugs.size();
    }
}
