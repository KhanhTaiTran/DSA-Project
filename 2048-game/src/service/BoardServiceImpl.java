package service;

import interfaces.BoardService;
import interfaces.ScoreCalculator;
import model.Grid;
import enums.Direction;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class BoardServiceImpl implements BoardService {
    private final Random random;
    private final ScoreCalculator scoreCalculator;
    private int lastMoveScore = 0;

    public BoardServiceImpl(ScoreCalculator scoreCalculator) {
        this.random = new Random();
        this.scoreCalculator = scoreCalculator;
    }

    @Override
    public Grid moveTiles(Grid grid, Direction direction) {
        Grid newGrid = new Grid(grid);
        lastMoveScore = 0;

        switch (direction) {
            case UP:
                return moveUp(newGrid);
            case DOWN:
                return moveDown(newGrid);
            case LEFT:
                return moveLeft(newGrid);
            case RIGHT:
                return moveRight(newGrid);
            default:
                return newGrid;
        }
    }

    private Grid moveUp(Grid grid) {
        int size = grid.getSize();
        for (int col = 0; col < size; col++) {
            int[] column = extractColumn(grid, col);
            int[] processed = processLine(column);
            setColumn(grid, col, processed);
        }
        return grid;
    }

    private Grid moveDown(Grid grid) {
        int size = grid.getSize();
        for (int col = 0; col < size; col++) {
            int[] column = extractColumn(grid, col);
            int[] reversed = reverse(column);
            int[] processed = processLine(reversed);
            int[] finalColumn = reverse(processed);
            setColumn(grid, col, finalColumn);
        }
        return grid;
    }

    private Grid moveLeft(Grid grid) {
        int size = grid.getSize();
        for (int row = 0; row < size; row++) {
            int[] rowArray = extractRow(grid, row);
            int[] processed = processLine(rowArray);
            setRow(grid, row, processed);
        }
        return grid;
    }

    private Grid moveRight(Grid grid) {
        int size = grid.getSize();
        for (int row = 0; row < size; row++) {
            int[] rowArray = extractRow(grid, row);
            int[] reversed = reverse(rowArray);
            int[] processed = processLine(reversed);
            int[] finalRow = reverse(processed);
            setRow(grid, row, finalRow);
        }
        return grid;
    }

    private int[] processLine(int[] line) {
        int[] compressed = compress(line);
        int[] merged = merge(compressed);
        return compress(merged);
    }

    private int[] compress(int[] line) {
        int[] result = new int[line.length];
        int index = 0;

        for (int value : line) {
            if (value != 0) {
                result[index++] = value;
            }
        }

        return result;
    }

    private int[] merge(int[] line) {
        // Official 2048 merge algorithm:
        // 1. Compress (slide non-zero tiles to the front)
        int[] compressed = new int[line.length];
        int index = 0;
        for (int value : line) {
            if (value != 0) {
                compressed[index++] = value;
            }
        }

        // 2. Merge
        for (int i = 0; i < compressed.length - 1; i++) {
            if (compressed[i] != 0 && compressed[i] == compressed[i + 1]) {
                compressed[i] *= 2;
                lastMoveScore += scoreCalculator.calculateMergeScore(compressed[i]);
                compressed[i + 1] = 0;
                i++; // Skip next tile (merged)
            }
        }

        // 3. Compress again
        int[] merged = new int[line.length];
        index = 0;
        for (int value : compressed) {
            if (value != 0) {
                merged[index++] = value;
            }
        }
        return merged;
    }

    private int[] reverse(int[] array) {
        int[] result = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[array.length - 1 - i];
        }
        return result;
    }

    private int[] extractRow(Grid grid, int row) {
        int size = grid.getSize();
        int[] rowArray = new int[size];
        for (int col = 0; col < size; col++) {
            rowArray[col] = grid.getValue(row, col);
        }
        return rowArray;
    }

    private int[] extractColumn(Grid grid, int col) {
        int size = grid.getSize();
        int[] columnArray = new int[size];
        for (int row = 0; row < size; row++) {
            columnArray[row] = grid.getValue(row, col);
        }
        return columnArray;
    }

    private void setRow(Grid grid, int row, int[] values) {
        for (int col = 0; col < values.length; col++) {
            grid.setValue(row, col, values[col]);
        }
    }

    private void setColumn(Grid grid, int col, int[] values) {
        for (int row = 0; row < values.length; row++) {
            grid.setValue(row, col, values[row]);
        }
    }

    @Override
    public boolean canMove(Grid grid, Direction direction) {
        Grid testGrid = new Grid(grid);
        Grid movedGrid = moveTiles(testGrid, direction);
        return !grid.equals(movedGrid);
    }

    @Override
    public Grid addRandomTile(Grid grid) {
        List<int[]> emptyCells = getEmptyCells(grid);

        if (emptyCells.isEmpty()) {
            return grid;
        }

        int[] randomCell = emptyCells.get(random.nextInt(emptyCells.size()));
        int value = random.nextDouble() < 0.9 ? 2 : 4;

        Grid newGrid = new Grid(grid);
        newGrid.setValue(randomCell[0], randomCell[1], value);

        return newGrid;
    }

    private List<int[]> getEmptyCells(Grid grid) {
        List<int[]> emptyCells = new ArrayList<>();
        int size = grid.getSize();

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (grid.isEmpty(row, col)) {
                    emptyCells.add(new int[] { row, col });
                }
            }
        }

        return emptyCells;
    }

    @Override
    public boolean hasEmptySpaces(Grid grid) {
        return grid.hasEmptyCells();
    }

    @Override
    public boolean canMerge(Grid grid) {
        int size = grid.getSize();

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                int current = grid.getValue(row, col);
                if (current != 0) {
                    // Check right neighbor
                    if (col < size - 1 && current == grid.getValue(row, col + 1)) {
                        return true;
                    }
                    // Check bottom neighbor
                    if (row < size - 1 && current == grid.getValue(row + 1, col)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public int getLastMoveScore() {
        return lastMoveScore;
    }
}
