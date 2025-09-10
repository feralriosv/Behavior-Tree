package model.node;

import model.GameContext;

/**
 * Strategy-bearing type for decision tree nodes.
 *
 * @param <T> the concrete node class this type applies to
 * @author ubpst
 */
public interface NodeType<T extends Node<?>> {

    /**
     * Executes the behavior associated with this node type for a concrete node instance.
     *
     * @param context the current game context
     * @param self    the node instance this behavior acts upon
     * @return the resulting {@link TickState} after execution
     */
    TickState behavior(GameContext context, T self);

    /**
     * Returns the human‑readable label for this node type (used for display/parsing).
     *
     * @return the string label.
     */
    String label();
}

