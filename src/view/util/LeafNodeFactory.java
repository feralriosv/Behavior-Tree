package view.util;

import model.decisiontree.node.LeafType;
import model.decisiontree.node.LeafNode;
import model.decisiontree.node.Naming;
import model.util.Vector2D;
import view.configuration.loader.LoadCallBack;

import java.util.Optional;

/**
 * Factory class responsible for creating {@link LeafNode} instances.
 * It parses labels into {@link LeafType}s and notifies a {@link LoadCallBack} when action nodes are created.
 *
 * @author ubpst
 */
public class LeafNodeFactory implements NodeFactory {

    private static final String LABEL_SEPARATOR = " ";
    private static final String PARAMETER_SEPARATOR = "[,\\s]+";

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
        Optional<LeafType> leafOpt = LeafType.fromLine(label.split(LABEL_SEPARATOR)[0]);
        if (leafOpt.isEmpty()) {
            return Optional.empty();
        }

        LeafType type = leafOpt.get();
        LeafNode leafNode;

        if (requiresParameters(type)) {
            Vector2D[] parameters = extractParameters(label, type);
            if (parameters == null) {
                return Optional.empty();
            }

            if (parameters.length == 2) {
                leafNode = new LeafNode(naming, type, parameters[0], parameters[1]);
            } else if (parameters.length == 1) {
                leafNode = new LeafNode(naming, type, null, parameters[0]);
            } else {
                return Optional.empty();
            }
        } else {
            leafNode = new LeafNode(naming, type);
        }

        if (this.callBack != null) {
            if (LeafType.isActionType(leafNode.getNodeType())) {
                this.callBack.markCreatedAction();
            }
        }

        return Optional.of(leafNode);
    }

    private boolean requiresParameters(LeafType type) {
        return type == LeafType.FLY || type == LeafType.EXISTS_PATH;
    }

    private Vector2D[] extractParameters(String label, LeafType type) {
        return switch (type) {
            case FLY -> parseFlyParameters(label);
            case EXISTS_PATH -> parseExistsPathParameters(label);
            default -> null;
        };
    }

    private Vector2D[] parseExistsPathParameters(String label) {
        String[] tokens = label.trim().split(LABEL_SEPARATOR);
        int[] coordinates = new int[4];

        for (int i = 0; i < 4; i++) {
            try {
                coordinates[i] = Integer.parseInt(tokens[i + 1]) - 1;
            } catch (NumberFormatException e) {
                return null;
            }
        }

        Vector2D[] vectors = new Vector2D[2];
        for (int i = 0; i < 2; i++) {
            vectors[i] = new Vector2D(coordinates[i * 2], coordinates[i * 2 + 1]);
        }

        return vectors;
    }


    private Vector2D[] parseFlyParameters(String label) {
        String[] tokens = label.trim().split(PARAMETER_SEPARATOR);

        int xDimension;
        int yDimension;
        try {
            xDimension = Integer.parseInt(tokens[1]) - 1;
            yDimension = Integer.parseInt(tokens[2]) - 1;
        } catch (NumberFormatException e) {
            return null;
        }

        return new Vector2D[]{new Vector2D(xDimension, yDimension)};
    }
}
