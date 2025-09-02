package game;

import game.value.Vector2D;

/**
 * Represents the four possible facing directions of a {@link LadyBug} on the board.
 *
 * @author ubpst
 */
public enum Facing {
    /**
     * Facing north (upward).
     */
    NORTH('^', 0, -1),
    /**
     * Facing east (right).
     */
    EAST('>', 1,  0),
    /**
     * Facing south (downward).
     */
    SOUTH('v', 0,  1),
    /**
     * Facing west (left).
     */
    WEST('<', -1, 0);

    private final char symbol;
    private final int deltaX;
    private final int deltaY;

    Facing(char symbol, int deltaX, int deltaY) {
        this.symbol = symbol;
        this.deltaX = deltaX;
        this.deltaY = deltaY;
    }

    /**
     * Checks if the given character corresponds to a valid ladybug facing symbol.
     *
     * @param symbol the character to check
     * @return true if the symbol matches a facing, false otherwise
     */
    public static boolean isLadyBugFacing(char symbol) {
        for (Facing facing : values()) {
            if (facing.symbol == symbol) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the character symbol associated with this facing.
     *
     * @return the symbol of this facing
     */
    public char getSymbol() {
        return this.symbol;
    }

    /**
     * Returns the facing corresponding to the given character symbol.
     *
     * @param symbol the character symbol
     * @return the corresponding facing, or null if no facing matches
     */
    public static Facing fromChar(char symbol) {
        for (Facing facing : values()) {
            if (facing.symbol == symbol) {
                return facing;
            }
        }

        return null;
    }

    /**
     * Returns the movement vector (delta) associated with this facing.
     *
     * @return a {@link Vector2D} representing the movement direction
     */
    public Vector2D delta() {
        return new Vector2D(deltaX, deltaY);
    }
}
