package model;

import Constants.Constants;

public class Grid {
    private final int[][] tiles;
    private final int size;

    public Grid() {
        this.size = Constants.SIZE;
        this.tiles = new int[size][size];
    }

    public Grid(int[][] tiles) {
        if (tiles == null) {
            throw new IllegalArgumentException("Tiles array cannot be null");
        }
        this.size = tiles.length;
        this.tiles = new int[size][size];

        for (int i = 0; i < size; i++) {
            if (tiles[i].length != size) {
                throw new IllegalArgumentException("Grid must be square");
            }
            System.arraycopy(tiles[i], 0, this.tiles[i], 0, size);
        }
    }

    public Grid(Grid other) {
        this.size = other.size;
        this.tiles = new int[size][size];
        for (int i = 0; i < size; i++) {
            System.arraycopy(other.tiles[i], 0, this.tiles[i], 0, size);
        }
    }

    public int getValue(int row, int col) {
        if (row < 0 || row >= size || col < 0 || col >= size) {
            throw new IndexOutOfBoundsException("Invalid coordinates: (" + row + ", " + col + ")");
        }
        return tiles[row][col];
    }

    public void setValue(int row, int col, int value) {
        if (row < 0 || row >= size || col < 0 || col >= size) {
            throw new IndexOutOfBoundsException("Invalid coordinates: (" + row + ", " + col + ")");
        }
        tiles[row][col] = value;
    }

    public int getSize() {
        return size;
    }

    public int[][] getTiles() {
        int[][] copy = new int[size][size];
        for (int i = 0; i < size; i++) {
            System.arraycopy(tiles[i], 0, copy[i], 0, size);
        }
        return copy;
    }

    public boolean isEmpty(int row, int col) {
        return getValue(row, col) == 0;
    }

    public boolean hasEmptyCells() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (isEmpty(row, col)) {
                    return true;
                }
            }
        }
        return false;
    }

    public int getHighestTile() {
        int highest = 0;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                highest = Math.max(highest, getValue(row, col));
            }
        }
        return highest;
    }

    public int getEmptyCellCount() {
        int count = 0;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (isEmpty(row, col)) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        Grid other = (Grid) obj;
        if (size != other.size)
            return false;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (tiles[i][j] != other.tiles[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = size;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                result = 31 * result + tiles[i][j];
            }
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                sb.append(String.format("%4d", tiles[i][j]));
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
