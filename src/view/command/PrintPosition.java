package view.command;

import model.Game;
import view.finder.BugFinder;
import view.finder.UnfoundedBugException;
import model.ladybug.LadyBug;
import model.ladybug.Identifier;
import model.ladybug.Vector2D;
import view.Command;
import view.Result;


/**
 * A command that prints the current position of a specified {@link LadyBug} in the game.
 *
 * @author ubpst
 */
public class PrintPosition implements Command<Game> {

    private static final String ERROR_UNFOUNDED_LADYBUG = "ladybug could not be found";

    private final Identifier identifier;

    /**
     * Creates a new {@code PrintPosition} command for the specified ladybug.
     *
     * @param ladyBugIdentifier the unique identifier of the ladybug whose position will be printed
     */
    public PrintPosition(Identifier ladyBugIdentifier) {
        this.identifier = ladyBugIdentifier;
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

        Vector2D ladyBugLocation = ladyBug.getLocation();
        return Result.success(ladyBugLocation.toString());
    }
}
