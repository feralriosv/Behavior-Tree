package model.decisiontree;

import model.GameContext;
import model.decisiontree.node.Naming;
import model.decisiontree.node.Node;
import model.ladybug.LadyBug;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a decision tree used to control the behavior of a {@link LadyBug}.
 *
 * @author ubpst
 */
public class DecisionTree {

    private final Node<?> rootNode;
    private Node<?> activeNode;
    private final List<Node<?>> allNodes;

    /**
     * Creates a new decision tree with the given root node.
     *
     * @param root the root node of this decision tree
     */
    public DecisionTree(Node<?> root) {
        this.allNodes = new ArrayList<>();
        this.rootNode = root;
        this.assignTree(root);
    }

    /**
     * Searches for a node within the tree by its {@link Naming}, starting from the root.
     * Uses a depth-first search (DFS).
     *
     * @param naming the identifier of the node to search for
     * @return the node with the matching naming, or {@code null} if not found
     */
    public Node<?> findByNameFromRoot(Naming naming) {
        return findNodeByNamingDFS(this.rootNode, naming);
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

    private Node<?> findNodeByNamingDFS(Node<?> root, Naming naming) {
        if (root.getNaming().equals(naming)) {
            return root;
        }

        List<Node<?>> children = root.getChildren();
        for (Node<?> child : children) {
            Node<?> hit = findNodeByNamingDFS(child, naming);
            if (hit != null) {
                return hit;
            }
        }

        return null;
    }
    /**
     * Returns an unmodifiable view of all nodes contained in this decision tree.
     *
     * @return an unmodifiable list of all {@link Node} instances in this tree
     */
    public List<Node<?>> getAllNodes() {
        return Collections.unmodifiableList(allNodes);
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
        return activeNode;
    }

    /**
     * Updates the reference to the node that is currently active in this decision tree.
     *
     * @param activeNode the {@link Node} that should be marked as active
     */
    public void setActiveNode(Node<?> activeNode) {
        this.activeNode = activeNode;
    }

    private void assignTree(Node<?> node) {
        node.setTree(this);
        this.allNodes.add(node);

        for (Node<?> child : node.getChildren()) {
            assignTree(child);
        }
    }
}
