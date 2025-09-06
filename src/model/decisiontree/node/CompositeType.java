package model.decisiontree.node;

import model.GameContext;
import model.decisiontree.TickResult;
import model.decisiontree.TickState;

import java.util.List;
import java.util.Objects;

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
        while (self.ticksUnCompleted()) {
            TickResult childResult = self.tickNextChild(context);

            if (context.wasActionExecuted()) {
                return TickState.WAITS_SUCCESS;
            }

            if (childResult.getState() == TickState.SUCCESS) {
                return TickState.SUCCESS;
            }

            self.advancePointer();
        }

        return TickState.FAILURE;
    }

    private static TickState runSequence(GameContext context, CompositeNode self) {
        int lastIndex = self.getChildren().size() - 1;

        while (self.ticksUnCompleted() && self.getLastState() != TickState.WAITS_SUCCESS && self.getLastState() != TickState.WAITS_FAILURE) {
            TickResult childResult = self.tickNextChild(context);

            if (context.wasActionExecuted()) {
                if (self.localPointer() == lastIndex && childResult.getState() == TickState.SUCCESS) {
                    return TickState.WAITS_SUCCESS;
                }

                if (childResult.getState() == TickState.FAILURE) {
                    return TickState.WAITS_FAILURE;
                }

                if (childResult.getState() == TickState.SUCCESS) {
                    self.advancePointer();
                    self.activateNextChild();
                }

                return TickState.ENTRY;
            }

            if (childResult.getState() == TickState.FAILURE) {
                return TickState.FAILURE;
            }

            self.advancePointer();
        }

        if (self.getLastState() == TickState.WAITS_FAILURE) {
            return TickState.FAILURE;
        } else {
            return TickState.SUCCESS;
        }
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
            TickResult childResult = self.tickNextChild(context);
            if (childResult.getState() == TickState.SUCCESS) {
                successes++;
            }

            if (context.wasActionExecuted()) {

                TickState state = childResult.getState();
                if (state == TickState.SUCCESS || state == TickState.FAILURE) {
                    self.advancePointer();
                }

                return TickState.ENTRY;
            }

            self.advancePointer();
        }

        return (successes >= self.getParameter()) ? TickState.SUCCESS : TickState.FAILURE;
    }

    /**
     * Checks whether a given {@link NodeType} corresponds to one of the composite types ({@link CompositeType}).
     *
     * @param nodeType the node type to check; returns {@code false} if {@code null}
     * @return {@code true} if the node type represents a composite, {@code false} otherwise
     */
    public static boolean isCompositeType(NodeType<?> nodeType) {
        for (CompositeType type : values()) {
            if (Objects.equals(type.label, nodeType.label())) {
                return true;
            }
        }
        return false;
    }
}
