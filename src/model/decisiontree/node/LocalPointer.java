package model.decisiontree.node;

public interface LocalPointer {
    int localPointer();
    boolean ticksCompleted();
    void advancePointer();
    void resetPointer();
}
