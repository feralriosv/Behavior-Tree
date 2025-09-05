package view.command;


import model.Game;
import model.decisiontree.DecisionTree;
import model.decisiontree.node.CompositeType;
import model.decisiontree.node.Naming;
import model.decisiontree.node.Node;
import model.ladybug.Identifier;
import model.ladybug.LadyBug;
import view.Command;
import view.NodeFabric;
import view.Result;
import view.NodeToken;

import java.util.Optional;


/**
 * Command implementation that allows adding a new sibling configuration
 * or related entity to the {@link Game}.
 *
 * @author ubpst
 */
public class AddSibling implements Command<Game> {

    private static final String ERROR_BUG_NOT_FOUND = "there was no bug with ID %d";
    private static final String ERROR_NODE_ALREADY_EXISTS = "node with naming %s already exists";
    private static final String ERROR_NODE_SEARCH_FAILED = "node %s not found in ladybug %s tree";
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
        Optional<LadyBug> ladyBugOpt = handle.getBugById(this.identifier);
        if (ladyBugOpt.isEmpty()) {
            return Result.error(ERROR_BUG_NOT_FOUND);
        }

        LadyBug bug = ladyBugOpt.get();

        DecisionTree decisionTree = handle.getBugDecisionTree(bug);
        Node<?> targetNode = decisionTree.findByNameFromRoot(this.nodeNaming);

        if (targetNode == null) {
            return Result.error(ERROR_NODE_SEARCH_FAILED
                    .formatted(this.nodeNaming.value(), bug.getId()));
        }

        Node<?> parent = targetNode.getParent();
        if (parent == null) {
            return Result.error(ERROR_PARENT_ROOT_NODE.formatted(targetNode.getNaming()));
        }

        if (!(CompositeType.isCompositeType(parent.getNodeType()))) {
            return Result.error("parent of %s is not composite; cannot add sibling"
                    .formatted(targetNode.getNaming()));
        }

        int indexOfTarget = parent.getChildren().indexOf(targetNode);

        String label = this.nodeToken.label();
        Naming nodeName = new Naming(this.nodeToken.name());

        for (Node<?> node : decisionTree.getAllNodes()) {
            if (node.getNaming().equals(nodeName)) {
                return Result.error(ERROR_NODE_ALREADY_EXISTS);
            }
        }

        NodeFabric fabric = new NodeFabric();
        Optional<? extends Node<?>> nodeOpt = fabric.createNode(nodeName, label);

        if (nodeOpt.isEmpty()) {
            return Result.error("invalid node");
        }

        boolean ok = parent.insertChildAt(indexOfTarget + 1, nodeOpt.get());
        if (!ok) {
            return Result.error("could not insert sibling after %s".formatted(targetNode.getNaming().value()));
        }

        return Result.success();
    }
}
