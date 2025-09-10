package view.command;

import model.Game;
import model.DecisionTree;
import model.decisiontree.NodeNaming;
import model.decisiontree.Node;
import model.util.BugFinder;
import model.util.NodeFinder;
import model.util.UnfoundedBugException;
import model.util.UnfoundedNodeException;
import model.ladybug.BugIdentifier;
import model.ladybug.LadyBug;
import view.Command;
import view.Result;

/**
 * A command that allows jumping to a specific state or position in the game.
 * This class implements the {@link Command} interface for {@link Game}.
 *
 * @author ubpst
 */
public class JumpTo implements Command<Game> {

    private final BugIdentifier bugIdentifier;
    private final NodeNaming nodeNaming;

    /**
     * Creates a new JumpTo command to reposition a LadyBug's decision tree execution to the given node.
     *
     * @param bugIdentifier the bug to affect
     * @param nodeNaming the target node name
     */
    public JumpTo(BugIdentifier bugIdentifier, NodeNaming nodeNaming) {
        this.bugIdentifier = bugIdentifier;
        this.nodeNaming = nodeNaming;
    }

    @Override
    public Result execute(Game handle) {
        BugFinder bugFinder = new BugFinder(handle);
        LadyBug ladyBug;

        try {
            ladyBug = bugFinder.findById(this.bugIdentifier);
        } catch (UnfoundedBugException e) {
            return Result.error(e.getMessage());
        }

        DecisionTree decisionTree = handle.getBugDecisionTree(ladyBug);
        NodeFinder nodeFinder = new NodeFinder(decisionTree);

        Node<?> target;
        try {
            target = nodeFinder.findByName(nodeNaming);
        } catch (UnfoundedNodeException e) {
            return Result.error(e.getMessage());
        }

        Node<?> parentNode = target.getParent();
        parentNode.skipToChild(target);
        return Result.success();
    }
}
