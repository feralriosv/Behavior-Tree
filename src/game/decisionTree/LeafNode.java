package game.decisionTree;

import game.GameContext;
import game.value.Naming;

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
    public TickResult tick(GameContext context) {
        TickState state = getNodeType().behavior(context, this);

        TickResult result = new TickResult(state, this);
        context.logResult(result);

        if (LeafType.isActionType(getNodeType())) {
            context.markAction();
        }

        return result;
    }

    @Override
    public boolean addChild(Node<?> childNode) {
        return false;
    }

    @Override
    public Iterator<Node<?>> iterator() {
        return Collections.emptyIterator();
    }
}
