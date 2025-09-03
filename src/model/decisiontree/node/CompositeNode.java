package model.decisiontree.node;

import model.GameContext;
import model.decisiontree.TickResult;
import model.decisiontree.TickState;

import java.util.Iterator;

/**
 * Node representing a composite type (Fallback, Sequence, Parallel) in a behavior trees.
 *
 * @author ubpst
 */
public class CompositeNode extends Node<CompositeType> implements LocalPointer {

    private final int parameter;
    private int localTickPointer;

    /**
     * Constructs a new composite node with an explicit parameter.
     *
     * @param nodeId the identifier of this node
     * @param nodeType the composite type (Fallback, Sequence, Parallel)
     * @param parameter additional parameter (e.g. M for Parallel), 0 if unused
     */
    public CompositeNode(Naming nodeId, CompositeType nodeType, int parameter) {
        super(nodeId, nodeType);
        this.localTickPointer = 0;
        this.parameter = parameter;
    }

    /**
     * Constructs a new composite node with no additional parameter.
     *
     * @param nodeId the identifier of this node
     * @param type   the composite type
     */
    public CompositeNode(Naming nodeId, CompositeType type) {
        super(nodeId, type);
        this.localTickPointer = 0;
        this.parameter = 0;
    }

    @Override
    public void tick(GameContext context) {
        if (localPointer() == 0) {
            logState(context, TickState.ENTRY);
        }

        this.getNodeType().behavior(context, this);
    }

    /**
     * Ticks the child at the current cursor position without advancing the cursor.
     *
     * @return the result corresponding to the tick
     */
    protected TickResult tickNextChild(GameContext context) {
        Node<?> child = getChildren().get(localTickPointer);

        child.tick(context);

        return new TickResult(child.getLastState(), child);
    }

    /**
     * Advances the tick pointer to the next child node, if any remain.
     */
    @Override
    public void advancePointer() {
        if (localTickPointer < getChildren().size()) {
            this.localTickPointer++;
        }
    }

    /**
     * Resets the tick pointer to the beginning.
     */
    @Override
    public void resetPointer() {
        this.localTickPointer = 0;
    }

    @Override
    public boolean ticksCompleted() {
        return this.localTickPointer >= getChildren().size();
    }

    @Override
    public int localPointer() {
        return this.localTickPointer;
    }

    /**
     * Returns the additional parameter associated with this composite node.
     *
     * @return the parameter value for this composite node
     */
    protected int getParameter() {
        return this.parameter;
    }

    @Override
    public Iterator<Node<?>> iterator() {
        return getChildren().iterator();
    }
}
