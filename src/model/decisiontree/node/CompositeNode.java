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
    private int localPointer;

    /**
     * Constructs a new composite node with an explicit parameter.
     *
     * @param nodeId the identifier of this node
     * @param nodeType the composite type (Fallback, Sequence, Parallel)
     * @param parameter additional parameter (e.g. M for Parallel), 0 if unused
     */
    public CompositeNode(Naming nodeId, CompositeType nodeType, int parameter) {
        super(nodeId, nodeType);
        this.localPointer = 0;
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
        this.localPointer = 0;
        this.parameter = 0;
    }

    @Override
    public void tick(GameContext context) {
        if (this.localPointer() == 0) {
            saveState(context, TickState.ENTRY);
        }

        TickState state =  this.getNodeType().behavior(context, this);

        if (state != TickState.ENTRY && state != TickState.STAND_BY) {
            this.saveState(context, state);
            resetPointer();
        } else if (state == TickState.STAND_BY){
            this.setLastState(TickState.STAND_BY);
        }
    }

    /**
     * Ticks the child at the current cursor position without advancing the cursor.
     *
     * @return the result corresponding to the tick
     */
    protected TickResult tickNextChild(GameContext context) {
        Node<?> child = getChildren().get(this.localPointer);
        child.tick(context);
        return new TickResult(child.getLastState(), child);
    }

    @Override
    public void advancePointer() {
        if (this.localPointer < getChildren().size()) {
            this.localPointer++;
        }
    }

    @Override
    public void resetPointer() {
        this.localPointer = 0;
    }

    @Override
    public boolean ticksCompleted() {
        return this.localPointer >= getChildren().size();
    }

    @Override
    public int localPointer() {
        return this.localPointer;
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
