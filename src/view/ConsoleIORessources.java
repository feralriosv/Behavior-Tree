package view;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * Implementation of {@link IORessources} that uses the console for input and output.
 *
 * @author ubpst
 */
public class ConsoleIORessources implements IORessources {

    private final Scanner scanner;
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
        this.scanner = new Scanner(inputStream);
        this.defaultStream = defaultStream;
        this.errorStream = errorStream;
    }

    @Override
    public PrintStream defaultStream() {
        return this.defaultStream;
    }

    @Override
    public PrintStream errorStream() {
        return this.errorStream;
    }

    @Override
    public Scanner inputSource() {
        return this.scanner;
    }
}
