package view.command;

import view.Arguments;
import view.Command;
import view.CommandProvider;
import view.InvalidArgumentException;
import view.Keyword;
import view.configuration.Configuration;
import view.configuration.SetupExecuter;

/**
 * Defines the available keywords for setup commands in the game configuration phase.
 * Each keyword maps to a specific command, such as {@link LoadBoard} or {@link LoadTrees},
 * and provides the logic for parsing arguments and creating the corresponding command.
 *
 * @author ubpst
 */
public enum SetupKeyword implements Keyword<SetupExecuter<Configuration, ?>> {
    /**
     * The keyword for the {@link LoadBoard} command.
     */
    LOAD_BOARD(arguments -> new LoadBoard(arguments.parsePath())),
    /**
     * The keyword for the {@link LoadTrees} command.
     */
    LOAD_TREES(arguments -> new LoadTrees(arguments.parsePaths()));

    private static final String VALUE_NAME_DELIMITER = "_";
    private final CommandProvider<SetupExecuter<Configuration, ?>> provider;


    SetupKeyword(CommandProvider<SetupExecuter<Configuration, ?>> provider) {
        this.provider = provider;
    }

    @Override
    public Command<SetupExecuter<Configuration, ?>> provide(Arguments arguments) throws InvalidArgumentException {
        return provider.provide(arguments);
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
