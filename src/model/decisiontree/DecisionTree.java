package model.decisiontree;

import model.GameContext;
import model.decisiontree.node.Node;
import model.ladybug.LadyBug;

import java.util.List;

/**
 * Represents a decision tree used to control the behavior of a {@link LadyBug}.
 *
 * @author ubpst
 */
public class DecisionTree {

    private boolean onTick;
    private final Node<?> rootNode;

    /**
     * Creates a new decision tree with the given root node.
     *
     * @param root the root node of this decision tree
     */
    public DecisionTree(Node<?> root) {
        this.onTick = false;
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
        this.onTick = true;
        context.beginTick(ladyBug);

        while (!context.wasActionExecuted()) {
            this.rootNode.tick(context);
        }

        this.onTick = false;
        return context.endTick();
    }
}
