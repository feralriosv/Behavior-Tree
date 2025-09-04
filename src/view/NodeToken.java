package view;

import java.util.Optional;

public record NodeToken (String name, String label) {

    private static final String EMPTY_LINE = "";
    private static final String BRACKET_OPEN_SYMBOL = "[";
    private static final String BRACKET_CLOSE_SYMBOL = "]";
    private static final String PAREN_OPEN_SYMBOL = "(";
    private static final String PAREN_CLOSE_SYMBOL = ")";

    /**
     * Parses a line of text to create a NodeToken.
     *
     * The line is expected to have the format: name[label], optionally enclosed in parentheses.
     * If the line does not contain brackets, the entire trimmed line is used as the name,
     * and the label is set to an empty string.
     *
     * @param line the input line to parse
     * @return an Optional containing the NodeToken if parsing is successful; otherwise, Optional.empty()
     */
    public static Optional<NodeToken> fromLine(String line) {
        String cleaned = line.trim().replace(PAREN_OPEN_SYMBOL, EMPTY_LINE).replace(PAREN_CLOSE_SYMBOL, EMPTY_LINE);
        int openIndex = cleaned.indexOf(BRACKET_OPEN_SYMBOL);
        int closeIndex = cleaned.lastIndexOf(BRACKET_CLOSE_SYMBOL);

        if (openIndex == -1 || closeIndex == -1) {
            if (cleaned.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(new NodeToken(cleaned, EMPTY_LINE));
        }

        String id = cleaned.substring(0, openIndex).trim();
        String label = cleaned.substring(openIndex + 1, closeIndex).trim();

        if (id.isEmpty() || label.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new NodeToken(id, label));
    }
}
