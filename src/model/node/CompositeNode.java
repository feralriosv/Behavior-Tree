package model.node;

import model.GameContext;

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
    public CompositeNode(NodeNaming nodeId, CompositeType nodeType, int parameter) {
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
    public CompositeNode(NodeNaming nodeId, CompositeType type) {
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
            this.setLastState(state);
            this.localPointer = 0;
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
     * Ticks the child node currently pointed to by the local pointer.
     *
     * @param context the {@link GameContext} in which the child is executed
     * @return the {@link TickState} produced by the child after ticking
     */
    protected TickState tickNextChild(GameContext context) {
        Node<?> child = getChildren().get(this.localPointer);
        child.tick(context);
        return child.getLastState();
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
    public boolean insertSibling(Node<?> childNode, Node<?> newSibling) {
        int targetIndex = getChildren().indexOf(childNode);

        if (targetIndex < 0) {
            return false;
        }

        newSibling.setParent(this);
        newSibling.setTree(getTree());
        getChildren().add(targetIndex, newSibling);
        return true;
    }

    @Override
    public void skipToChild(Node<?> target) {
        getTree().setActiveNode(target);
        int resetLimit = getChildren().indexOf(target);

        CompositeType type = this.getNodeType();
        TickState impliedState = switch (type) {
            case FALLBACK, PARALLEL -> TickState.FAILURE;
            case SEQUENCE -> TickState.SUCCESS;
        };

        for (int i = 0; i < resetLimit; i++) {
            Node<?> skipped = getChildren().get(i);
            skipped.handleReset();
            skipped.setLastState(impliedState);
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
