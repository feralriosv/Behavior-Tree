package view.command;


import model.Game;
import model.decisiontree.node.Node;
import view.finder.BugFinder;
import view.finder.UnfoundedBugException;
import model.ladybug.Identifier;
import model.ladybug.LadyBug;
import view.Command;
import view.Result;

/**
 * Command that displays or manipulates the "head" state of the {@link Game}.
 *
 * @author ubpst
 */
public class Head implements Command<Game> {

    private final Identifier identifier;

    /**
     * Creates a new {@code Head} command for the specified ladybug identifier.
     *
     * @param identifier the unique {@link Identifier} of the ladybug whose active head node
     *                   will be queried or manipulated by this command
     */
    public Head(Identifier identifier) {
        this.identifier = identifier;
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

        Node<?> activeNode = handle.getBugActiveNode(ladyBug);
        return Result.success(activeNode.getNaming().toString());
    }
}
