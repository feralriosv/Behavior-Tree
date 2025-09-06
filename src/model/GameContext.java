package model;

import model.board.BoardView;
import model.board.ReadOnlyBoard;
import model.board.Tile;
import model.board.TileType;
import model.decisiontree.TickResult;
import model.ladybug.Facing;
import model.ladybug.LadyBug;
import model.util.Vector2D;
import model.util.PathFinder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * Provides the execution context for the game during a tick.
 *
 * @author ubpst
 */
public class GameContext {
    private final Game game;
    private LadyBug activeLadybug;
    private final BoardView boardView;
    private final List<TickResult> tickLog;
    private boolean actionExecuted;

    /**
     * Creates a new game context bound to the specified game.
     *
     * @param game the game instance this context belongs to
     */
    public GameContext(Game game) {
        this.game = game;
        this.boardView = new ReadOnlyBoard(game.getBoard());
        this.tickLog = new ArrayList<>();
        this.actionExecuted = false;
    }

    /**
     * Starts a new tick for the given active ladybug.
     *
     * @param activeLadybug the ladybug whose actions will be processed in this tick
     */
    public void beginTick(LadyBug activeLadybug) {
        this.tickLog.clear();
        this.activeLadybug = activeLadybug;
        this.actionExecuted = false;
    }

    /**
     * Ends the current tick and returns the list of results generated during it.
     * Resets the active ladybug to null.
     *
     * @return an unmodifiable list of tick results
     */
    public List<TickResult> endTick() {
        List<TickResult> results = List.copyOf(this.tickLog);
        this.activeLadybug = null;
        return results;
    }

    /**
     * Marks that an action has been executed during this tick.
     */
    public void markAction() {
        this.actionExecuted = true;
    }

    /**
     * Determines whether execution should stop because an action was already executed.
     *
     * @return true if an action was executed, false otherwise
     */
    public boolean wasActionExecuted() {
        return this.actionExecuted;
    }

    /**
     * Adds a tick result to the log of the current tick.
     *
     * @param tickResult the result to add
     */
    public void logResult(TickResult tickResult) {
        this.tickLog.add(tickResult);
    }

    /**
     * Checks if the tile directly in front of the active ladybug is outside the board boundaries.
     *
     * @return true if the ladybug is at the edge, false otherwise
     */
    public boolean isAtEdge() {
        return !this.boardView.isInside(location().sum(new Vector2D(-1, 0)))
                || !this.boardView.isInside(location().sum(new Vector2D(1, 0)))
                || !this.boardView.isInside(location().sum(new Vector2D(0, -1)))
                || !this.boardView.isInside(location().sum(new Vector2D(0, 1)));
    }

    /**
     * Checks if there is a leaf tile directly in front of the active ladybug.
     *
     * @return true if the front tile is a leaf, false otherwise
     */
    public boolean isLeafFront() {
        return tileAhead().isPresent() && TileType.LEAF.matches(tileAhead().get());
    }

    /**
     * Checks if there is a tree tile directly in front of the active ladybug.
     *
     * @return true if the front tile is a tree, false otherwise
     */
    public boolean isTreeFront() {
        return tileAhead().isPresent() && TileType.TREE.matches(tileAhead().get());
    }

    /**
     * Checks if there is a mushroom tile directly in front of the active ladybug.
     *
     * @return true if the front tile is a mushroom, false otherwise
     */
    public boolean isMushroomFront() {
        return tileAhead().isPresent() && TileType.MUSHROOM.matches(tileAhead().get());
    }

    /**
     * Attempts to move the active ladybug one step forward.
     *
     * @return true if the ladybug moved successfully, false otherwise
     */
    public boolean move() {
        Vector2D frontPos = location().sum(getLadybugFacing().delta());
        return this.boardView.isInside(frontPos)
                && !isTreeFront()
                && !this.game.hasLadybugAt(frontPos)
                && this.game.moveAhead(this.activeLadybug);

    }

    /**
     * Turns the active ladybug left.
     *
     * @return true if the turn was successful
     */
    public boolean turnLeft() {
        return this.activeLadybug.turnLeft();
    }

    /**
     * Turns the active ladybug right.
     *
     * @return true if the turn was successful
     */
    public boolean turnRight() {
        return this.activeLadybug.turnRight();
    }

    /**
     * Attempts to pick up a leaf in front of the active ladybug.
     *
     * @return true if the leaf was taken successfully, false otherwise
     */
    public boolean takeLeaf() {
        return this.game.takeLeaf(activeLadybug);
    }

    /**
     * Attempts to place a leaf in front of the active ladybug.
     *
     * @return true if the leaf was placed successfully, false otherwise
     */
    public boolean placeLeaf() {
        return this.game.placeLeaf(activeLadybug);
    }

    /**
     * Checks if there is a connected empty path from the active ladybug's current
     * position to the given target cell.
     *
     * @param goal target board position (column/x, row/y)
     * @return {@code true} if a path of empty tiles exists, {@code false} otherwise
     */
    public boolean existsPath(Vector2D goal) {
        return PathFinder.existsPathBFS(this.boardView, location(), goal);
    }

    /**
     * Checks if there is a connected empty path between two positions on the board.
     *
     * @param start the starting position as a Vector2D
     * @param goal the goal position as a Vector2D
     * @return {@code true} if a path of empty tiles exists between start and goal, {@code false} otherwise
     */
    public boolean existsPath(Vector2D start, Vector2D goal) {
        return PathFinder.existsPathBFS(this.boardView, start, goal);
    }

    /**
     * Teleports the active ladybug to the specified target position,
     * ignoring obstacles in between.
     *
     * @param goal the target coordinate
     * @return true if the ladybug was moved, false if the target is invalid or blocked
     */
    public boolean fly(Vector2D goal) {
        if (!boardView.isInside(goal)) {
            return false;
        }
        if (!boardView.tileAt(goal).isEmptyTile()) {
            return false;
        }

        int deltaX = goal.horizontal() - location().horizontal();
        int deltaY = goal.vertical() - location().vertical();

        if (Math.abs(deltaX) >= Math.abs(deltaY)) {
            activeLadybug.setFacing(deltaX >= 0 ? Facing.EAST : Facing.WEST);
        } else {
            activeLadybug.setFacing(deltaY >= 0 ? Facing.SOUTH : Facing.NORTH);
        }

        this.activeLadybug.updateLocation(goal);
        return true;
    }

    private Optional<Tile> tileAhead() {
        Vector2D frontPosition = location().sum(getLadybugFacing().delta());
        if (!this.boardView.isInside(frontPosition)) {
            return Optional.empty();
        }
        Tile frontTile = this.boardView.tileAt(frontPosition);
        return Optional.of(frontTile);
    }

    private Vector2D location() {
        return this.activeLadybug.getLocation();
    }

    private Facing getLadybugFacing() {
        return activeLadybug.getFacing();
    }
}
