package model;

import model.board.GameBoard;
import model.board.Tile;
import model.board.TileType;
import model.decisiontree.DecisionTree;
import model.decisiontree.TickResult;
import model.ladybug.LadyBug;
import model.ladybug.Identifier;
import model.ladybug.Vector2D;
import view.configuration.Configuration;

import java.util.*;

/**
 * Represents the main game logic, managing the game board, ladybugs, and their associated decision trees.
 * A {@code Game} instance is initialized with a {@link Configuration} which provides the board,
 * the registered ladybugs, and their decision trees.
 *
 * @author ubpst
 */
public class Game {

    private final GameBoard board;
    private final GameContext context;
    private final List<LadyBug> bugsInGame;
    private final Map<LadyBug, DecisionTree> bugsAndTrees;

    /**
     * Creates a new {@code Game} instance using the given configuration.
     * Initializes the game board, registered ladybugs, and their decision trees.
     *
     * @param config the configuration providing the board, ladybugs, and decision trees
     */
    public Game(Configuration config) {
        this.board = config.getGameBoard();
        this.bugsInGame = List.copyOf(config.getRegisteredBugs());
        this.context = new GameContext(this);

        Map<LadyBug, DecisionTree> helper = new LinkedHashMap<>();
        List<DecisionTree> trees = List.copyOf(config.getTrees());

        int pairs = Math.min(this.bugsInGame.size(), trees.size());
        for (int i = 0; i < pairs; i++) {
            LadyBug bug = bugsInGame.get(i);
            DecisionTree tree = trees.get(i);

            helper.put(bug, tree);
        }

        this.bugsAndTrees = Collections.unmodifiableMap(helper);
    }

    /**
     * Returns this game's shared {@link GameContext}. The context is reused across ticks.
     *
     * @return the shared game context bound to this game
     */
    public Map<LadyBug, List<TickResult>> tickBugsOnce() {
        Map<LadyBug, List<TickResult>> resultsByBug = new LinkedHashMap<>();

        for (Map.Entry<LadyBug, DecisionTree> entry : this.bugsAndTrees.entrySet()) {
            LadyBug bug = entry.getKey();
            DecisionTree tree = entry.getValue();
            List<TickResult> results = tree.tick(context(), bug);
            resultsByBug.put(bug, results);
        }

        return resultsByBug;
    }

    /**
     * Creates a new {@link GameContext} for this game.
     *
     * @return a new game context bound to this game
     */
    public GameContext context() {
        return this.context;
    }

    /**
     * Moves the specified ladybug one step forward if the position ahead is inside the board.
     *
     * @param ladyBug the ladybug to move
     * @return true if the move was successful, false if outside the board
     */
    protected boolean moveAhead(LadyBug ladyBug) {
        Vector2D aheadPosition = ladyBug.positionAhead();

        if (this.board.isInside(aheadPosition)) {
            ladyBug.setLocation(aheadPosition);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Attempts to let the given ladybug pick up a leaf from the tile in front of it.
     *
     * @param bug the ladybug attempting to take a leaf
     * @return true if a leaf was successfully taken, false otherwise
     */
    protected boolean takeLeaf(LadyBug bug) {
        Vector2D aheadPosition = frontOf(bug);
        if (!board.isInside(aheadPosition)) {
            return false;
        }
        Tile tile = board.tileAt(aheadPosition);
        if (!TileType.LEAF.matches(tile)) {
            return false;
        }

        board.setTileAt(aheadPosition, new Tile(TileType.EMPTY));
        bug.setCarryingLeaf(true);
        return true;
    }

    /**
     * Attempts to place a leaf in front of the given ladybug, if it is carrying one.
     *
     * @param bug the ladybug attempting to place a leaf
     * @return true if the leaf was successfully placed, false otherwise
     */
    public boolean placeLeaf(LadyBug bug) {
        if (!bug.isCarryingLeaf()) {
            return false;
        }
        Vector2D aheadPosition = frontOf(bug);
        if (!board.isInside(aheadPosition)) {
            return false;
        }
        Tile tile = board.tileAt(aheadPosition);
        if (!TileType.EMPTY.matches(tile)) {
            return false;
        }

        board.setTileAt(aheadPosition, new Tile(TileType.LEAF));
        bug.setCarryingLeaf(false);
        return true;
    }

    /**
     * Returns the game board.
     *
     * @return the game board
     */
    public GameBoard getBoard() {
        return this.board;
    }

    /**
     * Returns the list of ladybugs currently in the game.
     *
     * @return unmodifiable list of ladybugs in the game
     */
    public List<LadyBug> getBugsInGame() {
        return this.bugsInGame;
    }

    /**
     * Finds a ladybug in the game by its identifier.
     *
     * @param identifier the unique identifier of the ladybug
     * @return an {@link Optional} containing the ladybug if found, otherwise empty
     */
    public Optional<LadyBug> getBugById(Identifier identifier) {
        for (LadyBug ladyBug : bugsInGame) {
            if (ladyBug.getId().equals(identifier)) {
                return Optional.of(ladyBug);
            }
        }
        return Optional.empty();
    }

    private static Vector2D frontOf(LadyBug bug) {
        return bug.getLocation().sum(bug.getFacing().delta());
    }
}
