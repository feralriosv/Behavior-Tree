package game.value;

import java.util.Objects;

/**
 * Represents a unique identifier for a node in a decision tree.
 * Encapsulates a string value and provides proper {@link #toString()},
 * {@link #equals(Object)}, and {@link #hashCode()} implementations.
 *
 * @param value the string value of this node identifier
 *
 * @author ubpst
 */
public record NodeIdentifier(String value) {

    @Override
    public String toString() {
        return this.value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        NodeIdentifier nodeName = (NodeIdentifier) obj;
        return Objects.equals(value, nodeName.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
