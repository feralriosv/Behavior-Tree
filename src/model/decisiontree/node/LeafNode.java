package model.decisiontree.node;

import model.GameContext;

import java.util.Collections;
import java.util.Iterator;

/**
 * Represents a leaf node in the decision tree.
 * A leaf node contains an action or condition ({@link LeafType}) that can be executed
 *
 * @author ubpst
 */
public class LeafNode extends Node<LeafType> {

    /**
     * Creates a new leaf node with the given identifier and type.
     *
     * @param nodeId the unique identifier of this node
     * @param nodeType the type of the leaf, defining its behavior
     */
    public LeafNode(Naming nodeId, LeafType nodeType) {
        super(nodeId, nodeType);
    }

    @Override
    public void tick(GameContext context) {
        getNodeType().behavior(context, this);
    }

    @Override
    public Iterator<Node<?>> iterator() {
        return Collections.emptyIterator();
    }
}
