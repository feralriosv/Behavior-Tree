package model;

import model.node.Node;
import model.node.TickState;

/**
 * Represents the outcome of executing a single tick on a {@link Node}.
 *
 * @param state the {@link TickState} resulting from executing the tick
 * @param node  the {@link Node} on which the tick was executed
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
