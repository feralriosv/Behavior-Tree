package model.util;

import model.decisiontree.NodeNaming;

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
     * @param nodeNaming the naming of the node that could not be found
     */
    public UnfoundedNodeException(NodeNaming nodeNaming) {
        super(ERROR_NODE_SEARCH_FAILED.formatted(nodeNaming));
    }
}
