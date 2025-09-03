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
    public void behavior(GameContext context, CompositeNode self) {
        this.strategy.run(context, self);
    }

    @Override
    public String label() {
        return this.label;
    }

    private static void runFallback(GameContext context, CompositeNode self) {
        List<Node<?>> children = self.getChildren();

        for (int i = 0; i < self.localPointer(); i++) {
            TickResult childResult = new TickResult(children.get(i).getLastState(), children.get(i));
            if (childResult.getState() == TickState.SUCCESS) {
                self.saveState(context, TickState.SUCCESS);
                return;
            }
        }

        while (!self.ticksCompleted()) {
            TickResult childResult = self.tickNextChild(context);

            if (childResult.getState() == TickState.SUCCESS) {
                self.saveState(context, TickState.SUCCESS);
                self.resetPointer();
                return;
            }

            if (context.wasActionExecuted()) {
                return;
            }

            self.advancePointer();
        }

        boolean anySuccess = false;
        for (Node<?> child : children) {
            TickState prev = child.getLastState();
            if (prev == TickState.SUCCESS) {
                anySuccess = true;
                break;
            }
        }

        TickState finalState = anySuccess ? TickState.SUCCESS : TickState.FAILURE;
        self.saveState(context, finalState);
        self.resetPointer();
    }

    private static void runSequence(GameContext context, CompositeNode self) {
        while (!self.ticksCompleted()) {
            TickResult childResult = self.tickNextChild(context);

            // sequence detecta fallo y reinicia
            if (childResult.getState() == TickState.FAILURE) {
                self.saveState(context, TickState.FAILURE);
                self.resetPointer();
                return;
            }

            if (context.wasActionExecuted()) {
                return;
            }

            self.advancePointer();
        }

        // si acaba iteracion y no hay fallo bien
        self.saveState(context, TickState.SUCCESS);
        self.resetPointer();
    }

    private static void runParallel(GameContext context, CompositeNode self) {
        List<Node<?>> children = self.getChildren();
        int successes = 0;

        for (int i = 0; i < self.localPointer(); i++) {
            TickResult childResult = new TickResult(children.get(i).getLastState(), children.get(i));

            if (childResult.getState() == TickState.SUCCESS) {
                successes++;
            }
        }

        while (!self.ticksCompleted()) {
            int remainingTicks = Math.max(0, children.size() - (self.localPointer() + 1));
            TickResult childResult = self.tickNextChild(context);

            if (childResult.getState() == TickState.SUCCESS) {
                successes++;
            }

            if (successes + remainingTicks < self.getParameter()) {
                self.saveState(context, TickState.FAILURE);
                self.resetPointer();
                return;
            }

            if (context.wasActionExecuted()) {
                self.advancePointer();
                return;
            }

            self.advancePointer();
        }

        if (successes >= self.getParameter()) {
            self.saveState(context, TickState.SUCCESS);
            self.resetPointer();
        }
    }
}
