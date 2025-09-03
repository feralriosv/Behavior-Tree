package model.decisiontree.node;

import model.GameContext;
import model.decisiontree.TickState;

import java.util.Optional;

/**
 * Defines the types of leaf nodes in the decision tree.
 * Each {@code LeafType} corresponds to an action or condition.
 *
 * @author ubpst
 */
public enum LeafType implements NodeType<LeafNode> {

    /** Action: Move forward one step if possible. */
    MOVE("move", (context, self) -> LeafType.executeAndLog(context, self, context.move())),
    /** Action: Turn left. */
    TURN_LEFT("turnLeft", (context, self) -> LeafType.executeAndLog(context, self, context.turnLeft())),
    /** Action: Turn right. */
    TURN_RIGHT("turnRight", (context, self) -> LeafType.executeAndLog(context, self, context.turnRight())),
    /** Action: Attempt to take a leaf in front. */
    TAKE_LEAF("takeLeaf", (context, self) -> LeafType.executeAndLog(context, self, context.takeLeaf())),
    /** Action: Place a leaf in front. */
    PLACE_LEAF("placeLeaf", (context, self) -> LeafType.executeAndLog(context, self, context.placeLeaf())),

    /** Condition: Check if a tree is directly in front. */
    TREE_FRONT("treeFront", (context, self) -> LeafType.executeAndLog(context, self, context.isTreeFront())),
    /** Condition: Check if a leaf is directly in front. */
    LEAF_FRONT("leafFront", (context, self) -> LeafType.executeAndLog(context, self, context.isLeafFront())),
    /** Condition: Check if a mushroom is directly in front. */
    MUSHROOM_FRONT("mushroomFront", (context, self) -> LeafType.executeAndLog(context, self, context.isMushroomFront())),
    /** Condition: Check if the ladybug is at the edge of the board. */
    AT_EDGE("atEdge", (context, self) -> LeafType.executeAndLog(context, self, context.isAtEdge()));

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

    @Override
    public void behavior(GameContext context, LeafNode self) {
        this.strategy.run(context, self);
    }

    private static void executeAndLog(GameContext context, LeafNode self, boolean actionResult) {
        TickState state = actionResult ? TickState.SUCCESS : TickState.FAILURE;
        self.setLastState(state);

        if (LeafType.isActionType(self.getNodeType())) {
            context.markAction();
        }
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
        return type != null && (type.equals(MOVE)
                        || type.equals(TURN_LEFT)
                        || type.equals(TURN_RIGHT)
                        || type.equals(TAKE_LEAF)
                        || type.equals(PLACE_LEAF)
                );
    }

    @Override
    public String label() {
        return this.label;
    }
}
