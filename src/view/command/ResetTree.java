package view.command;

import model.Game;
import model.util.BugFinder;
import model.util.UnfoundedBugException;
import model.ladybug.BugIdentifier;
import model.ladybug.LadyBug;
import view.Command;
import view.Result;

/**
 * A command that resets the decision tree of the game to its initial state.
 *
 * @author ubpst
 */
public class ResetTree implements Command<Game> {

    private final BugIdentifier bugIdentifier;

    /**
     * Creates a new {@code ResetTree} command for the given ladybug identifier.
     *
     * @param bugIdentifier the unique {@link BugIdentifier} of the ladybug whose decision tree
     *                   should be reset
     */
    public ResetTree(BugIdentifier bugIdentifier) {
        this.bugIdentifier = bugIdentifier;
    }

    @Override
    public Result execute(Game handle) {
        BugFinder finder = new BugFinder(handle);
        LadyBug ladyBug;

        try {
            ladyBug = finder.findById(this.bugIdentifier);
        } catch (UnfoundedBugException e) {
            return Result.error(e.getMessage());
        }

        handle.resetBugTree(ladyBug);
        return Result.success();
    }
}
