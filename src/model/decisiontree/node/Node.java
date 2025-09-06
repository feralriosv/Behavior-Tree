package model.decisiontree.node;

import model.GameContext;
import model.decisiontree.DecisionTree;
import model.decisiontree.TickResult;
import model.decisiontree.TickState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a node in a decision tree.
 *
 * @param <T> the type of node, extending {@link NodeType}
 *
 * @author ubpst
 */
public abstract class Node<T extends NodeType<?>> implements Iterable<Node<?>> {

    private final T nodeType;
    private final Naming naming;
    private final List<Node<?>> children;
    private TickState lastState;
    private DecisionTree tree;
    private Node<?> parent;

    /**
     * Creates a new node with the given identifier and type.
     *
     * @param nodeId the unique identifier of this node
     * @param nodeType the type of this node
     */
    protected Node(Naming nodeId, T nodeType) {
        this.naming = nodeId;
        this.nodeType = nodeType;
        this.children = new ArrayList<>();
        this.lastState = null;
        this.parent = null;
    }

    /**
     * Returns whether this node is the root node of the tree, i.e., has no parent.
     *
     * @return true if this node is the root node (has no parent), false otherwise
     */
    public boolean isRoot() {
        return this.parent == null;
    }

    /**
     * Executes the tick logic for this node using the provided game context.
     *
     * @param context the game context for the tick
     */
    public abstract void tick(GameContext context);

    /**
     * Called when a jump targets a child of this node.
     * Implementations should mark all children before the target as skipped,
     * setting their states appropriately and adjusting internal pointers if needed.
     *
     * @param target the child node that is the new active target
     */
    public abstract void handleSkippedChildren(Node<?> target);

    /**
     * Adds a child node to this node.
     *
     * @param child the child node to add
     * @return true if the child node was added successfully
     */
    public boolean addChild(Node<?> child) {
        child.setParent(this);
        child.setTree(this.tree);
        return this.children.add(child);
    }

    /**
     * Inserts a child node at the specified index in the children list.
     *
     * @param index the position at which to insert the child
     * @param child the child node to insert
     * @return true if the child was inserted successfully, false otherwise
     */
    public boolean insertChildAt(int index, Node<?> child) {
        if (child == null || index < 0 || index > getChildren().size()) {
            return false;
        }

        child.setTree(this.tree);
        this.children.add(index, child);
        return true;
    }

    /**
     * Saves the given tick state in the context and updates the last state of this node.
     *
     * @param context the game context
     * @param state the tick state to save
     */
    protected void saveState(GameContext context, TickState state) {
        context.logResult(new TickResult(state, this));
        this.setLastState(state);
    }

    /**
     * Returns the unique identifier of this node.
     *
     * @return the node identifier
     *
     */
    public Naming getNaming() {
        return this.naming;
    }

    /**
     * Returns the type of this node.
     *
     * @return the node type
     */
    public T getNodeType() {
        return this.nodeType;
    }

    /**
     * Returns the child nodes of this node as an unmodifiable list.
     *
     * @return the list of child nodes
     */
    public List<Node<?>> getChildren() {
        return Collections.unmodifiableList(children);
    }

    /**
     * Returns the parent node of this node.
     *
     * @return the parent node, or null if this node is the root
     */
    public Node<?> getParent() {
        return this.parent;
    }

    /**
     * Sets the parent node of this node.
     *
     * @param parent the parent node to set
     */
    public void setParent(Node<?> parent) {
        this.parent = parent;
    }

    /**
     * Returns the {@link DecisionTree} that this node belongs to, or {@code null} if it has not yet been assigned.
     *
     * @return the {@link DecisionTree} this node is part of, or {@code null} if unassigned
     */
    public DecisionTree getTree() {
        return this.tree;
    }

    /**
     * Sets the decision tree for this node.
     *
     * @param tree the decision tree to set
     */
    public void setTree(DecisionTree tree) {
        this.tree = tree;
    }

    /**
     * Sets the last tick state of this node.
     *
     * @param lastState the last tick state to set
     */
    protected void setLastState(TickState lastState) {
        this.lastState = lastState;
    }

    /**
     * Returns the last tick state of this node.
     *
     * @return the last tick state
     */
    protected TickState getLastState() {
        return this.lastState;
    }

    /**
     * Hook invoked when this node is being skipped by a jump-to operation.
     */
    protected void handleSkip() {
    }

    @Override
    public abstract Iterator<Node<?>> iterator();
}
