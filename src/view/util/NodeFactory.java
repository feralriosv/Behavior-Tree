package view.util;

import model.decisiontree.Node;
import model.decisiontree.NodeNaming;

import java.util.Optional;

/**
 * A factory interface for creating {@link Node} instances.
 *
 * @author ubpst
 */
public interface NodeFactory {

    /**
     * Creates a new {@link Node} instance with the given identifier and label.
     *
     * @param nodeNaming the name of the node
     * @param label  the label describing the node type
     * @return the created {@link Node} instance
     */
    Optional<? extends Node<?>> create(NodeNaming nodeNaming, String label);
}
