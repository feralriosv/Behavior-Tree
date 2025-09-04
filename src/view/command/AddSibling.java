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
import view.configuration.loader.LoadingException;

import java.util.Optional;


/**
 * Command implementation that allows adding a new sibling configuration
 * or related entity to the {@link Game}.
 *
 * @author ubpst
 */
public class AddSibling implements Command<Game> {

    private static final String ERROR_NODE_SEARCH_FAILED = "node %s not found in ladybug %s tree";

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
            return Result.error("there was no bug with ID %d");
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
            return Result.error("cannot add sibling to root node %s".formatted(targetNode.getNaming()));
        }

        if (!(CompositeType.isCompositeType(parent.getNodeType()))) {
            return Result.error("parent of %s is not composite; cannot add sibling"
                    .formatted(targetNode.getNaming()));
        }

        int indexOfTarget = parent.getChildren().indexOf(targetNode);
        if (indexOfTarget < 0) {
            return Result.error("internal error: target node is not listed among its parent's children");
        }

        String label = this.nodeToken.label().isEmpty() ? "" : this.nodeToken.label().trim();
        if (label.isEmpty()) {
            return Result.error("empty label for new sibling %s".formatted(this.nodeToken.name()));
        }

        if (parent.getNaming().value().equals(label)) {
            return Result.error("node %s already exists".formatted(label));
        }

        Naming newId = new Naming(this.nodeToken.name());
        NodeFabric fabric = new NodeFabric();
        Node<?> newNode;

        try {
            newNode = fabric.createNode(newId, label);
        } catch (LoadingException e) {
            return Result.error(e.getMessage());
        }

        boolean ok = parent.insertChildAt(indexOfTarget + 1, newNode);
        if (!ok) {
            return Result.error("could not insert sibling after %s".formatted(targetNode.getNaming().value()));
        }

        return Result.success();
    }
}
