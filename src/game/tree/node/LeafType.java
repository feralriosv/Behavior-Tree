package game.tree.node;

import game.GameContext;
import game.TickState;

import java.util.Optional;

/**
 * Defines the types of leaf nodes in the decision tree.
 * Each {@code LeafType} corresponds to an action or condition.
 *
 * @author ubpst
 */
public enum LeafType implements NodeType {

    /** Action: Move forward one step if possible. */
    MOVE("move", context -> context.move() ? TickState.SUCCESS : TickState.FAILURE),
    /** Action: Turn left. */
    TURN_LEFT("turnLeft", context -> context.turnLeft() ? TickState.SUCCESS : TickState.FAILURE),
    /** Action: Turn right. */
    TURN_RIGHT("turnRight", context -> context.turnRight() ? TickState.SUCCESS : TickState.FAILURE),
    /** Action: Attempt to take a leaf in front. */
    TAKE_LEAF("takeLeaf", context -> context.takeLeaf() ? TickState.SUCCESS : TickState.FAILURE),
    /** Action: Attempt to take a leaf in front. */
    PLACE_LEAF("placeLeaf", context -> context.placeLeaf() ? TickState.SUCCESS : TickState.FAILURE),

    /** Condition: Check if a tree is directly in front. */
    TREE_FRONT("treeFront", context -> context.isTreeFront() ? TickState.SUCCESS : TickState.FAILURE),
    /** Condition: Check if a leaf is directly in front. */
    LEAF_FRONT("leafFront", context -> context.isLeafFront() ? TickState.SUCCESS : TickState.FAILURE),
    /** Condition: Check if a mushroom is directly in front. */
    MUSHROOM_FRONT("mushroomFront", context -> context.isMushroomFront() ? TickState.SUCCESS : TickState.FAILURE),
    /** Condition: Check if the ladybug is at the edge of the board. */
    AT_EDGE("atEdge", context -> context.isAtEdge() ? TickState.SUCCESS : TickState.FAILURE);

    private final String symbol;
    private final LeafStrategy strategy;

    /**
     * Creates a new leaf type with the given symbol and strategy.
     *
     * @param symbol the string label representing this leaf type
     * @param strategy the behavior to execute when this leaf type is run
     */
    LeafType(String symbol, LeafStrategy strategy) {
        this.symbol = symbol;
        this.strategy = strategy;
    }

    @Override
    public String displayLabel() {
        return symbol;
    }

    /**
     * Executes this leaf type's strategy in the given game context.
     *
     * @param context the game context
     * @return the tick state resulting from executing the strategy
     */
    public TickState run(GameContext context) {
        return strategy.run(context);
    }

    /**
     * Parses a line of text into a {@link LeafType}, if possible.
     *
     * @param line the label string to parse
     * @return an {@link Optional} containing the matching leaf type, or empty if none matches
     */
    public static Optional<LeafType> fromLine(String line) {
        for (LeafType type : values()) {
            if (type.symbol.equals(line)) {
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
        return type != null && (type.equals(LeafType.MOVE)
                        || type.equals(LeafType.TURN_LEFT)
                        || type.equals(LeafType.TURN_RIGHT)
                        || type.equals(LeafType.TAKE_LEAF)
                        || type.equals(LeafType.PLACE_LEAF)
                );
    }
}
