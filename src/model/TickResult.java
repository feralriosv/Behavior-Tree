package model;

import model.node.Node;
import model.node.TickState;

/**
 * Represents the result of executing a single tick of a node in the decision tree.
 *
 * @author ubpst
 */
public record TickResult(TickState state, Node<?> node) {

    private static final String RESTULT_DISPLAY_FORMAT = "%s %s";

    @Override
    public String toString() {
        return RESTULT_DISPLAY_FORMAT.formatted(this.node.toString(), this.state);
    }
}
