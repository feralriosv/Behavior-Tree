package game;

import game.value.Identifier;
import game.value.Vector2D;

/**
 * Represents a ladybug in the game world.
 *
 * @author ubpst
 */
public class LadyBug {

    private final Identifier id;
    private Vector2D location;
    private LadybugFacing facing;
    private boolean carryingLeaf;

    /**
     * Creates a new {@code LadyBug} with the given id, starting location, and facing direction.
     *
     * @param id the unique identifier of the ladybug
     * @param location the starting position of the ladybug
     * @param facing the initial facing direction of the ladybug
     */
    public LadyBug(Identifier id, Vector2D location, LadybugFacing facing) {
        this.id = id;
        this.location = location;
        this.facing = facing;
        this.carryingLeaf = false;
    }

    /**
     * Checks whether this ladybug is currently carrying a leaf.
     *
     * @return true if the ladybug is carrying a leaf, false otherwise
     */
    public boolean isCarryingLeaf() {
        return this.carryingLeaf;
    }

    /**
     * Sets whether this ladybug is carrying a leaf.
     *
     * @param carryingLeaf true if the ladybug should carry a leaf, false otherwise
     */
    public void setCarryingLeaf(boolean carryingLeaf) {
        this.carryingLeaf = carryingLeaf;
    }

    /**
     * Updates the location of this ladybug.
     *
     * @param location the new position of the ladybug
     */
    public void setLocation(Vector2D location) {
        this.location = location;
    }

    /**
     * Calculates the position directly in front of this ladybug based on its current facing.
     *
     * @return the next position in front of the ladybug
     */
    public Vector2D positionAhead() {
        return location.sum(facing.delta());
    }

    /**
     * Turns the ladybug to face left (west).
     *
     * @return true if the turn was applied successfully
     */
    public boolean turnLeft() {
        this.facing = LadybugFacing.WEST;
        return true;
    }

    /**
     * Turns the ladybug to face right (east).
     *
     * @return true if the turn was applied successfully
     */
    public boolean turnRight() {
        this.facing = LadybugFacing.EAST;
        return true;
    }

    /**
     * Returns the current location of the ladybug.
     *
     * @return the current position
     */
    public Vector2D getLocation() {
        return location;
    }

    /**
     * Returns the unique identifier of this ladybug.
     *
     * @return the identifier of the ladybug
     */
    public Identifier getId() {
        return this.id;
    }

    /**
     * Returns the current facing direction of the ladybug.
     *
     * @return the facing direction
     */
    public LadybugFacing getFacing() {
        return facing;
    }
}
