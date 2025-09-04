package model.decisiontree.node;

import model.GameContext;
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
    }

    public abstract void tick(GameContext context);


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
     * @param childNode the child node to add
     * @return true if the child node was added successfully
     */
    public boolean addChild(Node<?> childNode) {
        return this.children.add(childNode);
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

    @Override
    public abstract Iterator<Node<?>> iterator();

    @Override
    public String toString() {
        return this.naming + " " + this.nodeType + " " + this.lastState;
    }
}
