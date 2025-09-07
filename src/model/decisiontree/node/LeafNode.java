package model.decisiontree.node;

import model.GameContext;
import model.decisiontree.TickState;
import model.util.Vector2D;
import view.util.Vector2DDecorator;

import java.util.Collections;
import java.util.Iterator;

/**
 * Represents a leaf node in the decision tree.
 * A leaf node contains an action or condition ({@link LeafType}) that can be executed
 *
 * @author ubpst
 */
public class LeafNode extends Node<LeafType> {

    private static final String LEAF_GOAL_FORMAT = "%s %s";
    private static final String LEAF_START_GOAL_FORMAT = "%s %s %s";

    private final Vector2D start;
    private final Vector2D goal;

    /**
     * Creates a new leaf node with the given identifier, type, and optional start and goal coordinates.
     *
     * @param nodeId  the unique identifier of this node
     * @param nodeType the type of the leaf, defining its behavior
     * @param start   the starting coordinate relevant to the node's behavior, or {@code null} if not required
     * @param goal    the goal coordinate relevant to the node's behavior, or {@code null} if not required
     */
    public LeafNode(Naming nodeId, LeafType nodeType, Vector2D start, Vector2D goal) {
        super(nodeId, nodeType);
        this.start = start;
        this.goal = goal;
    }

    /**
     * Creates a new leaf node with the given identifier and type.
     *
     * @param nodeId the unique identifier of this node
     * @param nodeType the type of the leaf, defining its behavior
     */
    public LeafNode(Naming nodeId, LeafType nodeType) {
        this(nodeId, nodeType, null, null);
    }

    /**
     * Returns the starting position relevant to this leaf node's behavior.
     *
     * @return the start position, or null if not set
     */
    protected Vector2D getStart() {
        return this.start;
    }

    /**
     * Returns the goal position relevant to this leaf node's behavior.
     *
     * @return the goal position, or null if not set
     */
    protected Vector2D getGoal() {
        return this.goal;
    }

    @Override
    public void tick(GameContext context) {
        super.tick(context);
        TickState tickState = getNodeType().behavior(context, this);
        this.saveState(context, tickState);
    }

    @Override
    public void handleSkippedChildren(Node<?> target) {

    }

    @Override
    public Iterator<Node<?>> iterator() {
        return Collections.emptyIterator();
    }


    @Override
    public boolean addChild(Node<?> child) {
        return false;
    }

    @Override
    public void insertChildAt(int index, Node<?> child) {

    }

    @Override
    protected void handleSkip() {

    }

    @Override
    public String toString() {
        String base = super.toString();

        if (getNodeType() == LeafType.FLY) {
            Vector2DDecorator startDeco = new Vector2DDecorator(getGoal());
            return LEAF_GOAL_FORMAT.formatted(base, startDeco.asCsv());
        }

        if (getNodeType() == LeafType.EXISTS_PATH) {
            if (this.start != null && this.goal != null) {
                Vector2DDecorator startDeco = new Vector2DDecorator(this.start);
                Vector2DDecorator goalDeco = new Vector2DDecorator(this.goal);
                return LEAF_START_GOAL_FORMAT.formatted(base, startDeco.asCsv(), goalDeco.asCsv());
            } else if (this.goal != null) {
                Vector2DDecorator startDeco = new Vector2DDecorator(getGoal());
                return LEAF_GOAL_FORMAT.formatted(base, startDeco.asCsv());
            }
        }

        return base;
    }
}
