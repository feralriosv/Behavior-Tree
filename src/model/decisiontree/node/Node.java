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

    public Node<?> getParent() {
        return this.parent;
    }

    public void setParent(Node<?> parent) {
        this.parent = parent;
    }

    public DecisionTree getTree() {
        return this.tree;
    }

    public void setTree(DecisionTree tree) {
        this.tree = tree;
    }

    protected void setLastState(TickState lastState) {
        this.lastState = lastState;
    }

    protected TickState getLastState() {
        return this.lastState;
    }

    protected void saveState(GameContext context, TickState state) {
        context.logResult(new TickResult(state, this));
        this.setLastState(state);
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

    public boolean insertChildAt(int index, Node<?> child) {
        if (child == null || index < 0 || index > getChildren().size()) {
            return false;
        }

        child.setTree(this.tree);
        this.children.add(index, child);
        return true;
    }

        /**
         * Returns the unique identifier of this node.
         *
         * @return the node identifier
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

    public abstract void tick(GameContext context);

    @Override
    public abstract Iterator<Node<?>> iterator();

    @Override
    public String toString() {
        return this.naming + " " + this.nodeType + " " + this.lastState;
    }
}
