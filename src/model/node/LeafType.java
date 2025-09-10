package model.node;

import model.GameContext;
import model.ladybug.Vector2D;

import java.util.Optional;

/**
 * Enumeration of all possible leaf node types in the decision tree.
 *
 * @author ubpst
 */
public enum LeafType implements NodeType<LeafNode> {

    /** Action: Move forward one step if possible. */
    MOVE("move", (context, self) -> evaluate(context, self, context.move())),
    /** Action: Turn left. */
    TURN_LEFT("turnLeft", (context, self) -> evaluate(context, self, context.turnLeft())),
    /** Action: Turn right. */
    TURN_RIGHT("turnRight", (context, self) -> evaluate(context, self, context.turnRight())),
    /** Action: Attempt to take a leaf in front. */
    TAKE_LEAF("takeLeaf", (context, self) -> evaluate(context, self, context.takeLeaf())),
    /** Action: Place a leaf in front. */
    PLACE_LEAF("placeLeaf", (context, self) -> evaluate(context, self, context.placeLeaf())),
    /** Action: Teleports the ladybug to the specified target coordinates, ignoring any intermediate obstacles. */
    FLY("fly", ((context, self) -> {
        Vector2D goal = self.getGoal();
        boolean ok = context.fly(goal);
        return evaluate(context, self, ok);
    })),

    /** Condition: Check if a tree is directly in front. */
    TREE_FRONT("treeFront", (context, self) -> evaluate(context, self, context.isTreeFront())),
    /** Condition: Check if a leaf is directly in front. */
    LEAF_FRONT("leafFront", (context, self) -> evaluate(context, self, context.isLeafFront())),
    /** Condition: Check if a mushroom is directly in front. */
    MUSHROOM_FRONT("mushroomFront", (context, self) -> evaluate(context, self, context.isMushroomFront())),
    /** Condition: Check if the ladybug is at the edge of the board. */
    AT_EDGE("atEdge", (context, self) -> evaluate(context, self, context.isAtEdge())),
    /** Condition: Checks if a path of empty tiles exists between two coordinates. */
    EXISTS_PATH("existsPath", (context, self) -> {
        Vector2D start = self.getStart();
        Vector2D goal  = self.getGoal();
        boolean ok = (start != null)
                ? context.existsPath(start, goal)
                : context.existsPath(goal);
        return evaluate(context, self, ok);
    });

    private final String label;
    private final NodeBehavior<LeafNode> strategy;

    /**
     * Creates a new leaf type with the given symbol and strategy.
     *
     * @param label the string label representing this leaf type
     * @param strategy the behavior to execute when this leaf type is run
     */
    LeafType(String label, NodeBehavior<LeafNode> strategy) {
        this.label = label;
        this.strategy = strategy;
    }

    /**
     * Parses a line of text into a {@link LeafType}, if possible.
     *
     * @param line the label string to parse
     * @return an {@link Optional} containing the matching leaf type, or empty if none matches
     */
    public static Optional<LeafType> fromLine(String line) {
        for (LeafType type : values()) {
            if (type.label.equals(line)) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }

    /**
     * Determines whether the given leaf type represents an action (as opposed to a condition).
     *
     * @param type the leaf type to check
     * @return true if the leaf type is an action, false otherwise
     */
    public static boolean isActionType(LeafType type) {
        return type.equals(MOVE)
                || type.equals(TURN_LEFT)
                || type.equals(TURN_RIGHT)
                || type.equals(TAKE_LEAF)
                || type.equals(PLACE_LEAF)
                || type.equals(FLY);
    }

    private static TickState evaluate(GameContext context, LeafNode self, boolean actionResult) {
        TickState state = actionResult ? TickState.SUCCESS : TickState.FAILURE;
        if (isActionType(self.getNodeType())) {
            context.markAction();
        }
        return state;
    }

    @Override
    public String label() {
        return this.label;
    }

    @Override
    public TickState behavior(GameContext context, LeafNode self) {
        return this.strategy.run(context, self);
    }
}
