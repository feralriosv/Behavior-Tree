package model.node;

import model.GameContext;
import model.DecisionTree;
import model.TickResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a node in a decision tree.
 *
 * @param <T> the type of node, extending {@link NodeType}
 * @author ubpst
 */
public abstract class Node<T extends NodeType<?>> implements Iterable<Node<?>> {

    private static final String STRING_FORMAT = "%s %s";

    private final T nodeType;
    private final NodeNaming nodeNaming;
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
    protected Node(NodeNaming nodeId, T nodeType) {
        this.nodeNaming = nodeId;
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
     * Marks this node as the currently active node in its decision tree.
     *
     * @param context the game context in which this tick is executed
     */
    public void tick(GameContext context) {
        this.tree.setActiveNode(this);
    }

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
     * Inserts a new sibling node into the list of children of this node, directly to the right of the given.
     *
     * @param childNode   the existing child node whose right side will be used
     *                    as the insertion position
     * @param newSibling  the new node to insert as a sibling
     * @return {@code true} if the sibling was successfully inserted,
     *         {@code false} otherwise.
     */
    public abstract boolean insertSibling(Node<?> childNode, Node<?> newSibling);

    /**
     * Saves the given tick state in the context and updates the last state of this node.
     *
     * @param context the game context
     * @param state the tick state to save
     */
    protected void saveState(GameContext context, TickState state) {
        context.registerResult(new TickResult(state, this));
        this.setLastState(state);
    }

    /**
     * Returns the unique identifier of this node.
     *
     * @return the node identifier
     *
     */
    public NodeNaming getNodeNaming() {
        return this.nodeNaming;
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
    protected void setParent(Node<?> parent) {
        this.parent = parent;
    }

    /**
     * Returns the {@link DecisionTree} that this node belongs to, or {@code null} if it has not yet been assigned.
     *
     * @return the {@link DecisionTree} this node is part of, or {@code null} if unassigned
     */
    protected DecisionTree getTree() {
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
    public void setLastState(TickState lastState) {
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
     * Hook invoked when this node is being reset by a jump-to operation.
     */
    public abstract void handleReset();

    /**
     * Called when a jump targets a child of this node.
     *
     * @param target the child node that is the new active target
     */
    public abstract void skipToChild(Node<?> target);


    @Override
    public abstract Iterator<Node<?>> iterator();

    @Override
    public String toString() {
        return STRING_FORMAT.formatted(this.nodeNaming, this.nodeType.label());
    }
}
