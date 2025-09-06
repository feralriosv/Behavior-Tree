package view.command;

import model.Game;
import view.finder.BugFinder;
import view.finder.UnfoundedBugException;
import model.ladybug.Identifier;
import model.ladybug.LadyBug;
import view.Command;
import view.Result;

/**
 * A command that resets the decision tree of the game to its initial state.
 *
 * @author ubpst
 */
public class ResetTree implements Command<Game> {

    private final Identifier identifier;

    /**
     * Creates a new {@code ResetTree} command for the given ladybug identifier.
     *
     * @param identifier the unique {@link Identifier} of the ladybug whose decision tree
     *                   should be reset
     */
    public ResetTree(Identifier identifier) {
        this.identifier = identifier;
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

        handle.resetBugTree(ladyBug);
        return Result.success();
    }
}
