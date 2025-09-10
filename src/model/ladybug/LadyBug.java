package model.ladybug;

import model.util.Vector2D;

/**
 * Represents a ladybug in the game world.
 *
 * @author ubpst
 */
public class LadyBug {

    private final Identifier id;
    private Vector2D location;
    private Facing facing;

    /**
     * Creates a new {@code LadyBug} with the given id, starting location, and facing direction.
     *
     * @param id the unique identifier of the ladybug
     * @param location the starting position of the ladybug
     * @param facing the initial facing direction of the ladybug
     */
    public LadyBug(Identifier id, Vector2D location, Facing facing) {
        this.id = id;
        this.location = location;
        this.facing = facing;
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
        this.facing = facing.left();
        return true;
    }

    /**
     * Turns the ladybug to face right (east).
     *
     * @return true if the turn was applied successfully
     */
    public boolean turnRight() {
        this.facing = facing.right();
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
    public Facing getFacing() {
        return facing;
    }

    /**
     * Updates the fecing of this ladybug.
     *
     * @param facing the new facing of the ladybug
     */
    public void setFacing(Facing facing) {
        this.facing = facing;
    }

    /**
     * Updates the location of this ladybug.
     *
     * @param location the new position of the ladybug
     */
    public void updateLocation(Vector2D location) {
        this.location = location;
    }
}
