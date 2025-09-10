package model.board;

/**
 * Represents a single tile on the game board.
 *
 * @author ubpst
 */

public record Tile(TileType tileType) {

    /**
     * Checks whether this tile represents an empty cell.
     *
     * @return true if the tile type is {@link TileType#EMPTY}, false otherwise
     */
    public boolean isEmptyTile() {
        return this.tileType == TileType.EMPTY;
    }

    /**
     * Returns the character symbol representing this tile, as defined by its {@link TileType}.
     *
     * @return the symbol of the tile
     */
    public char symbol() {
        return this.tileType.toChar();
    }
}
