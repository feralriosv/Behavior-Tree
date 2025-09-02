package view.command;


import model.Game;
import view.Command;
import view.Result;


/**
 * Command that displays or manipulates the "head" state of the {@link Game}.
 *
 * @author ubpst
 */
public class Head implements Command<Game> {
    @Override
    public Result execute(Game handle) {
        return null;
    }
}
