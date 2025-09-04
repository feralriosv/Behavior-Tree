package view.factory;

import model.decisiontree.node.LeafType;
import model.decisiontree.node.LeafNode;
import model.decisiontree.node.Naming;
import view.configuration.loader.TreeLoader;

import java.util.Optional;

/**
 * Factory class responsible for creating {@link LeafNode} instances.
 * It parses labels into {@link LeafType}s and ensures that created action nodes are properly registered in the {@link TreeLoader}.
 *
 * @author ubpst
 */
public class LeafNodeFactory implements NodeFactory {

    private final TreeLoader loader;

    /**
     * Creates a new factory for leaf nodes using the given tree loader.
     *
     * @param loader the loader used to track created nodes, especially actions
     */
    public LeafNodeFactory(TreeLoader loader) {
        this.loader = loader;
    }

    /**
     * Creates a new factory for leaf nodes without using any tree loader.
     */
    public LeafNodeFactory() {
        this.loader = null;
    }

    @Override
    public Optional<LeafNode> create(Naming naming, String label) {
        Optional<LeafType> leafOpt = LeafType.fromLine(label);
        if (leafOpt.isPresent()) {
            LeafNode leafNode = new LeafNode(naming, leafOpt.get());

            if (this.loader != null) {
                if (LeafType.isActionType(leafNode.getNodeType())) {
                    this.loader.markCreatedAction();
                }
            }

            return Optional.of(leafNode);
        }

        return Optional.empty();
    }
}
