package model.decisiontree;

import model.GameContext;
import model.decisiontree.node.Naming;
import model.decisiontree.node.Node;
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
    private final Map<Naming, Node<?>> nodeIndex;

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

        while (!context.wasActionExecuted()) {
            this.rootNode.tick(context);
        }

        return context.endTick();
    }

    /**
     * Resets the decision tree to its initial state.
     */
    public void reset() {
        this.activeNode = this.rootNode;
    }

    /**
     * Returns the node that is currently active in this decision tree.
     *
     * @return the currently active {@link Node}, or {@code null} if no node has been activated yet
     */
    public Node<?> getActiveNode() {
        return this.activeNode;
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
     * Fast lookup of a node by its {@link Naming} using an internal index.
     * @param naming the identifier to look up
     * @return the node if present, or {@code null} if not found
     */
    public Node<?> findByName(Naming naming) {
        return nodeIndex.get(naming);
    }

    /**
     * Checks whether a node with the specified {@link Naming} exists in this decision tree.
     *
     * @param naming the {@link Naming} identifier of the node to check for
     * @return {@code true} if a node with the given naming exists in this tree; {@code false} otherwise
     */
    public boolean containsNode(Naming naming) {
        return nodeIndex.containsKey(naming);
    }

    private void assignTree(Node<?> node) {
        node.setTree(this);
        this.nodeIndex.put(node.getNaming(), node);

        for (Node<?> child : node.getChildren()) {
            assignTree(child);
        }
    }
}
