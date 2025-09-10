package model.util;

import model.ladybug.BugIdentifier;

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
     * @param bugIdentifier the identifier of the ladybug that could not be found
     */
    public UnfoundedBugException(BugIdentifier bugIdentifier) {
        super(UNFOUNDED_LADYBUG_MESSAGE.formatted(bugIdentifier));
    }
}
