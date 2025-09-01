package game.tree.node;

import game.GameContext;
import game.TickState;

import java.util.List;

/**
 * Enumeration of composite node types in a behavior tree.
 * <p>
 * A composite node manages multiple children and determines its result
 * based on their evaluation strategy: fallback, sequence, or parallel.
 * </p>
 *
 * @author ubpst
 */
public enum CompositeType implements NodeType {
    /**
     * Fallback composite: evaluates children in order until one succeeds.
     * Returns SUCCESS on the first child success, FAILURE if all fail, or ENTRY if evaluation should pause.
     */
    FALLBACK("?", "fallback", CompositeType::runFallback),

    /**
     * Sequence composite: evaluates children in order until one fails.
     * Returns FAILURE on the first child failure, SUCCESS if all succeed,or ENTRY if evaluation should pause.
     */
    SEQUENCE("->", "sequence", CompositeType::runSequence),

    /**
     * Parallel composite: evaluates all children, counting successes.
     * Returns FAILURE if fewer than M successes are possible, or ENTRY if evaluation should pause.
     */
    PARALLEL("=M>", "parallel", CompositeType::runParallel);

    private final String label;
    private final String symbol;
    private final CompositeStrategy strategy;

    CompositeType(String symbol, String label, CompositeStrategy strategy) {
        this.symbol = symbol;
        this.label  = label;
        this.strategy = strategy;
    }

    /**
     * Runs this composite's strategy on the given children.
     *
     * @param context the game context
     * @param children the list of child nodes
     * @param parameter the parameter for parallel nodes (ignored otherwise)
     * @return the tick state resulting from evaluation
     */
    public TickState runStrategy(GameContext context, List<Node<?>> children, int parameter) {
        return strategy.run(context, children, parameter);
    }

    /**
     * Returns the symbol associated with this composite type.
     *
     * @return the symbol string
     */
    public String getSymbol() {
        return this.symbol;
    }

    @Override
    public String displayLabel() {
        return this.label;
    }

    private static TickState runFallback(GameContext gameContext, List<Node<?>> children, int parameter) {
        for (Node<?> node : children) {
            TickState state = node.tick(gameContext).getState();
            if (state == TickState.SUCCESS) {
                return TickState.SUCCESS;
            }
            if (gameContext.shouldStop()) {
                return TickState.ENTRY;
            }
        }
        return TickState.FAILURE;
    }

    private static TickState runSequence(GameContext gameContext, List<Node<?>> children, int parameter) {
        for (Node<?> node : children) {
            TickState tickState = node.tick(gameContext).getState();
            if (tickState == TickState.FAILURE) {
                return TickState.FAILURE;
            }
            if (gameContext.shouldStop()) {
                return TickState.ENTRY;
            }
        }
        return TickState.SUCCESS;
    }

    private static TickState runParallel(GameContext context, List<Node<?>> children, int parameter) {
        int successes = 0;
        int remaining = children.size();

        for (Node<?> node : children) {
            TickState state = node.tick(context).getState();
            remaining--;

            if (state == TickState.SUCCESS) {
                successes++;
                if (successes >= parameter) {
                    return TickState.SUCCESS;
                }
            }

            if (successes + remaining < parameter) {
                return TickState.FAILURE;
            }

            if (context.shouldStop()) {
                return TickState.ENTRY;
            }
        }

        return successes >= parameter ? TickState.SUCCESS : TickState.FAILURE;
    }
}
