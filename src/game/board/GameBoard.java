package game.board;

import game.Tile;
import game.value.Vector2D;

/**
 * Represents the game board composed of a grid of {@link Tile}s.
 *
 * @author ubpst
 */
public class GameBoard implements BoardView {

    private final Tile[][] grid;
    private final int width;
    private final int height;

    /**
     * Creates a new game board by copying the given grid of tiles.
     *
     * @param grid a 2D array of tiles representing the initial board state
     */
    public GameBoard(Tile[][] grid) {
        this.height = grid.length;
        this.width = grid[0].length;
        this.grid = new Tile[grid.length][];

        for (int i = 0; i < grid.length; i++) {
            this.grid[i] = grid[i].clone();
        }
    }

    /**
     * Sets a new tile at the specified position on the board.
     *
     * @param position the location where the tile should be placed
     * @param newTile the new tile to set
     */
    public void setTileAt(Vector2D position, Tile newTile) {
        this.grid[position.vertical()][position.horizontal()] = newTile;
    }

    @Override
    public boolean isInside(Vector2D location) {
        boolean validRow = location.vertical() >= 0 && location.vertical() < this.height;
        boolean validCol = location.horizontal() >= 0 && location.horizontal() < this.width;
        return validRow && validCol;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public Tile getTileAt(Vector2D location) {
        return this.grid[location.vertical()][location.horizontal()];
    }
}
