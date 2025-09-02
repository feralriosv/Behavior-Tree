package view.command;

import model.Game;
import model.GameContext;
import model.ladybug.LadyBug;
import model.decisiontree.TickResult;
import model.decisiontree.DecisionTree;
import view.BoardDisplayer;
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
            LadyBug ladyBug = entry.getKey();
            DecisionTree tree = entry.getValue();

            GameContext context = handle.context();
            List<TickResult> tickResults = tree.tick(context, ladyBug);

            for (TickResult result : tickResults) {
                displayContent.add(DISPLAY_FORMAT.formatted(ladyBug.getId(), result));
            }

            BoardDisplayer displayer = new BoardDisplayer(handle.getBoard(), handle.getBugsInGame());
            displayContent.add(displayer.renderBoard());
        }

        return Result.success(displayContent.toString());
    }
}
