package game.board;

import game.Tile;
import game.value.Vector2D;

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
    int getWidth();

    /**
     * Returns the number of rows of the board.
     *
     * @return the height of the board in tiles
     */
    int getHeight();

    /**
     * Returns the tile at the specified location.
     *
     * @param location the coordinates to query
     * @return the {@link Tile} at the given location
     * @throws IndexOutOfBoundsException if the location is outside the board
     */
    Tile getTileAt(Vector2D location);

    /**
     * Checks if the given location lies within the bounds of the board.
     *
     * @param location the coordinates to check
     * @return {@code true} if the location is valid inside the board, {@code false} otherwise
     */
    boolean isInside(Vector2D location);
}
