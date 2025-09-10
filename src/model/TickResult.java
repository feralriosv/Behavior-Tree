package model;

import model.decisiontree.TickState;
import model.decisiontree.Node;

/**
 * Represents the result of executing a single tick of a node in the decision tree.
 *
 * @param state the {@link TickState} resulting from executing the tick
 * @param node  the {@link Node} on which the tick was executed
 * @author ubpst
 */
public record TickResult(TickState state, Node<?> node) {

    private static final String RESTULT_DISPLAY_FORMAT = "%s %s";

    @Override
    public String toString() {
        return RESTULT_DISPLAY_FORMAT.formatted(this.node.toString(), this.state);
    }
}
