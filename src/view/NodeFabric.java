package view;

import model.decisiontree.node.Naming;
import model.decisiontree.node.Node;
import view.configuration.loader.LoadingException;
import view.configuration.loader.TreeLoader;
import view.factory.CompositeNodeFactory;
import view.factory.LeafNodeFactory;
import view.factory.NodeFactory;

import java.util.List;
import java.util.Optional;

/**
 * Factory aggregator that can create decision-tree nodes from a textual label.
 * <p>
 * Delegates to a set of {@link NodeFactory} implementations (composite and leaf)
 * in order until one recognizes the label and returns a concrete {@link Node}.
 * If no factory accepts the label, a {@link LoadingException} is thrown.
 *
 * @author ubpst
 */
public class NodeFabric {

    private static final String ERROR_UNKNOWN_LABEL = "unknown node label: %s";

    private final List<NodeFactory> factories;

    /**
     * Creates a new node fabric with the default factories (composite and leaf).
     *
     * @param loader a {@link TreeLoader} used by the leaf factory to resolve leaf definitions
     */
    public NodeFabric(TreeLoader loader) {
        this.factories = List.of(new CompositeNodeFactory(), new LeafNodeFactory(loader));
    }

    /**
     * Creates a new node fabric with the default factories (composite and leaf),
     * using the parameterless leaf factory.
     */
    public NodeFabric() {
        this.factories = List.of(new CompositeNodeFactory(), new LeafNodeFactory());
    }

    /**
     * Creates a concrete {@link Node} instance for the given logical node name and textual label.
     *
     * @param nodeName the identifier of the node to be created
     * @param label the textual label describing the node's behavior.
     * @return a concrete {@link Node}
     * @throws LoadingException if the label is empty/blank or no factory recognizes the label
     */
    public Node<?> createNode(Naming nodeName, String label) throws LoadingException {
        for (NodeFactory factory : this.factories) {
            Optional<? extends Node<?>> maybe = factory.create(nodeName, label);
            if (maybe.isPresent()) {
                return maybe.get();
            }
        }

        throw new LoadingException(ERROR_UNKNOWN_LABEL.formatted(label));
    }
}
