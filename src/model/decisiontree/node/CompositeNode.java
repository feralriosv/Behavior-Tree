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
        super.tick(context);

        if (this.getLastState() != TickState.ENTRY && !waitsResult()) {
            saveState(context, TickState.ENTRY);
            this.setLastState(TickState.ENTRY);
        }

        TickState state = this.getNodeType().behavior(context, this);

        if (state == TickState.WAITS_SUCCESS) {
            this.setLastState(TickState.WAITS_SUCCESS);
            return;
        }

        if (state == TickState.WAITS_FAILURE) {
            this.setLastState(TickState.WAITS_FAILURE);
            return;
        }

        if (state != TickState.ENTRY) {
            this.saveState(context, state);
            this.localPointer = 0;
            this.setLastState(state);
        }
    }

    /**
     * Sets the currently active child node for this composite.
     */
    protected void activateNextChild() {
        if (this.localPointer < getChildren().size()) {
            getTree().setActiveNode(getChildren().get(this.localPointer));
        }
    }

    /**
     * Retrieves the current child node at the local pointer, executes its tick method,
     * and returns the resulting state and child node.
     *
     * @param context the game context used for ticking the child
     * @return a TickResult containing the child's last state and the child node itself
     */
    protected TickResult tickNextChild(GameContext context) {
        Node<?> child = getChildren().get(this.localPointer);
        child.tick(context);
        return new TickResult(child.getLastState(), child);
    }

    @Override
    public void insertChildAt(int index, Node<?> child) {
        child.setParent(this);
        super.insertChildAt(index, child);
        this.localPointer = index;
    }

    /**
     * Advances the local pointer to the next child if there are remaining children.
     */
    protected void advancePointer() {
        if (this.localPointer < getChildren().size()) {
            this.localPointer++;
        }
    }

    /**
     * Returns the current zero-based index used to select the next child node.
     *
     * @return the current local pointer index
     */
    protected int localPointer() {
        return this.localPointer;
    }

    /**
     * Checks whether there are still children left to tick.
     *
     * @return {@code true} if not all children have been processed yet,
     *         {@code false} if the pointer has reached or exceeded the number of children
     */
    protected boolean ticksUnCompleted() {
        return this.localPointer < getChildren().size();
    }

    /**
     * Returns the additional parameter associated with this composite node.
     *
     * @return the parameter value for this composite node
     */
    protected int getParameter() {
        return this.parameter;
    }

    private boolean waitsResult() {
        return this.getLastState() == TickState.WAITS_FAILURE || this.getLastState() == TickState.WAITS_SUCCESS;
    }

    @Override
    public void handleSkippedChildren(Node<?> target) {
        getTree().setActiveNode(target);
        int resetLimit = getChildren().indexOf(target);

        CompositeType type = this.getNodeType();
        TickState implied = switch (type) {
            case FALLBACK, PARALLEL -> TickState.FAILURE;
            case SEQUENCE -> TickState.SUCCESS;
        };

        for (int i = 0; i < resetLimit; i++) {
            Node<?> skipped = getChildren().get(i);
            skipped.handleReset();
            skipped.setLastState(implied);
        }

        this.setLastState(TickState.ENTRY);
        this.localPointer = resetLimit;
    }

    @Override
    public void handleReset() {
        this.setLastState(null);
        this.localPointer = 0;
    }

    @Override
    public Iterator<Node<?>> iterator() {
        return getChildren().iterator();
    }
}
