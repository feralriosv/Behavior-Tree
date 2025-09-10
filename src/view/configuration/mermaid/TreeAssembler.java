package view.configuration.mermaid;

import model.DecisionTree;
import model.node.NodeNaming;
import model.node.Node;
import view.util.NodeCreationException;
import view.util.NodeFabric;
import view.configuration.loader.LoadCallBack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Assembles a {@link DecisionTree} from parsed Mermaid data.
 *
 * @author ubpst
 */
public class TreeAssembler {

    private final NodeFabric fabric;
    private final MermaidData mermaidData;

    private final Map<NodeNaming, Node<?>> nodesCreated;
    private final Set<NodeNaming> children;

    /**
     * Creates a new assembler that uses a {@link NodeFabric} (with the given callback)
     * to instantiate nodes from Mermaid definitions.
     *
     * @param mermaidData  parsed Mermaid data (nodes, edges, labels)
     * @param loadCallBack callback used by the leaf factory to resolve leaf definitions
     */
    public TreeAssembler(MermaidData mermaidData, LoadCallBack loadCallBack) {
        this.mermaidData = mermaidData;
        this.fabric = new NodeFabric(loadCallBack);
        this.nodesCreated = new HashMap<>();
        this.children = new HashSet<>();
    }

    /**
     * Builds the decision tree from the loaded Mermaid data.
     *
     * @return an {@link Optional} containing the assembled root node,
     *         or empty if the tree could not be assembled
     */
    public Optional<Node<?>> assemble() {
        if (!allNodesCreated() || !allNodesReferenced() || !onlyCompositeParents()) {
            return Optional.empty();
        }

        NodeNaming rootName = findRoot(nodesCreated.keySet(), children);
        if (rootName == null) {
            return Optional.empty();
        }

        return Optional.of(nodesCreated.get(rootName));
    }

    private boolean allNodesCreated() {
        for (Map.Entry<NodeNaming, String> entry : this.mermaidData.getNodeDefinitions()) {
            NodeNaming nodeNaming = entry.getKey();
            String label  = entry.getValue();

            Node<?> createdNode;
            try {
                createdNode = fabric.createNode(nodeNaming, label);
            } catch (NodeCreationException e) {
                return false;
            }

            nodesCreated.put(nodeNaming, createdNode);
        }

        return true;
    }

    private boolean allNodesReferenced() {
        for (NodeNaming nodeNaming : mermaidData.getNamingsFound()) {
            if (!nodesCreated.containsKey(nodeNaming)) {
                return false;
            }
        }
        return true;
    }

    private boolean onlyCompositeParents() {
        for (Edge edge : mermaidData.getEdges()) {
            Node<?> parent = nodesCreated.get(edge.from());
            Node<?> child  = nodesCreated.get(edge.to());

            if (parent == null || child == null) {
                return false;
            }

            this.children.add(edge.to());
            if (!parent.addChild(child)) {
                return false;
            }
        }

        return true;
    }

    private static NodeNaming findRoot(Set<NodeNaming> all, Set<NodeNaming> children) {
        NodeNaming root = null;
        for (NodeNaming nodeNaming : all) {
            if (!children.contains(nodeNaming)) {
                root = nodeNaming;
            }
        }
        return root;
    }
}
