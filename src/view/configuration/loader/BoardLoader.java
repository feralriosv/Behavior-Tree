package view.configuration.loader;

import model.board.GameBoard;
import model.board.Tile;
import model.board.TileType;

import java.util.List;
import java.util.Optional;

/**
 * Loader responsible for constructing a {@link GameBoard} from a list of text lines.
 *
 * @author ubpst
 */
public class BoardLoader implements Loader<GameBoard> {

    private static final String BOARD_ASCII_CORNER = "+";
    private static final String BOARD_ASCII_VERTICAL = "|";


    @Override
    public GameBoard load(List<String> lines) {
        if (hasAlreadyBorders(lines)) {
            return GameBoard.unplayableBoard();
        }

        Tile[][] grid = new Tile[lines.size()][lines.getFirst().length()];

        int rows = lines.size();
        int cols = lines.getFirst().length();

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < cols; column++) {
                char symbol = lines.get(row).charAt(column);
                Optional<TileType> tileTypeOpt = TileType.fromChar(symbol);

                if (tileTypeOpt.isPresent()) {
                    grid[row][column] = new Tile(tileTypeOpt.get());
                } else {
                    grid[row][column] = new Tile(TileType.EMPTY);
                }
            }
        }

        return new GameBoard(grid);
    }

    private static boolean hasAlreadyBorders(List<String> lines) {
        if (lines.isEmpty()) {
            return false;
        }

        String first = lines.getFirst();
        String last  = lines.getLast();

        if ((first.startsWith(BOARD_ASCII_CORNER) && first.endsWith(BOARD_ASCII_CORNER))
                || (last.startsWith(BOARD_ASCII_CORNER) && last.endsWith(BOARD_ASCII_CORNER))) {
            return true;
        }

        for (String line : lines) {
            if (line.startsWith(BOARD_ASCII_VERTICAL) && line.endsWith(BOARD_ASCII_VERTICAL)) {
                return true;
            }
        }

        return false;
    }
}
