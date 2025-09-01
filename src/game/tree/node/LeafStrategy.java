package game.tree.node;

import game.GameContext;
import game.TickState;

/**
 * Functional interface representing the execution strategy of a leaf node
 * (either action or condition) in a behavior tree.
 * <p>
 * Each leaf node evaluates itself in the given {@link GameContext} and produces
 * a {@link TickState} result.
 * </p>
 *
 * @author ubpst
 */
public interface LeafStrategy {

    /**
     * Executes the strategy of this leaf node.
     *
     * @param context the current game context in which the node is evaluated
     * @return the {@link TickState} resulting from this evaluation
     */
    TickState run(GameContext context);
}
