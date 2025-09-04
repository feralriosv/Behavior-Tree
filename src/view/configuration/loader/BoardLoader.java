package view.configuration.loader;

import model.board.GameBoard;
import model.ladybug.Facing;
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

    private static final String LINES_UNIFORMITY_ERROR = "lines are not uniform";

    @Override
    public GameBoard load(List<String> lines) throws LoadingException {
        if (!hasUniformLength(lines)) {
            throw new LoadingException(LINES_UNIFORMITY_ERROR);
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

    private boolean hasUniformLength(List<String> lines) {
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).length() != lines.getFirst().length()) {
                return false;
            }
        }
        return true;
    }
}
