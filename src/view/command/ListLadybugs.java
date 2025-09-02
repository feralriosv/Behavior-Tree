package view.command;

import model.Game;
import model.ladybug.LadyBug;
import view.Command;
import view.Result;

import java.util.StringJoiner;

/**
 * A command that lists all ladybugs currently present in the game.
 *
 * @author ubpst
 */
public class ListLadybugs implements Command<Game> {

    private static final String EMPTY_SPACE = " ";

    @Override
    public Result execute(Game handle) {
        StringJoiner lineContent = new StringJoiner(EMPTY_SPACE);
        for (LadyBug ladybug : handle.getBugsInGame()) {
            lineContent.add(ladybug.getId().toString());
        }
        return Result.success(lineContent.toString());
    }
}
