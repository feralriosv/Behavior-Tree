package view.command;

import view.Arguments;
import view.Command;
import view.CommandExecuter;
import view.CommandProvider;
import view.InvalidArgumentException;
import view.Keyword;

/**
 * Defines the available keywords for general view-level commands.
 *
 * @author Programmieren-Team
 * @author ubpst
 */
public enum ViewKeyword implements Keyword<CommandExecuter<?, ?>> {
    /**
     * The keyword for the {@link Quit quit} command.
     */
    QUIT(arguments -> new Quit());

    private static final String VALUE_NAME_DELIMITER = "_";
    private final CommandProvider<CommandExecuter<?, ?>> provider;

    ViewKeyword(CommandProvider<CommandExecuter<?, ?>> provider) {
        this.provider = provider;
    }

    @Override
    public Command<CommandExecuter<?, ?>> provide(Arguments arguments) throws InvalidArgumentException {
        return provider.provide(arguments);
    }

    @Override
    public boolean matches(String[] command) {
        return name().toLowerCase().equals(command[0]);
    }

    @Override
    public int words() {
        return name().split(VALUE_NAME_DELIMITER).length;
    }
}
