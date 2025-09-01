package view.command;

import game.Game;
import view.Command;
import view.Result;

/**
 * A command that allows jumping to a specific state or position in the game.
 * This class implements the {@link Command} interface for {@link Game}.
 *
 * @author ubpst
 */
public class JumpTo implements Command<Game> {
    @Override
    public Result execute(Game handle) {
        return null;
    }
}
