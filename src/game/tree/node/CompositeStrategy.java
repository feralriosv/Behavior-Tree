package game.tree.node;

import game.GameContext;
import game.TickState;

import java.util.List;


/**
 * Functional interface representing the execution strategy of a composite node in a behavior tree.
 * A composite strategy defines how child nodes are evaluated and combined into a resulting {@link TickState}.
 *
 * @author ubpst
 */
@FunctionalInterface
public interface CompositeStrategy {
    /**
     * Executes the composite strategy for a set of child nodes.
     *
     * @param context the current game context of the tick
     * @param children the child nodes of the composite
     * @param parameter an additional parameter for the strategy (e.g. M in Parallel), or 0 if unused
     * @return the resulting {@link TickState} after evaluating the children
     */
    TickState run(GameContext context, List<Node<?>> children, int parameter);
}
