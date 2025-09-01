package game;

/**
 * Represents a single tile on the game board.
 *
 * @author ubpst
 */

public class Tile {

    private final TileType tileType;

    /**
     * Creates a new tile with the specified type.
     *
     * @param tileType the type of the tile
     */
    public Tile(TileType tileType) {
        this.tileType = tileType;
    }

    /**
     * Returns the type of this tile.
     *
     * @return the tile type
     */
    public TileType getTileType() {
        return tileType;
    }

    /**
     * Returns the character symbol representing this tile, as defined by its {@link TileType}.
     *
     * @return the symbol of the tile
     */
    public char getSymbol() {
        return this.tileType.toChar();
    }
}
