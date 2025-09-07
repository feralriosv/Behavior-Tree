package view;

import model.ladybug.LadyBug;
import model.ladybug.Facing;
import model.board.Tile;
import model.board.BoardView;
import model.util.Vector2D;

import java.util.Collections;
import java.util.List;

/**
 * Utility class to render a textual representation of a {@link BoardView} enclosed by a decorative border.
 *
 * @author ubpst
 */
public final class BoardDisplayer {

    private static final String CORNER_SYMBOL = "+";
    private static final String HORIZONTAL_BORDER_SYMBOL = "-";
    private static final String VERTICAL_BORDER_SYMBOL = "|";

    private final BoardView boardView;
    private final List<LadyBug> bugsOnBoard;

    /**
     * Constructs a {@code BoardDisplayer} for the given board view and ladybugs.
     *
     * @param boardView   the {@link BoardView} to be displayed
     * @param bugsOnBoard the list of {@link LadyBug} instances currently on the board
     */
    public BoardDisplayer(BoardView boardView, List<LadyBug> bugsOnBoard) {
        this.bugsOnBoard = Collections.unmodifiableList(bugsOnBoard);
        this.boardView = boardView;
    }

    /**
     * Standard Description.
     *
     * @return standard return.
     */
    public String renderBoard() {
        int height = boardView.height();
        int width  = boardView.width();
        char[][] buffer = new char[height][width];

        for (int vertical = 0; vertical < height; vertical++) {
            for (int horizontal = 0; horizontal < width; horizontal++) {
                buffer[vertical][horizontal] = symbolFor(boardView.tileAt(new Vector2D(vertical, horizontal)));
            }
        }

        for (LadyBug bug : bugsOnBoard) {
            Vector2D position = bug.getLocation();
            if (boardView.isInside(position)) {
                buffer[position.vertical()][position.horizontal()] = symbolFor(bug.getFacing());
            }
        }

        return getDecoratedBoard(buffer, width);
    }

    private String getDecoratedBoard(char[][] buffer, int width) {
        StringBuilder decoratedBoard = new StringBuilder();
        decoratedBoard.append(horizontalEdge(width)).append(System.lineSeparator());

        for (char[] charsRow : buffer) {
            decoratedBoard.append(VERTICAL_BORDER_SYMBOL);
            for (int col = 0; col < width; col++) {
                decoratedBoard.append(charsRow[col]);
            }
            decoratedBoard.append(VERTICAL_BORDER_SYMBOL).append(System.lineSeparator());
        }

        decoratedBoard.append(horizontalEdge(width));
        return decoratedBoard.toString();
    }

    private String horizontalEdge(int width) {
        return CORNER_SYMBOL + HORIZONTAL_BORDER_SYMBOL.repeat(width) + CORNER_SYMBOL;
    }

    private static char symbolFor(Tile tile) {
        return tile.getSymbol();
    }

    private static char symbolFor(Facing facing) {
        return facing.getSymbol();
    }
}
