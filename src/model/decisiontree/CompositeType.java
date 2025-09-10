package model.decisiontree;

import model.GameContext;

import java.util.List;

/**
 * Defines the composite node types used in behavior trees.
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

    private final String symbol;
    private final String label;
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
        return this.strategy.run(context, self);
    }

    @Override
    public String label() {
        return this.label;
    }

    private static TickState runFallback(GameContext context, CompositeNode self) {
        if (self.getLastState() == TickState.WAITS_SUCCESS) {
            return TickState.SUCCESS;
        }

        while (self.ticksUnCompleted()) {
            TickState childState = self.tickNextChild(context);

            if (context.actionExecuted()) {
                if (childState == TickState.SUCCESS) {
                    return TickState.WAITS_SUCCESS;
                }

                if (childState == TickState.FAILURE) {
                    self.advancePointer();
                    self.activateNextChild();
                }

                return TickState.ENTRY;
            }

            if (childState == TickState.SUCCESS) {
                return TickState.SUCCESS;
            }

            self.advancePointer();
        }

        return TickState.FAILURE;
    }

    private static TickState runSequence(GameContext context, CompositeNode self) {
        if (self.getLastState() == TickState.WAITS_FAILURE) {
            return TickState.FAILURE;
        }

        while (self.ticksUnCompleted()) {
            TickState childState = self.tickNextChild(context);

            if (context.actionExecuted()) {
                if (childState == TickState.FAILURE) {
                    return TickState.WAITS_FAILURE;
                }

                if (childState == TickState.SUCCESS) {
                    self.advancePointer();
                    self.activateNextChild();
                }

                return TickState.ENTRY;
            }

            if (childState == TickState.FAILURE) {
                return TickState.FAILURE;
            }

            self.advancePointer();
        }

        return TickState.SUCCESS;
    }

    private static TickState runParallel(GameContext context, CompositeNode self) {
        List<Node<?>> children = self.getChildren();
        int successes = 0;

        for (int i = 0; i < self.localPointer(); i++) {
            if (children.get(i).getLastState() == TickState.SUCCESS) {
                successes++;
            }
        }

        while (self.ticksUnCompleted()) {
            TickState childState = self.tickNextChild(context);
            if (childState == TickState.SUCCESS) {
                successes++;
            }

            if (context.actionExecuted()) {
                if (childState == TickState.SUCCESS || childState == TickState.FAILURE) {
                    self.advancePointer();
                }

                return TickState.ENTRY;
            }

            self.advancePointer();
        }

        return (successes >= self.getParameter()) ? TickState.SUCCESS : TickState.FAILURE;
    }
}
