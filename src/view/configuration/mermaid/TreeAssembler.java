package view.configuration.mermaid;

import model.decisiontree.DecisionTree;
import model.decisiontree.node.Naming;
import model.decisiontree.node.Node;
import view.NodeFabric;
import view.configuration.loader.LoadCallBack;
import view.configuration.loader.LoadingException;

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

    /**
     * Creates a new assembler that uses the given {@link NodeFabric} to instantiate nodes.
     *
     * @param fabric node factory provider
     */
    public TreeAssembler(NodeFabric fabric) {
        this.fabric = fabric;
    }
    /**
     * Builds a decision tree from the parsed Mermaid data.
     *
     * @param data        Mermaid data nodes/edges
     * @param loadCallBack  (kept for API parity; not used here but available if you need side effects)
     * @return the assembly result containing the tree, the root naming, and the validity flag
     */
    public Optional<Node<?>> assemble(MermaidData data, LoadCallBack loadCallBack) {
        Map<Naming, Node<?>> nodes = new HashMap<>();

        for (Map.Entry<Naming, String> entry : data.getNodeDefinitions().entrySet()) {
            Naming naming = entry.getKey();
            String label = entry.getValue();
            Node<?> node;

            try {
                node = fabric.createNode(naming, label);
            } catch (LoadingException e) {
                return Optional.empty();
            }

            nodes.put(naming, node);
        }

        for (Naming naming : data.getReferencedIds()) {
            if (!nodes.containsKey(naming)) {
                return Optional.empty();
            }
        }

        Set<Naming> children = new HashSet<>();
        for (Edge edge : data.getEdges()) {
            Node<?> parent = nodes.get(edge.from());
            Node<?> child  = nodes.get(edge.to());
            children.add(edge.to());

            boolean success = parent.addChild(child);
            if (!success) {
                return Optional.empty();
            }
        }

        Naming rootName = findRoot(nodes.keySet(), children);
        if (rootName == null) {
            return Optional.empty();
        }

        return Optional.of(nodes.get(rootName));
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
