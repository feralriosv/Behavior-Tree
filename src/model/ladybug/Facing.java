package model.ladybug;

import model.util.Vector2D;

/**
 * Represents the four possible facing directions of a {@link LadyBug} on the board.
 *
 * @author ubpst
 */
public enum Facing {
    /** Facing north (upward). */
    NORTH('^', new Vector2D(-1, 0)),
    /** Facing east (right). */
    EAST('>', new Vector2D(0, 1)),
    /** Facing south (downward). */
    SOUTH('v', new Vector2D(1, 0)),
    /** Facing west (left). */
    WEST('<', new Vector2D(0, -1));

    private final char symbol;
    private final Vector2D delta;

    Facing(char symbol, Vector2D delta) {
        this.symbol = symbol;
        this.delta = delta;
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
        return this.delta;
    }


    /**
     * Returns the facing that results from turning left (counter-clockwise) relative to this facing.
     *
     * @return the facing after turning left
     */
    public Facing left() {
        return switch (this) {
            case NORTH -> WEST;
            case WEST  -> SOUTH;
            case SOUTH -> EAST;
            case EAST  -> NORTH;
        };
    }

    /**
     * Returns the facing that results from turning right (clockwise) relative to this facing.
     *
     * @return the facing after turning right
     */
    public Facing right() {
        return switch (this) {
            case NORTH -> EAST;
            case EAST  -> SOUTH;
            case SOUTH -> WEST;
            case WEST  -> NORTH;
        };
    }
}
