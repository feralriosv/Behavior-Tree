package view.command;


import model.Game;
import model.decisiontree.DecisionTree;
import model.decisiontree.node.CompositeType;
import model.decisiontree.node.Naming;
import model.decisiontree.node.Node;
import model.util.BugFinder;
import model.util.NodeFinder;
import model.util.UnfoundedBugException;
import model.util.UnfoundedNodeException;
import model.ladybug.Identifier;
import model.ladybug.LadyBug;
import view.Command;
import view.util.NodeCreationException;
import view.util.NodeFabric;
import view.Result;
import view.NodeToken;

/**
 * Command implementation that allows adding a new sibling configuration
 * or related entity to the {@link Game}.
 *
 * @author ubpst
 */
public class AddSibling implements Command<Game> {

    private static final String ERROR_NODE_ALREADY_EXISTS = "node with naming %s already exists";
    private static final String ERROR_PARENT_ROOT_NODE =  "cannot add sibling to root node %s";

    private final Identifier identifier;
    private final Naming nodeNaming;
    private final NodeToken nodeToken;

    /**
     * Constructs the AddSibling command.
     *
     * @param identifier the identifier of the ladybug whose decision tree is being modified
     * @param nodeNaming the naming of the existing node to which a sibling will be added
     * @param nodeToken  the token representing the new sibling node to be created and added
     */
    public AddSibling(Identifier identifier, Naming nodeNaming, NodeToken nodeToken) {

        this.identifier = identifier;
        this.nodeNaming = nodeNaming;
        this.nodeToken = nodeToken;
    }

    @Override
    public Result execute(Game handle) {
        BugFinder finder = new BugFinder(handle);

        LadyBug ladyBug;
        try {
            ladyBug = finder.findById(this.identifier);
        } catch (UnfoundedBugException e) {
            return Result.error(e.getMessage());
        }

        DecisionTree decisionTree = handle.getBugDecisionTree(ladyBug);
        NodeFinder nodeFinder = new NodeFinder(decisionTree);

        Node<?> targetNode;
        try {
            targetNode = nodeFinder.findByName(this.nodeNaming);
        } catch (UnfoundedNodeException e) {
            return Result.error(e.getMessage());
        }

        if (targetNode.isRoot()) {
            return Result.error(ERROR_PARENT_ROOT_NODE.formatted(targetNode.getNaming()));
        }

        Node<?> parent = targetNode.getParent();
        if (!(CompositeType.isCompositeType(parent.getNodeType()))) {
            return Result.error("parent of %s is not composite; cannot add sibling"
                    .formatted(targetNode.getNaming()));
        }

        int indexOfTarget = parent.getChildren().indexOf(targetNode);

        String label = this.nodeToken.label();
        Naming nodeName = new Naming(this.nodeToken.name());

        if (decisionTree.containsNode(nodeName)) {
            return Result.error(ERROR_NODE_ALREADY_EXISTS);
        }

        NodeFabric fabric = new NodeFabric();
        Node<?> node;

        try {
            node = fabric.createNode(nodeName, label);
        } catch (NodeCreationException e) {
            return Result.error(e.getMessage());
        }

        boolean ok = parent.insertChildAt(indexOfTarget + 1, node);
        if (!ok) {
            return Result.error("could not insert sibling after %s".formatted(targetNode.getNaming().value()));
        }

        return Result.success();
    }
}
