package model;

import model.node.NodeNaming;
import model.node.Node;
import model.ladybug.LadyBug;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a decision tree used to control the behavior of a {@link LadyBug}.
 *
 * @author ubpst
 */
public class DecisionTree {

    private final Node<?> rootNode;
    private Node<?> activeNode;
    private final Map<NodeNaming, Node<?>> nodeIndex;

    /**
     * Creates a new decision tree with the given root node.
     *
     * @param root the root node of this decision tree
     */
    public DecisionTree(Node<?> root) {
        this.rootNode = root;
        this.nodeIndex = new HashMap<>();
        this.activeNode = root;
        this.assignTree(root);
    }

    private DecisionTree() {
        this.nodeIndex = null;
        this.rootNode = null;
        this.activeNode = null;
    }

    /**
     * Attempts to insert a new sibling node immediately after a given target node in the decision tree.
     *
     * @param childNode the node after which the new sibling should be inserted
     * @param newSibling the node to insert as a sibling
     * @return {@code true} if the sibling was successfully inserted; {@code false} otherwise
     */
    public boolean addSibling(Node<?> childNode, Node<?> newSibling) {
        if (childNode.isRoot() || this.containsNode(newSibling.getNodeNaming())) {
            return false;
        }

        Node<?> parent = childNode.getParent();
        return parent.insertSibling(childNode, newSibling);
    }

    /**
     * Creates a special placeholder tree that is considered "unplayable".
     *
     * @return a {@link DecisionTree} instance that is marked as unplayable
     */
    public static DecisionTree unplayableTree() {
        return new DecisionTree();
    }

    /**
     * Checks whether this decision tree is unplayable.
     *
     * @return {@code true} if this tree is unplayable; {@code false} otherwise
     */
    public boolean isUnplayableTree() {
        return this.rootNode == null
                && this.nodeIndex == null
                && this.activeNode == null;
    }

    /**
     * Executes a tick of the decision tree for the given ladybug in the specified game context.
     *
     * @param context the game context in which this tick is executed
     * @param ladyBug the ladybug whose behavior is determined by the decision tree
     * @return a list of {@link TickResult} representing the outcomes of the tick
     */
    public List<TickResult> tick(GameContext context, LadyBug ladyBug) {
        context.beginTick(ladyBug);

        while (!context.actionExecuted()) {
            this.rootNode.tick(context);
        }

        return context.endTick();
    }

    /**
     * Resets the decision tree to its initial state.
     */
    protected void resetTree() {
        this.activeNode = this.rootNode;
        this.resetSubtree(this.rootNode);
    }

    /**
     * Updates the reference to the node that is currently active in this decision tree.
     *
     * @param activeNode the {@link Node} that should be marked as active
     */
    public void setActiveNode(Node<?> activeNode) {
        this.activeNode = activeNode;
    }

    /**
     * Fast lookup of a node by its {@link NodeNaming} using an internal index.
     *
     * @param nodeNaming the identifier to look up
     * @return the node if present, or {@code null} if not found
     */
    public Node<?> findByName(NodeNaming nodeNaming) {
        return this.nodeIndex.get(nodeNaming);
    }

    /**
     * Returns the node that is currently active in this decision tree.
     *
     * @return the currently active {@link Node}, or {@code null} if no node has been activated yet
     */
    protected Node<?> getActiveNode() {
        return this.activeNode;
    }

    private void resetSubtree(Node<?> node) {
        if (node == null) {
            return;
        }
        node.handleReset();
        for (Node<?> child : node.getChildren()) {
            resetSubtree(child);
        }
    }

    private boolean containsNode(NodeNaming nodeNaming) {
        return this.nodeIndex.containsKey(nodeNaming);
    }

    private void assignTree(Node<?> node) {
        node.setTree(this);
        this.nodeIndex.put(node.getNodeNaming(), node);

        for (Node<?> child : node.getChildren()) {
            assignTree(child);
        }
    }
}
