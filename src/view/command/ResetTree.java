package view.command;

import model.Game;
import view.Command;
import view.Result;

/**
 * A command that resets the decision tree of the game to its initial state.
 *
 * @author ubpst
 */
public class ResetTree implements Command<Game> {
    @Override
    public Result execute(Game handle) {
        return null;
    }
}
