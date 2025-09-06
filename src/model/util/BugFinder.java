package model.util;

import model.Game;
import model.ladybug.Identifier;
import model.ladybug.LadyBug;

import java.util.Optional;

/**
 * Utility class to locate {@link LadyBug} instances within a {@link Game} by their identifiers.
 *
 * @author ubpst
 */
public class BugFinder {

    private final Game game;

    /**
     * Constructs a BugFinder for a given game instance.
     *
     * @param game the game instance containing ladybugs
     */
    public BugFinder(Game game) {
        this.game = game;
    }

    /**
     * Finds a LadyBug by its identifier.
     *
     * @param identifier the identifier of the ladybug to find
     * @return the LadyBug with the specified identifier
     * @throws UnfoundedBugException if no LadyBug with the given identifier is found
     */
    public LadyBug findById(Identifier identifier) throws UnfoundedBugException {
        Optional<LadyBug> optionalLadyBug = this.game.getBugById(identifier);
        if (optionalLadyBug.isPresent()) {
            return optionalLadyBug.get();
        } else {
            throw new UnfoundedBugException(identifier);
        }
    }
}
