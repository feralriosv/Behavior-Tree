package model.util;

import model.DecisionTree;
import model.decisiontree.NodeNaming;
import model.decisiontree.Node;

/**
 * Helper class to find nodes by name in a decision tree.
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
     * @param nodeNaming the naming of the node to find
     * @return the node with the specified naming
     * @throws UnfoundedNodeException if no node with the specified naming is found
     */
    public Node<?> findByName(NodeNaming nodeNaming) throws UnfoundedNodeException {
        Node<?> node = this.decisionTree.findByName(nodeNaming);
        if (node == null) {
            throw new UnfoundedNodeException(nodeNaming);
        }
        return node;
    }
}
