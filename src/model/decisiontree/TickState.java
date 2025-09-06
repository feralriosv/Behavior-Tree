package model.decisiontree;

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
    ENTRY,
    /** Used internally by composite nodes when it should only emit the SUCCESS on the next tick cycle. */
    WAITS_SUCCESS,
    /** Used internally by composite nodes when it should only emit the FAILURE on the next tick cycle. */
    WAITS_FAILURE;
}
