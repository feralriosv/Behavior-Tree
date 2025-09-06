package view.util;

import model.util.Vector2D;

/**
 * Utility decorator for {@link Vector2D} that provides formatted string representations of the vector.
 *
 * @author ubpst
 */

public class Vector2DDecorator {

    private static final String FORMAT_CSV = "%d,%d";
    private static final String FORMAT_TUPLE = "(%d, %d)";

    private final Vector2D vector2D;

    /**
     * Constructs a Vector2DDecorator for the specified {@link Vector2D}.
     *
     * @param vector2D the vector to decorate and format
     */
    public Vector2DDecorator(Vector2D vector2D) {
        this.vector2D = vector2D;
    }

    /**
     * Returns a string representation of the vector in CSV format: "x,y".
     *
     * @return the vector as a CSV string
     *
     */
    public String asCsv() {
        return String.format(FORMAT_CSV, vector2D.vertical() + 1, vector2D.horizontal() + 1);
    }

    /**
     * Returns a string representation of the vector in tuple format: "(x,y)".
     *
     * @return the vector as a tuple string
     */
    public String asTuple() {
        return String.format(FORMAT_TUPLE, vector2D.horizontal() + 1, vector2D.vertical() + 1);
    }

}
