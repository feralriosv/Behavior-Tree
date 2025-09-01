package game.tree.node;

import game.GameContext;
import game.TickResult;
import game.value.NodeIdentifier;

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
public abstract class Node<T extends NodeType> implements Iterable<Node<?>> {

    private final T nodeType;
    private final NodeIdentifier nodeId;
    private final List<Node<?>> children;

    /**
     * Creates a new node with the given identifier and type.
     *
     * @param nodeId the unique identifier of this node
     * @param nodeType the type of this node
     */
    protected Node(NodeIdentifier nodeId, T nodeType) {
        this.nodeId = nodeId;
        this.nodeType = nodeType;
        this.children = new ArrayList<>();
    }

    /**
     * Executes this node in the given game context.
     *
     * @param context the game context in which this node is executed
     * @return a {@link TickResult} representing the outcome of execution
     */
    public abstract TickResult tick(GameContext context);

    /**
     * Returns the unique identifier of this node.
     *
     * @return the node identifier
     */
    public NodeIdentifier getNodeId() {
        return this.nodeId;
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
     * Adds a child node to this node.
     *
     * @param childNode the child node to add
     * @return true if the child node was added successfully
     */
    public boolean addChild(Node<?> childNode) {
        return this.children.add(childNode);
    }

    /**
     * Returns the type of this node.
     *
     * @return the node type
     */
    public T getNodeType() {
        return this.nodeType;
    }

    @Override
    public abstract Iterator<Node<?>> iterator();
}
