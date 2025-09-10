package view.util;

import model.node.CompositeType;
import model.node.CompositeNode;
import model.node.NodeNaming;

import java.util.Optional;

/**
 * Factory for creating composite behavior tree nodes based on string labels.
 * <p>
 * Recognizes fallback ("?"), sequence ("->"), and parallel ("=M>") composite labels
 * and constructs corresponding {@link CompositeNode} instances.
 * </p>
 *
 * @author ubpst
 */
public class CompositeNodeFactory implements NodeFactory {

    private static final String PARALLEL_OPEN_SYMBOL = "=";
    private static final String PARALLEL_CLOSE_SYMBOL = ">";

    @Override
    public Optional<CompositeNode> create(NodeNaming nodeNaming, String label) {
        if (CompositeType.FALLBACK.getSymbol().equals(label)) {
            return Optional.of(new CompositeNode(nodeNaming, CompositeType.FALLBACK));
        }

        if (CompositeType.SEQUENCE.getSymbol().equals(label)) {
            return Optional.of(new CompositeNode(nodeNaming, CompositeType.SEQUENCE));
        }

        if (isParallel(label)) {
            int parameter = extractParameter(label);
            if (parameter < 0) {
                return Optional.empty();
            }
            return Optional.of(new CompositeNode(nodeNaming, CompositeType.PARALLEL, parameter));
        }

        return Optional.empty();
    }

    private static int extractParameter(String label) {
        String inner = label.substring(1, label.length() - 1);
        int parameter;
        try {
            parameter = Integer.parseInt(inner);
        } catch (NumberFormatException e) {
            return -1;
        }
        if (parameter < 1) {
            return -1;
        }
        return parameter;
    }

    private boolean isParallel(String raw) {
        return raw != null && raw.startsWith(PARALLEL_OPEN_SYMBOL) && raw.endsWith(PARALLEL_CLOSE_SYMBOL);
    }
}
