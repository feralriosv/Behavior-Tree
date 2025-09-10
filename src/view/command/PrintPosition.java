package view.command;

import model.Game;
import view.util.Vector2DDecorator;
import model.util.BugFinder;
import model.util.UnfoundedBugException;
import model.ladybug.LadyBug;
import model.ladybug.BugIdentifier;
import view.Command;
import view.Result;


/**
 * A command that prints the current position of a specified {@link LadyBug} in the game.
 *
 * @author ubpst
 */
public class PrintPosition implements Command<Game> {

    private final BugIdentifier bugIdentifier;

    /**
     * Creates a new {@code PrintPosition} command for the specified ladybug.
     *
     * @param ladyBugBugIdentifier the unique identifier of the ladybug whose position will be printed
     */
    public PrintPosition(BugIdentifier ladyBugBugIdentifier) {
        this.bugIdentifier = ladyBugBugIdentifier;
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

        Vector2DDecorator positionDeco = new Vector2DDecorator(ladyBug.getLocation());
        return Result.success(positionDeco.asTuple());
    }
}
