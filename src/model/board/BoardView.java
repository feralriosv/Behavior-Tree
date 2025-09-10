package model.board;

import model.util.Vector2D;

/**
 * Read-only view of a game board.
 * Provides access to board dimensions, tile information, and boundary checks without allowing modification.
 *
 * @author ubpst
 */
public interface BoardView {
    /**
     * Returns the number of columns of the board.
     *
     * @return the width of the board in tiles
     */
    int width();

    /**
     * Returns the number of rows of the board.
     *
     * @return the height of the board in tiles
     */
    int height();

    /**
     * Retrieves the {@link Tile} located at the given coordinates.
     *
     * @param location the {@link Vector2D} representing the coordinates to query
     * @return the {@link Tile} present at the specified location
     */
    Tile tileAt(Vector2D location);

    /**
     * Checks if the given location lies within the bounds of the board.
     *
     * @param location the coordinates to check
     * @return {@code true} if the location is valid inside the board, {@code false} otherwise
     */
    boolean isInside(Vector2D location);

    /**
     * Checks whether the specified location is considered empty.
     *
     * @param location the coordinates to check
     * @return {@code true} if the location is empty, {@code false} otherwise
     */
    boolean isEmpty(Vector2D location);
}
