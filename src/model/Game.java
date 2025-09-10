package model;

import model.board.GameBoard;
import model.board.Tile;
import model.board.TileType;
import model.decisiontree.DecisionTree;
import model.decisiontree.node.Node;
import model.ladybug.LadyBug;
import model.ladybug.Identifier;
import model.util.Vector2D;
import view.configuration.Configuration;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Represents the main game logic, managing the game board, ladybugs, and their associated decision trees.
 *
 * @author ubpst
 */
public class Game {

    private final GameBoard board;
    private final List<LadyBug> bugsInGame;
    private final Map<LadyBug, DecisionTree> bugsAndTrees;

    /**
     * Creates a new {@code Game} instance using the given configuration.
     *
     * @param config the configuration providing the board, ladybugs, and decision trees
     */
    public Game(Configuration config) {
        this.board = config.gameBoard();
        this.bugsInGame = List.copyOf(config.registeredBugs());

        Map<LadyBug, DecisionTree> helper = new LinkedHashMap<>();
        List<DecisionTree> trees = List.copyOf(config.trees());

        int pairs = Math.min(this.bugsInGame.size(), trees.size());
        for (int i = 0; i < pairs; i++) {
            LadyBug bug = bugsInGame.get(i);
            DecisionTree tree = trees.get(i);

            helper.put(bug, tree);
        }

        this.bugsAndTrees = Collections.unmodifiableMap(helper);
    }

    /**
     * Creates a new {@link GameContext} for this game.
     *
     * @return a new game context bound to this game
     */
    public GameContext context() {
        return new GameContext(this);
    }

    /**
     * Moves the specified ladybug one step forward if the position ahead is inside the board.
     *
     * @param ladyBug the ladybug to move
     * @return true if the move was successful, false if outside the board
     */
    protected boolean moveAhead(LadyBug ladyBug) {
        Vector2D ahead = frontOf(ladyBug);
        Tile aheadTile = board.tileAt(ahead);

        if (aheadTile.isEmptyTile()) {
            ladyBug.updateLocation(ahead);
            return true;
        }

        if (TileType.MUSHROOM.matches(aheadTile)) {
            Vector2D pushTo = ahead.sum(ladyBug.getFacing().delta());

            if (!board.isInside(pushTo) || hasLadybugAt(pushTo)) {
                return false;
            }

            Tile pushToTile = board.tileAt(pushTo);
            if (!pushToTile.isEmptyTile()) {
                return false;
            }

            board.setTileAt(pushTo, new Tile(TileType.MUSHROOM));
            board.setTileAt(ahead, new Tile(TileType.EMPTY));
            ladyBug.updateLocation(ahead);
            return true;
        }

        return false;
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
        return true;
    }

    /**
     * Attempts to place a leaf in front of the given ladybug, if it is carrying one.
     *
     * @param bug the ladybug attempting to place a leaf
     * @return true if the leaf was successfully placed, false otherwise
     */
    public boolean placeLeaf(LadyBug bug) {
        if (!board.isInside(bug.positionAhead())) {
            return false;
        }

        Tile tile = board.tileAt(bug.positionAhead());
        if (!TileType.EMPTY.matches(tile)) {
            return false;
        }

        board.setTileAt(bug.positionAhead(), new Tile(TileType.LEAF));
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
     * Returns the decision tree associated with the given ladybug.
     *
     * @param ladyBug the ladybug whose decision tree should be retrieved
     * @return the {@link DecisionTree} associated with the specified ladybug,
     *         or {@code null} if the ladybug has no assigned tree
     */
    public DecisionTree getBugDecisionTree(LadyBug ladyBug) {
        return this.bugsAndTrees.get(ladyBug);
    }

    /**
     * Resets the decision tree associated with the given ladybug to its initial state.
     *
     * @param ladyBug the {@link LadyBug} whose decision tree should be reset
     */
    public void resetBugTree(LadyBug ladyBug) {
        this.bugsAndTrees.get(ladyBug).resetTree();
    }

    /**
     * Returns the root node of the decision tree associated with the given ladybug.
     *
     * @param ladyBug the ladybug whose decision tree root should be retrieved
     * @return the root {@link Node} of the specified ladybug's decision tree
     */
    public Node<?> getBugActiveNode(LadyBug ladyBug) {
        return this.bugsAndTrees.get(ladyBug).getActiveNode();
    }

    /**
     * Returns all ladybugs in the game paired with their corresponding decision trees.
     *
     * @return a set of {@link Map.Entry} where the key is a {@link LadyBug}
     *         and the value is its associated {@link DecisionTree}
     */
    public Set<Map.Entry<LadyBug, DecisionTree>> getBugsAndTrees() {
        return bugsAndTrees.entrySet();
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

    /**
     * Checks if any ladybug in the game is currently located at the given position.
     *
     * @param position the position to check for a ladybug
     * @return {@code true} if a ladybug is at the given position, {@code false} otherwise
     */
    public boolean hasLadybugAt(Vector2D position) {
        for (LadyBug ladyBug : this.bugsInGame) {
            if (ladyBug.getLocation().equals(position)) {
                return true;
            }
        }
        return false;
    }


    private static Vector2D frontOf(LadyBug bug) {
        return bug.getLocation().sum(bug.getFacing().delta());
    }
}
