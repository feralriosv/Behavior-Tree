package view.util;

import model.decisiontree.node.LeafType;
import model.decisiontree.node.LeafNode;
import model.decisiontree.node.Naming;
import model.util.Vector2D;
import view.Arguments;
import view.InvalidArgumentException;
import view.configuration.loader.LoadCallBack;

import java.util.Optional;

/**
 * Factory class responsible for creating {@link LeafNode} instances.
 * It parses labels into {@link LeafType}s and notifies a {@link LoadCallBack} when action nodes are created.
 *
 * @author ubpst
 */
public class LeafNodeFactory implements NodeFactory {

    private static final String LABEL_PARTS_SEPARATOR = " ";
    private static final String VECTOR_SEPARATOR = "[,\\s]+";

    private final LoadCallBack callBack;

    /**
     * Creates a new factory for leaf nodes using the given tree loader.
     *
     * @param loader callback used to track created nodes, especially actions
     */
    public LeafNodeFactory(LoadCallBack loader) {
        this.callBack = loader;
    }

    /**
     * Creates a new factory for leaf nodes without using any tree loader.
     */
    public LeafNodeFactory() {
        this.callBack = null;
    }

    @Override
    public Optional<LeafNode> create(Naming naming, String label) {
        String[] parts = label.trim().split(LABEL_PARTS_SEPARATOR, 2);

        Optional<LeafType> leafOpt = LeafType.fromLine(parts[0]);
        if (leafOpt.isEmpty()) {
            return Optional.empty();
        }

        LeafType type = leafOpt.get();
        LeafNode leafNode;

        if (requiresVectors(type)) {
            Vector2D[] vectorsFound = extractVectors(parts[1]);
            if (vectorsFound == null || vectorsFound.length == 0 || vectorsFound.length > 2) {
                return Optional.empty();
            }

            leafNode = (vectorsFound.length == 2)
                    ? new LeafNode(naming, type, vectorsFound[0], vectorsFound[1])
                    : new LeafNode(naming, type, null, vectorsFound[0]);
        } else {
            leafNode = new LeafNode(naming, type);
        }

        if (this.callBack != null) {
            if (LeafType.isActionType(leafNode.getNodeType())) {
                this.callBack.markCreatedActionNode();
            }
        }

        return Optional.of(leafNode);
    }

    private boolean requiresVectors(LeafType type) {
        return type == LeafType.FLY || type == LeafType.EXISTS_PATH;
    }

    private Vector2D[] extractVectors(String vectorArguments) {
        Arguments argumentHolder = new Arguments(vectorArguments.trim().split(VECTOR_SEPARATOR));

        try {
            return argumentHolder.parseVectors();
        } catch (InvalidArgumentException e) {
            return null;
        }
    }
}
