package view.finder;

import model.ladybug.Identifier;

/**
 * Exception thrown when a ladybug with the given identifier cannot be found.
 *
 * @author ubpst
 */
public class UnfoundedBugException extends Exception {

    private static final String UNFOUNDED_LADYBUG_MESSAGE = "LadyBug with identifier %s not found";

    /**
     * Constructs a new UnfoundedBugException with a message indicating the ladybug identifier that was not found.
     *
     * @param identifier the identifier of the ladybug that could not be found
     */
    public UnfoundedBugException(Identifier identifier) {
        super(UNFOUNDED_LADYBUG_MESSAGE.formatted(identifier));
    }
}
