package view.command;


import model.Game;
import model.decisiontree.DecisionTree;
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
import view.configuration.NodeToken;

/**
 * Command implementation that allows adding a new sibling configuration
 * or related entity to the {@link Game}.
 *
 * @author ubpst
 */
public class AddSibling implements Command<Game> {

    private static final String ERROR_SIBLING_NOT_ADDED = "node could not be added";

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

        Node<?> treeNode;
        try {
            treeNode = nodeFinder.findByName(this.nodeNaming);
        } catch (UnfoundedNodeException e) {
            return Result.error(e.getMessage());
        }

        NodeFabric fabric = new NodeFabric();
        Node<?> newSibling;

        try {
            newSibling = fabric.createNode(nodeNaming, nodeToken.label());
        } catch (NodeCreationException e) {
            return Result.error(e.getMessage());
        }

        if (!decisionTree.addSibling(treeNode, newSibling)) {
            return Result.error(ERROR_SIBLING_NOT_ADDED);
        }

        return Result.success();
    }
}
