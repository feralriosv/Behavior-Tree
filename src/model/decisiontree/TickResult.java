package model.decisiontree;

import model.decisiontree.node.CompositeNode;
import model.decisiontree.node.Node;

/**
 * Represents the result of executing a single tick of a node in the decision tree.
 *
 * @author ubpst
 */
public class TickResult {

    private static final String RESTULT_DISPLAY_FORMAT = "%s %s %s";

    private final Node<?> node;
    private final TickState state;

    /**
     * Creates a new tick result with the given state and node.
     *
     * @param state the resulting state after executing the node
     * @param node the node that was executed
     */
    public TickResult(TickState state, Node<?> node) {
        this.state = state;
        this.node = node;
    }

    public static TickResult entryResult(CompositeNode node) {
        return new TickResult(TickState.ENTRY, node);
    }

    public Node<?> getNode() {
        return node;
    }

    /**
     * Returns the resulting state of this tick.
     *
     * @return the tick state
     */
    public TickState getState() {
        return state;
    }

    @Override
    public String toString() {
        return RESTULT_DISPLAY_FORMAT.formatted(node.getNaming(), node.getNodeType().label(), this.state);
    }
}
