package game.decisionTree;

/**
 * Represents the possible states resulting from executing a tick in the decision tree.
 *
 * @author ubpst
 */
public enum TickState {
    /** Indicates that the node executed successfully. */
    SUCCESS,
    /** Indicates that the node execution failed. */
    FAILURE,
    /** Marks the entry point of a tick before execution begins. */
    ENTRY;
}
