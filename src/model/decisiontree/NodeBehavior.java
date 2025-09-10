package model.decisiontree;

import model.GameContext;

/**
 * Strategy interface for defining the execution behavior of decision tree nodes.
 *
 * @param <T> the concrete node type this behavior targets; must extend {@link Node}
 * @author ubpst
 */
@FunctionalInterface
public interface NodeBehavior<T extends Node<?>> {
    /**
     * Executes the behavior of the given node in the provided game context.
     *
     * @param context the current game context
     * @param self    the node being executed (needed by composite behaviors to traverse children)
     * @return the resulting {@link TickState} after execution
     */
    TickState run(GameContext context, T self);
}
