package model.decisiontree.node;

public interface LocalPointer {
    /**
     * Advances the tick pointer to the next child node, if any remain.
     */
    int localPointer();
    /**
     * Resets the tick pointer to the beginning.
     */
    boolean ticksCompleted();

    void advancePointer();
    void resetPointer();
}
