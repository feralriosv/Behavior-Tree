package view.configuration;

import model.board.GameBoard;
import model.ladybug.LadyBug;
import model.DecisionTree;

import java.util.List;


/**
 * Represents an immutable configuration of a game setup before it begins.
 *
 * @param gameBoard the game board on which the game will be played
 * @param registeredBugs the list of ladybugs registered for the game
 * @param trees the list of decision trees used in the game
 * @author ubpst
 */
public record Configuration(GameBoard gameBoard, List<LadyBug> registeredBugs, List<DecisionTree> trees) {
}
