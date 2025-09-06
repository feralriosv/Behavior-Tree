package model.util;

import model.decisiontree.DecisionTree;
import model.decisiontree.node.Naming;
import model.decisiontree.node.Node;

/**
 * Helper class to find nodes by name in a decision tree.
 * <p>
 * This class standardizes object retrieval and error handling to avoid code duplication.
 *
 * @author ubpst
 */
public class NodeFinder {

    private final DecisionTree decisionTree;

    /**
     * Constructs a NodeFinder with the given decision tree.
     *
     * @param decisionTree the decision tree to search nodes in
     */
    public NodeFinder(DecisionTree decisionTree) {
        this.decisionTree = decisionTree;
    }

    /**
     * Finds a node in the decision tree by its naming.
     *
     * @param naming the naming of the node to find
     * @return the node with the specified naming
     * @throws UnfoundedNodeException if no node with the specified naming is found
     */
    public Node<?> findByName(Naming naming) throws UnfoundedNodeException {
        Node<?> node = this.decisionTree.findByName(naming);
        if (node == null) {
            throw new UnfoundedNodeException(naming);
        }
        return node;
    }
}
