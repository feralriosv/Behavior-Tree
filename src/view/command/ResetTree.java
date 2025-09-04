package view.command;

import model.Game;
import model.decisiontree.node.Node;
import model.ladybug.Identifier;
import model.ladybug.LadyBug;
import view.Command;
import view.Result;

import java.util.Optional;

/**
 * A command that resets the decision tree of the game to its initial state.
 *
 * @author ubpst
 */
public class ResetTree implements Command<Game> {

    private final Identifier identifier;

    public ResetTree(Identifier identifier) {
        this.identifier = identifier;
    }

    @Override
    public Result execute(Game handle) {
        Optional<LadyBug> ladyBugOpt = handle.getBugById(this.identifier);
        if (ladyBugOpt.isEmpty()) {
            return Result.error("there was no bug with ID %d found");
        }

        Node<?> activeNode = handle.getBugActiveNode(ladyBugOpt.get());

        return null;
    }
}
