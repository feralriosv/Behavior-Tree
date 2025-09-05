package view.configuration.loader;

import java.util.List;

/**
 * A generic loader interface for converting a list of strings into a specific type.
 * Implementations define how textual configuration lines are parsed into objects.
 *
 * @param <T> the type of object produced by this loader
 *
 * @author ubpst
 */
public interface Loader<T> {

    /**
     * Loads an object of type {@code T} from the given list of strings.
     *
     * @param lines the list of lines to parse
     * @return the loaded object
     * @throws LoadingException if the input cannot be parsed into a valid object
     */
    T load(List<String> lines);
}
