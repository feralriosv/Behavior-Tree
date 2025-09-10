package model.node;

/**
 * Defines the possible states a node can report during the execution (tick) of a decision tree.
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
    /** The node is paused and waiting, expected to eventually succeed. */
    WAITS_SUCCESS,
    /** The node is paused and waiting, expected to eventually fail. */
    WAITS_FAILURE;
}
