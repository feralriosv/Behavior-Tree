package view.util;

import model.decisiontree.node.Node;
import model.decisiontree.node.Naming;

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
     * @param naming the name of the node
     * @param label  the label describing the node type
     * @return the created {@link Node} instance
     */
    Optional<? extends Node<?>> create(Naming naming, String label);
}
