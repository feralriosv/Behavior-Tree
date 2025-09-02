/*
 * Copyright (c) 2025, KASTEL. All rights reserved.
 */

package model.ladybug;

/**
 * Represents an immutable 2D vector with vertical and horizontal components.
 *
 * @param vertical   the vertical component of the vector
 * @param horizontal the horizontal component of the vector
 *
 * @author ubpst
 * @author Programmieren-Team
 */
public record Vector2D(int vertical, int horizontal) {

    private static final String REPRESENTATION = "(%d, %d)";

    /**
     * Returns a new Vector2D that is the sum of this vector and the given one.
     *
     * @param other the vector to add
     * @return the resulting summed vector
     */
    public Vector2D sum(Vector2D other) {
        return new Vector2D(this.vertical + other.vertical, this.horizontal + other.horizontal);
    }


    @Override
    public String toString() {
        return REPRESENTATION.formatted(this.horizontal + 1, this.vertical + 1);
    }
}
