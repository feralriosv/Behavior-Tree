package view.command;

import game.Game;
import game.LadyBug;
import game.value.Identifier;
import game.value.Vector2D;
import view.Command;
import view.Result;

import java.util.Optional;


/**
 * A command that prints the current position of a specified {@link LadyBug} in the game.
 *
 * @author ubpst
 */
public class PrintPosition implements Command<Game> {

    private static final String ERROR_UNFOUNDED_LADYBUG = "ladybug could not be found";

    private final Identifier ladyBugIdentifier;

    /**
     * Creates a new {@code PrintPosition} command for the specified ladybug.
     *
     * @param ladyBugIdentifier the unique identifier of the ladybug whose position will be printed
     */
    public PrintPosition(Identifier ladyBugIdentifier) {
        this.ladyBugIdentifier = ladyBugIdentifier;
    }

    @Override
    public Result execute(Game handle) {
        Optional<LadyBug> ladyBugOpt = handle.getBugById(this.ladyBugIdentifier);

        if (ladyBugOpt.isEmpty()) {
            return Result.error(ERROR_UNFOUNDED_LADYBUG);
        }

        Vector2D ladyBugLocation = ladyBugOpt.get().getLocation();
        return Result.success(ladyBugLocation.toString());
    }
}
