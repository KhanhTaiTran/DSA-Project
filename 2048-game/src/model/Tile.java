package model;

public class Tile {
    private final int value;

    public static final Tile EMPTY = new Tile(0);

    public Tile(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Tile value cannot be negative");
        }
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public boolean isEmpty() {
        return value == 0;
    }

    public Tile merge() {
        if (isEmpty()) {
            throw new IllegalStateException("Cannot merge empty tile");
        }
        return new Tile(value * 2);
    }

    public boolean canMergeWith(Tile other) {
        return !isEmpty() && !other.isEmpty() && this.value == other.value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Tile tile = (Tile) obj;
        return value == tile.value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }

    @Override
    public String toString() {
        return isEmpty() ? "." : String.valueOf(value);
    }
}
