package view;

import model.decisiontree.node.Naming;
import model.ladybug.Identifier;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class represents the arguments of a {@link Command}.
 *
 * @author ubpst
 * @author Programmieren-Team
 */
public class Arguments {

    private static final String ERROR_TOO_FEW_ARGUMENTS = "too few arguments";

    private final String[] arguments;
    private int argumentIndex;

    /**
     * Constructs a new instance that only holds arguments.
     *
     * @param arguments the arguments to parse
     */
    public Arguments(String[] arguments) {
        this.arguments = arguments.clone();
    }

    /**
     * Returns whether all provided arguments have been consumed.
     *
     * @return {@code true} if all arguments have been consumed, {@code false} otherwise
     */
    public boolean isExhausted() {
        return this.argumentIndex >= this.arguments.length;
    }

    /**
     * Parses the next argument as a string.
     *
     * @return the argument as a string
     * @throws InvalidArgumentException if there is no argument to parse
     */
    private String parseString() throws InvalidArgumentException {
        return retrieveArgument();
    }

    /**
     * Parses the next argument as a {@link NodeToken} in the canonical form {@code NAME[LABEL]}.
     *
     * @return the parsed {@link NodeToken}
     * @throws InvalidArgumentException if the argument is missing or does not match the expected format
     */
    public NodeToken parseNodeToken() throws InvalidArgumentException {
        String argument = retrieveArgument();

        Optional<NodeToken> nodeTokenOpt = NodeToken.fromLine(argument);
        if (nodeTokenOpt.isEmpty()) {
            throw new InvalidArgumentException("unrecognizable token: " + argument);
        }

        return nodeTokenOpt.get();
    }

    public Naming parseNodeNaming() throws InvalidArgumentException {
        return new Naming(retrieveArgument());
    }

    /**
     * Standard Description.
     *
     * @return standard return.
     * @throws InvalidArgumentException i
     */
    public Identifier parseIdentifier() throws InvalidArgumentException {
        return new Identifier(parseInteger());
    }

    /**
     * Parses the next argument as a string.
     *
     * @return the argument as a string
     * @throws InvalidArgumentException if there is no argument to parse
     */
    public Path parsePath() throws InvalidArgumentException {
        return Path.of(retrieveArgument().strip());
    }

    /**
     * Parses all remaining arguments as paths.
     *
     * @return a list of paths representing all remaining arguments
     * @throws InvalidArgumentException if there is no argument to parse
     */
    public List<Path> parsePaths() throws InvalidArgumentException {
        List<Path> paths = new ArrayList<>();
        do {
            String pathString = retrieveArgument();
            paths.add(Path.of(pathString.strip()));
        } while (!isExhausted());
        return paths;
    }

    private int parseInteger() throws InvalidArgumentException {
        int integer;

        try {
            integer = Integer.parseInt(retrieveArgument());
        } catch (NumberFormatException e) {
            throw new InvalidArgumentException("invalid integer format");
        }

        if (integer < 0) {
            throw new InvalidArgumentException("integers must be greater than 0");
        }

        return integer;
    }

    private String retrieveArgument() throws InvalidArgumentException {
        if (isExhausted()) {
            throw new InvalidArgumentException(ERROR_TOO_FEW_ARGUMENTS);
        }
        return arguments[argumentIndex++];
    }
}
