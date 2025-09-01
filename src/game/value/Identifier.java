package game.value;

/**
 * Represents a unique identifier for game entities such as {@code LadyBug}s or other objects.
 *
 * @param value the integer value of this identifier
 * @author ubpst
 */
public record Identifier(int value) {
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Identifier other = (Identifier) obj;
        return value == other.value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }
}
