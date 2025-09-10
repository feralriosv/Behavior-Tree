package view.command;


import model.Game;
import model.node.Node;
import model.util.BugFinder;
import model.util.UnfoundedBugException;
import model.ladybug.BugIdentifier;
import model.ladybug.LadyBug;
import view.Command;
import view.Result;

/**
 * Command that displays or manipulates the "head" state of the {@link Game}.
 *
 * @author ubpst
 */
public class Head implements Command<Game> {

    private final BugIdentifier bugIdentifier;

    /**
     * Creates a new {@code Head} command for the specified ladybug identifier.
     *
     * @param bugIdentifier the unique {@link BugIdentifier} of the ladybug whose active head node
     *                   will be queried or manipulated by this command
     */
    public Head(BugIdentifier bugIdentifier) {
        this.bugIdentifier = bugIdentifier;
    }

    @Override
    public Result execute(Game handle) {
        BugFinder finder = new BugFinder(handle);
        LadyBug ladyBug;

        try {
            ladyBug = finder.findById(this.bugIdentifier);
        } catch (UnfoundedBugException e) {
            return Result.error(e.getMessage());
        }

        Node<?> activeNode = handle.getBugActiveNode(ladyBug);
        return Result.success(activeNode.getNodeNaming().toString());
    }
}
