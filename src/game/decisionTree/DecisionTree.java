package game.decisionTree;

import game.GameContext;
import game.LadyBug;

import java.util.List;

/**
 * Represents a decision tree used to control the behavior of a {@link LadyBug}.
 *
 * @author ubpst
 */
public class DecisionTree {
    private final Node<?> rootNode;

    /**
     * Creates a new decision tree with the given root node.
     *
     * @param root the root node of this decision tree
     */
    public DecisionTree(Node<?> root) {
        this.rootNode = root;
    }

    /**
     * Executes a tick of the decision tree for the given ladybug in the specified game context.
     *
     * @param context the game context in which this tick is executed
     * @param ladyBug the ladybug whose behavior is determined by the decision tree
     * @return a list of {@link TickResult} representing the outcomes of the tick
     */
    public List<TickResult> tick(GameContext context, LadyBug ladyBug) {
        context.beginTick(ladyBug);
        this.rootNode.tick(context);
        return context.endTick();
    }
}
