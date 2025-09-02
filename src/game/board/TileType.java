package game.board;

import java.util.Optional;


/**
 * Represents the different types of tiles that can appear on the game board.
 *
 * @author ubpst
 */
public enum TileType {
    /** A tree tile. */
    TREE('#'),
    /** A leaf tile. */
    LEAF('*'),
    /** A mushroom tile. */
    MUSHROOM('o'),
    /** An empty tile. */
    EMPTY('.');

    private final char representation;

    /**
     * Creates a new tile type with the given character representation.
     *
     * @param representation the character symbol representing this tile type
     */
    TileType(char representation) {
        this.representation = representation;
    }

    /**
     * Returns the character representation of this tile type.
     *
     * @return the character symbol for this tile type
     */
    public char toChar() {
        return representation;
    }

    /**
     * Checks if the given tile has this tile type.
     *
     * @param tile the tile to compare
     * @return true if the tile has the same type, false otherwise
     */
    public boolean matches(Tile tile) {
        return tile.getTileType() == this;
    }

    /**
     * Converts a character into the corresponding {@link TileType}, if one exists.
     *
     * @param character the character to match
     * @return an {@link Optional} containing the matching tile type, or empty if none matches
     */
    public static Optional<TileType> fromChar(char character) {
        for (TileType tileType : values()) {
            if (tileType.representation == character) {
                return Optional.of(tileType);
            }
        }
        return Optional.empty();
    }
}
