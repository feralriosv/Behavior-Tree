package view.configuration.mermaid;

import model.decisiontree.DecisionTree;
import model.decisiontree.node.Naming;
import model.decisiontree.node.Node;
import view.fabric.NodeCreationException;
import view.fabric.NodeFabric;
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

    private final Map<Naming, Node<?>> nodes;
    private final Set<Naming> children;

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
        this.nodes = new HashMap<>();
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

        Naming rootName = findRoot(nodes.keySet(), children);
        if (rootName == null) {
            return Optional.empty();
        }

        return Optional.of(nodes.get(rootName));
    }

    private boolean allNodesCreated() {
        for (Map.Entry<Naming, String> entry : mermaidData.getNodeDefinitions().entrySet()) {
            Naming naming = entry.getKey();
            String label  = entry.getValue();

            Node<?> createdNode;
            try {
                createdNode = fabric.createNode(naming, label);
            } catch (NodeCreationException e) {
                return false;
            }

            nodes.put(naming, createdNode);
        }
        return true;
    }

    private boolean allNodesReferenced() {
        for (Naming naming : mermaidData.getReferencedIds()) {
            if (!nodes.containsKey(naming)) {
                return false;
            }
        }
        return true;
    }

    private boolean onlyCompositeParents() {
        for (Edge edge : mermaidData.getEdges()) {
            Node<?> parent = nodes.get(edge.from());
            Node<?> child  = nodes.get(edge.to());
            if (parent == null || child == null) {
                return false;
            }

            children.add(edge.to());
            if (!parent.addChild(child)) {
                return false;
            }
        }
        return true;
    }

    private static Naming findRoot(Set<Naming> all, Set<Naming> children) {
        Naming root = null;
        for (Naming naming : all) {
            if (!children.contains(naming)) {
                root = naming;
            }
        }
        return root;
    }
}
