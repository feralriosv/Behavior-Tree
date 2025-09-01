package view.configuration.loader;

/**
 * Signals that parsing the configuration failed.
 *
 * @author ubpst
 */
public class LoadingException extends Exception {

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the details of the exception
     */
    public LoadingException(String message) {
        super(message);
    }
}
