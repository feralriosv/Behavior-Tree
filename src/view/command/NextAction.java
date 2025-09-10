package view.command;

import model.Game;
import model.DecisionTree;
import model.ladybug.LadyBug;
import model.TickResult;
import model.board.BoardDisplayer;
import view.Command;
import view.Result;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * A command that executes the next action for each ladybug in the game.
 *
 * @author ubpst
 */
public class NextAction implements Command<Game> {

    private static final String DISPLAY_FORMAT = "%s %s";

    @Override
    public Result execute(Game handle) {
        StringJoiner displayContent = new StringJoiner(System.lineSeparator());

        for (Map.Entry<LadyBug, DecisionTree> entry : handle.getBugsAndTrees()) {
            LadyBug bug = entry.getKey();
            DecisionTree tree = entry.getValue();

            List<TickResult> tickResults = tree.tick(handle.context(), bug);
            for (TickResult result : tickResults) {
                displayContent.add(DISPLAY_FORMAT.formatted(bug.getId(), result));
            }

            BoardDisplayer displayer = new BoardDisplayer(handle.getBoard(), handle.getBugsInGame());
            displayContent.add(displayer.renderBoard());
        }

        return Result.success(displayContent.toString());
    }
}
