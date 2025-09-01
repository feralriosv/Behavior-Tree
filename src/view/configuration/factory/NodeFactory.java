package view.configuration.factory;

import game.tree.node.Node;
import game.value.NodeIdentifier;
import view.configuration.loader.LoadingException;

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
     * @param name the identifier of the node
     * @param label the label describing the node type
     * @return an {@link Optional} containing the created node if successful, otherwise empty
     * @throws LoadingException if the label cannot be parsed into a valid node
     */
    Optional<? extends Node<?>> create(NodeIdentifier name, String label) throws LoadingException;
}
