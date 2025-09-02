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
public class CompositeNode extends Node<CompositeType> {

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

    protected boolean localTicksCompleted() {
        return this.localTickPointer >= getChildren().size();
    }

    public int getLocalTickPointer() {
        return this.localTickPointer;
    }

    @Override
    public boolean ticksCompleted() {
        if (this.localTickPointer < getChildren().size()) {
            return false;
        }

        for (Node<?> child : getChildren()) {
            if (!child.ticksCompleted()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Ticks the child at the current cursor position without advancing the cursor.
     *
     * @return the result corresponding to the tick
     */
    protected TickResult tickNextChild(GameContext context) {
        Node<?> nextChild = getChildren().get(this.localTickPointer);
        nextChild.tick(context);
        return nextChild.getLastResult();
    }

    /**
     * Advances the tick pointer to the next child node, if any remain.
     */
    protected void advancePointer() {
        if (localTickPointer < getChildren().size()) {
            this.localTickPointer++;
        }
    }

    /**
     * Resets the tick pointer to the beginning.
     */
    protected void resetPointer() {
        this.localTickPointer = 0;
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
    public void tick(GameContext context) {
        if (getLastResult() == null) {
            TickResult entry = TickResult.entryResult(this);
            context.logResult(entry);
            setLastResult(entry);
        }

        TickState result = getNodeType().behavior(context, this);

        if (result != TickState.ENTRY) {
            TickResult done = new TickResult(result, this);
            context.logResult(done);
        }
    }

    @Override
    public Iterator<Node<?>> iterator() {
        return getChildren().iterator();
    }
}
