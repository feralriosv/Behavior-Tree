package view;

import java.io.PrintStream;
import java.util.Scanner;

/**
 * Abstraction for input and output resources.
 * Provides access to a {@link Scanner} for input, and {@link PrintStream}s for default and error outputs.
 *
 * @author ubst
 */
public interface IORessources {
    /**
     * Returns the scanner that is used for input.
     *
     * @return the {@link Scanner} instance for user input
     */
    Scanner getInputSource();

    /**
     * Returns the default output stream.
     *
     * @return the {@link PrintStream} used for standard output
     */
    PrintStream getDefaultStream();

    /**
     * Returns the error output stream.
     *
     * @return the {@link PrintStream} used for error output
     */
    PrintStream getErrorStream();
}
