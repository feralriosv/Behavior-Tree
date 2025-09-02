package view.configuration.factory;

import model.decisiontree.node.CompositeType;
import model.decisiontree.node.CompositeNode;
import model.decisiontree.node.Naming;
import view.configuration.loader.LoadingException;

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

    private static final String ERROR_INVALID_NODE = "invalid ParallelNode: %s";
    private static final String ERROR_INVALID_PARAMETER = "parallel M must be an integer: %s";

    /**
     * Attempts to create a composite node for the given identifier and label.
     *
     * @param naming the node identifier
     * @param label the string label representing the composite type
     * @return an {@link Optional} with a new {@link CompositeNode} if recognized; empty otherwise
     * @throws LoadingException if the label is invalid or a parameter cannot be parsed
     */
    @Override public Optional<CompositeNode> create(Naming naming, String label) throws LoadingException {
        if (CompositeType.FALLBACK.getSymbol().equals(label)) {
            return Optional.of(new CompositeNode(naming, CompositeType.FALLBACK));
        }

        if (CompositeType.SEQUENCE.getSymbol().equals(label)) {
            return Optional.of(new CompositeNode(naming, CompositeType.SEQUENCE));
        }

        if (isParallel(label)) {
            int parameter = extractParameter(label);
            return Optional.of(new CompositeNode(naming, CompositeType.PARALLEL, parameter));
        }

        return Optional.empty();
    }

    private static int extractParameter(String label) throws LoadingException {
        String inner = label.substring(1, label.length() - 1);
        int parameter;
        try {
            parameter = Integer.parseInt(inner);
        } catch (NumberFormatException e) {
            throw new LoadingException(ERROR_INVALID_NODE.formatted(label));
        }
        if (parameter < 1) {
            throw new LoadingException(ERROR_INVALID_PARAMETER.formatted(label));
        }
        return parameter;
    }

    private boolean isParallel(String raw) {
        return raw != null && raw.startsWith("=") && raw.endsWith(">");
    }
}
