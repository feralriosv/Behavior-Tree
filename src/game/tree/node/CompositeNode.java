package game.tree.node;

import game.GameContext;
import game.TickResult;
import game.TickState;
import game.value.NodeIdentifier;

import java.util.Iterator;

/**
 * Node representing a composite type (Fallback, Sequence, Parallel) in a behavior trees.
 *
 * @author ubpst
 */
public class CompositeNode extends Node<CompositeType> {

    private final int parameter;

    /**
     * Constructs a new composite node with an explicit parameter.
     *
     * @param nodeId the identifier of this node
     * @param nodeType the composite type (Fallback, Sequence, Parallel)
     * @param parameter additional parameter (e.g. M for Parallel), 0 if unused
     */
    public CompositeNode(NodeIdentifier nodeId, CompositeType nodeType, int parameter) {
        super(nodeId, nodeType);
        this.parameter = parameter;
    }

    /**
     * Constructs a new composite node with no additional parameter.
     *
     * @param nodeId the identifier of this node
     * @param type   the composite type
     */
    public CompositeNode(NodeIdentifier nodeId, CompositeType type) {
        super(nodeId, type);
        this.parameter = 0;
    }

    @Override
    public TickResult tick(GameContext context) {
        TickResult entry = new TickResult(TickState.ENTRY, this);
        context.logResult(entry);

        TickState out = getNodeType().runStrategy(context, getChildren(), this.parameter);

        if (out != TickState.ENTRY) {
            TickResult done = new TickResult(out, this);
            context.logResult(done);
            return done;
        }

        return entry;
    }

    @Override
    public Iterator<Node<?>> iterator() {
        return getChildren().iterator();
    }
}
