package view.command;

import model.Game;
import view.Arguments;
import view.Command;
import view.CommandProvider;
import view.InvalidArgumentException;
import view.Keyword;

/**
 * This enum represents all keywords for commands handling a {@link Game}.
 *
 * @author ubpst
 * @author Programmieren-Team
 */
public enum ModelKeyword implements Keyword<Game> {
    /** Command to add a sibling node in the decision tree. */
    ADD_SIBLING(arguments -> new AddSibling(arguments.parseIdentifier(), arguments.parseNodeNaming(), arguments.parseNodeToken())),
    /** Command to display or move to the head node of the tree. */
    HEAD(arguments -> new Head(arguments.parseIdentifier())),
    /** Command to jump to a specific node in the decision tree. */
    JUMP_TO(arguments -> new JumpTo()),
    /** Command to list all ladybugs currently in the game. */
    LIST_LADYBUGS(arguments -> new ListLadybugs()),
    /** Command to execute the next action in the decision tree. */
    NEXT_ACTION(arguments -> new NextAction()),
    /** Command to print the position of a specific ladybug by identifier. */
    PRINT_POSITION(arguments -> new PrintPosition(arguments.parseIdentifier())),
    /** Command to reset the decision tree to its initial state. */
    RESET_TREE(arguments -> new ResetTree());

    private static final String VALUE_NAME_DELIMITER = "_";
    private final CommandProvider<Game> provider;

    /**
     * Creates a new keyword with the given command provider.
     *
     * @param provider the command provider that will create the command when this keyword is used
     */
    ModelKeyword(CommandProvider<Game> provider) {
        this.provider = provider;
    }

    @Override
    public Command<Game> provide(Arguments arguments) throws InvalidArgumentException {
        return this.provider.provide(arguments);
    }

    @Override
    public boolean matches(String[] command) {
        String[] splittedKeyword = name().split(VALUE_NAME_DELIMITER);
        if (command.length < splittedKeyword.length) {
            return false;
        }
        for (int i = 0; i < splittedKeyword.length; i++) {
            if (!splittedKeyword[i].toLowerCase().equals(command[i])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int words() {
        return name().split(VALUE_NAME_DELIMITER).length;
    }
}
