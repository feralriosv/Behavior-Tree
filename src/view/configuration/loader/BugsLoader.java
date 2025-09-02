package view.configuration.loader;

import game.LadyBug;
import game.Facing;
import game.value.Identifier;
import game.value.Vector2D;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Loader that scans board lines for ladybug symbols and constructs {@link LadyBug} instances.
 *
 * @author ubpst
 */
public class BugsLoader implements Loader<List<LadyBug>> {

    private final List<LadyBug> loadedBugs;

    /**
     * Creates a new loader with an empty internal list of ladybugs.
     */
    public BugsLoader() {
        this.loadedBugs = new ArrayList<>();
    }

    /**
     * Loads ladybugs from the given board lines by scanning for facing symbols.
     *
     * @param lines the textual board representation
     * @return an unmodifiable list of loaded {@link LadyBug} instances
     * @throws LoadingException if no ladybugs are found
     */
    @Override
    public List<LadyBug> load(List<String> lines) throws LoadingException {
        int rows = lines.size();
        int cols = lines.getFirst().length();

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < cols; column++) {
                char symbol = lines.get(row).charAt(column);
                if (Facing.isLadyBugFacing(symbol)) {
                    processLadybug(symbol, row, column);
                }
            }
        }

        if (this.loadedBugs.isEmpty()) {
            throw new LoadingException("no ladybug could be loaded");
        }

        return Collections.unmodifiableList(this.loadedBugs);
    }

    private void processLadybug(char facingSymbol, int vertical, int horizontal) throws LoadingException {
        Vector2D location = new Vector2D(vertical, horizontal);
        Identifier identifier = new Identifier(loadedBugs.size() + 1);
        Facing facing = Facing.fromChar(facingSymbol);
        loadedBugs.add(new LadyBug(identifier, location, facing));
    }
}
