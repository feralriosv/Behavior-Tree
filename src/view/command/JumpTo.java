package view.command;

import model.Game;
import model.decisiontree.DecisionTree;
import model.decisiontree.Naming;
import model.decisiontree.Node;
import view.finder.BugFinder;
import view.finder.NodeFinder;
import view.finder.UnfoundedBugException;
import view.finder.UnfoundedNodeException;
import model.ladybug.Identifier;
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

    private final Identifier identifier;
    private final Naming naming;

    /**
     * Creates a new JumpTo command to reposition a LadyBug's decision tree execution to the given node.
     *
     * @param identifier the bug to affect
     * @param naming the target node name
     */
    public JumpTo(Identifier identifier, Naming naming) {
        this.identifier = identifier;
        this.naming = naming;
    }

    @Override
    public Result execute(Game handle) {
        BugFinder bugFinder = new BugFinder(handle);
        LadyBug ladyBug;

        try {
            ladyBug = bugFinder.findById(this.identifier);
        } catch (UnfoundedBugException e) {
            return Result.error(e.getMessage());
        }

        DecisionTree decisionTree = handle.getBugDecisionTree(ladyBug);
        NodeFinder nodeFinder = new NodeFinder(decisionTree);

        Node<?> target;
        try {
            target = nodeFinder.findByName(naming);
        } catch (UnfoundedNodeException e) {
            return Result.error(e.getMessage());
        }

        Node<?> parentNode = target.getParent();
        parentNode.handleSkippedChildren(target);
        return Result.success();
    }
}
