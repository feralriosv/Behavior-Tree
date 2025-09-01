package game.board;

import game.Tile;
import game.value.Vector2D;

/**
 * A read-only view of a {@link GameBoard}.
 * This class delegates all method calls to the underlying {@link GameBoard}.
 *
 * @author ubpst
 */
public class ReadOnlyBoard implements BoardView {

    private final GameBoard delegate;

    /**
     * Creates a new read-only view of the given {@link GameBoard}.
     *
     * @param delegate the game board to wrap with a read-only view
     */
    public ReadOnlyBoard(GameBoard delegate) {
        this.delegate = delegate;
    }

    @Override
    public int getWidth() {
        return this.delegate.getWidth();
    }

    @Override
    public int getHeight() {
        return this.delegate.getHeight();
    }

    @Override
    public Tile getTileAt(Vector2D location) {
        return this.delegate.getTileAt(location);
    }

    @Override
    public boolean isInside(Vector2D location) {
        return this.delegate.isInside(location);
    }
}
