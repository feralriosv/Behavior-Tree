package model.board;

import model.ladybug.LadyBug;
import model.ladybug.Vector2D;

import java.util.Collections;
import java.util.List;

/**
 * Utility class to render a textual representation of a {@link BoardView} enclosed by a decorative border.
 *
 * @author ubpst
 */
public final class BoardDisplayer {

    private static final String BOARD_CORNER = "+";
    private static final String BOARD_HORIZONTAL_BORDER = "-";
    private static final String BOARD_VERTICAL_BORDER = "|";

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
                buffer[vertical][horizontal] = boardView.tileAt(new Vector2D(vertical, horizontal)).symbol();
            }
        }

        for (LadyBug bug : bugsOnBoard) {
            Vector2D position = bug.getLocation();
            if (boardView.isInside(position)) {
                buffer[position.vertical()][position.horizontal()] = bug.getFacing().getSymbol();
            }
        }

        return putBorders(buffer);
    }

    private String putBorders(char[][] buffer) {
        StringBuilder decoratedBoard = new StringBuilder();
        decoratedBoard.append(horizontalEdge()).append(System.lineSeparator());

        for (char[] charsRow : buffer) {
            decoratedBoard.append(BOARD_VERTICAL_BORDER);
            for (int col = 0; col < boardView.width(); col++) {
                decoratedBoard.append(charsRow[col]);
            }
            decoratedBoard.append(BOARD_VERTICAL_BORDER).append(System.lineSeparator());
        }

        decoratedBoard.append(horizontalEdge());
        return decoratedBoard.toString();
    }

    private String horizontalEdge() {
        return BOARD_CORNER + BOARD_HORIZONTAL_BORDER.repeat(boardView.width()) + BOARD_CORNER;
    }
}
