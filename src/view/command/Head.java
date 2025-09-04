package view.command;


import model.Game;
import model.decisiontree.node.Node;
import model.ladybug.Identifier;
import model.ladybug.LadyBug;
import view.Command;
import view.Result;

import java.util.Optional;


/**
 * Command that displays or manipulates the "head" state of the {@link Game}.
 *
 * @author ubpst
 */
public class Head implements Command<Game> {

    private final Identifier identifier;

    public Head(Identifier identifier) {
        this.identifier = identifier;
    }

    @Override
    public Result execute(Game handle) {

        Optional<LadyBug> ladyBugOpt = handle.getBugById(this.identifier);

        if (ladyBugOpt.isEmpty()) {
            return Result.error("there was no bug with ID %d found");
        }

        Node<?> activeNode = handle.getBugActiveNode(ladyBugOpt.get());

        return Result.success(activeNode.getNaming().toString());
    }
}
