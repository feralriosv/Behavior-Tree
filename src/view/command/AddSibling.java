package view.command;


import model.Game;
import view.Command;
import view.Result;


/**
 * Command implementation that allows adding a new sibling configuration
 * or related entity to the {@link Game}.
 *
 * @author ubpst
 */
public class AddSibling implements Command<Game> {
    @Override
    public Result execute(Game handle) {
        return null;
    }
}
