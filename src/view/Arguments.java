package view;

import model.decisiontree.node.Naming;
import model.ladybug.Identifier;
import model.util.Vector2D;
import view.configuration.NodeToken;

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
     * Parses all remaining arguments as a sequence of 1-based board coordinates (x y),
     * converting them into zero-based {@link Vector2D} instances.
     *
     * @return an array of parsed {@link Vector2D}
     * @throws InvalidArgumentException if any coordinate pair is invalid
     */
    public Vector2D[] parseVectors() throws InvalidArgumentException {
        List<Vector2D> vectors = new ArrayList<>();
        while (!isExhausted()) {
            vectors.add(parseVector());
        }
        return vectors.toArray(new Vector2D[0]);
    }

    /**
     * Parses the next two arguments as a 1-based board coordinate (x y)
     * and returns it as a zero-based {@link Vector2D}.
     *
     * @return the parsed {@link Vector2D}
     * @throws InvalidArgumentException if fewer than two integers remain or format is invalid
     */
    private Vector2D parseVector() throws InvalidArgumentException {
        int horizontal = parseInteger();
        int vertical = parseInteger();
        return new Vector2D(horizontal - 1, vertical - 1);
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

    /**
     * Parses the next argument as a {@link Naming}.
     *
     * @return a {@link Naming} created from the next argument
     * @throws InvalidArgumentException if there are no more arguments available
     */
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
