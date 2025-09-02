package model.decisiontree.node;

import model.GameContext;
import model.decisiontree.TickResult;
import model.decisiontree.TickState;

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
public enum CompositeType implements NodeType<CompositeNode> {
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
    private final NodeBehavior<CompositeNode> strategy;

    CompositeType(String symbol, String label, NodeBehavior<CompositeNode> strategy) {
        this.symbol = symbol;
        this.label  = label;
        this.strategy = strategy;
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
    public TickState behavior(GameContext context, CompositeNode self) {
        return strategy.run(context, self);
    }

    @Override
    public String label() {
        return this.label;
    }

    private static TickState runFallback(GameContext context, CompositeNode self) {
        boolean childSucceeded = false;

        while (!self.localTicksCompleted()) {
            TickResult result = self.tickNextChild(context);

            if (result.getState() == TickState.SUCCESS) {
                childSucceeded = true;
            }

            if (context.wasActionExecuted()) {
                return TickState.ENTRY;
            }

            self.advancePointer();
        }

        self.resetPointer();
        return childSucceeded ? TickState.SUCCESS : TickState.FAILURE;
    }

    private static TickState runSequence(GameContext context, CompositeNode self) {
        while (!self.localTicksCompleted()) {
            TickResult result = self.tickNextChild(context);

            if (result.getState() == TickState.FAILURE) {
                self.resetPointer();
                return TickState.FAILURE;
            }

            if (context.wasActionExecuted()) {
                return TickState.ENTRY;
            }

            self.advancePointer();
        }

        self.resetPointer();
        return TickState.SUCCESS;
    }

    private static TickState runParallel(GameContext context, CompositeNode self) {
        List<Node<?>> children = self.getChildren();
        int successes = 0;

        while (!self.localTicksCompleted()) {
            int remainingTicks = children.size() - self.getLocalTickPointer();
            TickResult result = self.tickNextChild(context);

            if (result.getState() == TickState.SUCCESS) {
                successes++;

                if (self.ticksCompleted() && successes >= self.getParameter()) {
                    self.resetPointer();
                    return TickState.SUCCESS;
                }
            }

            if (successes + (remainingTicks - 1) < self.getParameter()) {
                self.resetPointer();
                return TickState.FAILURE;
            }

            if (context.wasActionExecuted()) {
                self.advancePointer();
                return TickState.ENTRY;
            }

            self.advancePointer();
        }

        TickState finalState = (successes >= self.getParameter()) ? TickState.SUCCESS : TickState.FAILURE;
        self.resetPointer();
        return finalState;
    }

    /**
     * Checks whether the given label corresponds to a defined composite type.
     *
     * @param label the label string to check
     * @return true if the label matches a composite type, false otherwise
     */
    public static boolean isCompositeType(NodeType<?> label) {
        for (CompositeType type : values()) {
            if (type.label().equals(label.label()) || type.getSymbol().equals(label.label())) {
                return true;
            }
        }
        return false;
    }
}
