package view.util;

import model.decisiontree.NodeNaming;
import model.decisiontree.Node;
import view.configuration.loader.LoadCallBack;

import java.util.List;
import java.util.Optional;

/**
 * Factory aggregator that can create decision-tree nodes from a textual label.
 *
 * @author ubpst
 */
public class NodeFabric {

    private final List<NodeFactory> factories;

    /**
     * Creates a new node fabric with the default factories (composite and leaf).
     *
     * @param callBack the callback used by the leaf factory to resolve leaf node definitions
     */
    public NodeFabric(LoadCallBack callBack) {
        this.factories = List.of(new CompositeNodeFactory(), new LeafNodeFactory(callBack));
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
     * @throws NodeCreationException if the label is empty/blank or no factory recognizes the label
     */
    public Node<?> createNode(NodeNaming nodeName, String label) throws NodeCreationException {
        for (NodeFactory factory : this.factories) {
            Optional<? extends Node<?>> nodeOpt = factory.create(nodeName, label);
            if (nodeOpt.isPresent()) {
                return nodeOpt.get();
            }
        }
        throw new NodeCreationException(nodeName, label);
    }
}
