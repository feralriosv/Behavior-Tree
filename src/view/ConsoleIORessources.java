package view;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * Implementation of {@link IORessources} that uses the console for input and output.
 *
 * @author ubpst
 */
public class ConsoleIORessources {

    private final Scanner inputSource;
    private final PrintStream defaultStream;
    private final PrintStream errorStream;

    /**
     * Creates a new {@code ConsoleIORessources} instance with the given input and output streams.
     *
     * @param inputStream   the input stream to read from, wrapped in a {@link Scanner}
     * @param defaultStream the stream used for standard output
     * @param errorStream   the stream used for error output
     */
    public ConsoleIORessources(InputStream inputStream, PrintStream defaultStream, PrintStream errorStream) {
        this.inputSource = new Scanner(inputStream);
        this.defaultStream = defaultStream;
        this.errorStream = errorStream;
    }

    public PrintStream getDefaultStream() {
        return this.defaultStream;
    }

    public PrintStream getErrorStream() {
        return this.errorStream;
    }

    public Scanner getInputSource() {
        return this.inputSource;
    }
}
