package game.tree.node;

/**
 * Marker interface for all node types in the decision tree.
 * A {@code NodeType} defines how a node is identified and displayed.
 *
 * @author ubpst
 */
public interface NodeType {

    /**
     * Returns a human-readable label representing this node type.
     *
     * @return the display label of the node type
     */
    String displayLabel();
}

