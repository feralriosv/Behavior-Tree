package view.finder;

import model.decisiontree.Naming;

/**
 * Exception thrown when a node cannot be found in a ladybug's decision tree.
 *
 * @author ubpst
 */
public class UnfoundedNodeException extends Exception {

    private static final String ERROR_NODE_SEARCH_FAILED = "node %s not found in ladybugs tree";

    /**
     * Initializes the exception with a standardized error message.
     *
     * @param naming the naming of the node that could not be found
     */
    public UnfoundedNodeException(Naming naming) {
        super(ERROR_NODE_SEARCH_FAILED.formatted(naming));
    }
}
