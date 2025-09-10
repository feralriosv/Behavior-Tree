package view.configuration.mermaid;

import java.util.Optional;

/**
 * Immutable representation of a parsed node definition consisting of a name and an optional label.
 *
 * @param name  the name of the node
 * @param label the label associated with the node, may be empty
 *
 * @author ubpst
 */
public record NodeToken(String name, String label) {

    private static final String EMPTY_LINE = "";
    private static final String NODE_LABEL_OPEN = "[";
    private static final String NODE_LABEL_CLOSE = "]";
    private static final String CONDITION_LABEL_OPEN = "(";
    private static final String CONDITION_LABEL_CLOSE = ")";

    /**
     * Parses a line of text to create a NodeToken.
     *
     * The line is expected to have the format: name[label], optionally enclosed in parentheses.
     *
     * @param line the input line to parse
     * @return an Optional containing the NodeToken if parsing is successful; otherwise, Optional.empty()
     */
    public static Optional<NodeToken> fromLine(String line) {
        String cleaned = line.trim().replace(CONDITION_LABEL_OPEN, EMPTY_LINE).replace(CONDITION_LABEL_CLOSE, EMPTY_LINE);
        int openIndex = cleaned.indexOf(NODE_LABEL_OPEN);
        int closeIndex = cleaned.lastIndexOf(NODE_LABEL_CLOSE);

        if (openIndex == -1 || closeIndex == -1) {
            if (cleaned.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(new NodeToken(cleaned, EMPTY_LINE));
        }

        String naming = cleaned.substring(0, openIndex).trim();
        String label = cleaned.substring(openIndex + 1, closeIndex).trim();

        if (naming.isEmpty() || label.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new NodeToken(naming, label));
    }
}
