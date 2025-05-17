package game;

import java.util.Random;
import java.util.Stack;

import Constants.Constants;

public class Board {
    private int[][] grid;
    private static final int SIZE = Constants.SIZE;
    private Stack<int[][]> undoStack;

    public Board() {
        grid = new int[SIZE][SIZE];
        undoStack = new Stack<>();
    }

    // Save the current state to the undo stack
    private void saveState() {
        int[][] previousState = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(grid[i], 0, previousState[i], 0, SIZE);
        }
        undoStack.push(previousState);
    }

    // Undo the last move
    public void undo() {
        if (!undoStack.isEmpty()) {
            grid = undoStack.pop();
        } else {
            System.out.println("No moves to undo.");
        }
    }

    public void move(String direction) {
        boolean canMove = false;
        saveState(); // Save the current state before making a move
        switch (direction.toLowerCase()) {
            case "up":
                canMove = canMoveUp();
                if (canMove)
                    moveUp();
                break;
            case "down":
                canMove = canMoveDown();
                if (canMove)
                    moveDown();
                break;
            case "left":
                canMove = canMoveLeft();
                if (canMove)
                    moveLeft();
                break;
            case "right":
                canMove = canMoveRight();
                if (canMove)
                    moveRight();
                break;
            default:
                System.out.println("Invalid direction. Use up, down, left, or right.");
        }
        if (canMove) {
            addRandomTile();
        } else {
            undoStack.pop(); // Revert the save if no move was made
            System.out.println("No tiles can be moved in this direction.");
        }
    }

    public void addRandomTile() {
        Random random = new Random();
        int row, col;
        do {
            row = random.nextInt(SIZE);
            col = random.nextInt(SIZE);
        } while (grid[row][col] != 0);
        grid[row][col] = random.nextBoolean() ? 2 : 4; // 90% chance for 2, 10% for 4
    }

    private boolean canMoveUp() {
        for (int col = 0; col < SIZE; col++) {
            for (int row = 1; row < SIZE; row++) {
                if (grid[row][col] != 0 && (grid[row - 1][col] == 0 || grid[row - 1][col] == grid[row][col])) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean canMoveDown() {
        for (int col = 0; col < SIZE; col++) {
            for (int row = SIZE - 2; row >= 0; row--) {
                if (grid[row][col] != 0 && (grid[row + 1][col] == 0 || grid[row + 1][col] == grid[row][col])) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean canMoveLeft() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 1; col < SIZE; col++) {
                if (grid[row][col] != 0 && (grid[row][col - 1] == 0 || grid[row][col - 1] == grid[row][col])) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean canMoveRight() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = SIZE - 2; col >= 0; col--) {
                if (grid[row][col] != 0 && (grid[row][col + 1] == 0 || grid[row][col + 1] == grid[row][col])) {
                    return true;
                }
            }
        }
        return false;
    }

    private void moveUp() {
        for (int col = 0; col < SIZE; col++) {
            int[] compressed = compressColumn(col);
            mergeTiles(compressed);
            setColumn(col, compressed);
        }
    }

    private void moveDown() {
        for (int col = 0; col < SIZE; col++) {
            int[] compressed = compressColumnReverse(col);
            mergeTiles(compressed);
            setColumnReverse(col, compressed);
        }
    }

    private void moveLeft() {
        for (int row = 0; row < SIZE; row++) {
            int[] compressed = compressRow(row);
            mergeTiles(compressed);
            setRow(row, compressed);
        }
    }

    private void moveRight() {
        for (int row = 0; row < SIZE; row++) {
            int[] compressed = compressRowReverse(row);
            mergeTiles(compressed);
            setRowReverse(row, compressed);
        }
    }

    private int[] compressRow(int row) {
        int[] compressed = new int[SIZE];
        int index = 0;
        for (int col = 0; col < SIZE; col++) {
            if (grid[row][col] != 0) {
                compressed[index++] = grid[row][col];
            }
        }
        return compressed;
    }

    private int[] compressRowReverse(int row) {
        int[] compressed = new int[SIZE];
        int index = SIZE - 1;
        for (int col = SIZE - 1; col >= 0; col--) {
            if (grid[row][col] != 0) {
                compressed[index--] = grid[row][col];
            }
        }
        return compressed;
    }

    private int[] compressColumn(int col) {
        int[] compressed = new int[SIZE];
        int index = 0;
        for (int row = 0; row < SIZE; row++) {
            if (grid[row][col] != 0) {
                compressed[index++] = grid[row][col];
            }
        }
        return compressed;
    }

    private int[] compressColumnReverse(int col) {
        int[] compressed = new int[SIZE];
        int index = SIZE - 1;
        for (int row = SIZE - 1; row >= 0; row--) {
            if (grid[row][col] != 0) {
                compressed[index--] = grid[row][col];
            }
        }
        return compressed;
    }

    // ismerge?
    public boolean isMerge = false;

    private void mergeTiles(int[] line) {
        for (int i = 0; i < line.length - 1; i++) {
            if (line[i] != 0 && line[i] == line[i + 1]) {
                line[i] *= 2;
                line[i + 1] = 0;
            }
        }
        int[] compressed = new int[SIZE];
        int index = 0;
        for (int value : line) {
            if (value != 0) {
                compressed[index++] = value;
            }
        }
        System.arraycopy(compressed, 0, line, 0, SIZE);
    }

    private void setRow(int row, int[] line) {
        System.arraycopy(line, 0, grid[row], 0, SIZE);
    }

    private void setRowReverse(int row, int[] line) {
        for (int col = 0; col < SIZE; col++) {
            grid[row][col] = line[SIZE - 1 - col];
        }
    }

    private void setColumn(int col, int[] line) {
        for (int row = 0; row < SIZE; row++) {
            grid[row][col] = line[row];
        }
    }

    private void setColumnReverse(int col, int[] line) {
        for (int row = 0; row < SIZE; row++) {
            grid[row][col] = line[SIZE - 1 - row];
        }
    }

    // check if the game is over
    public boolean isGameOver() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (grid[row][col] == 0) {
                    return false;
                }
                if (row < SIZE - 1 && grid[row][col] == grid[row + 1][col]) {
                    return false;
                }
                if (col < SIZE - 1 && grid[row][col] == grid[row][col + 1]) {
                    return false;
                }
            }
        }
        return true;
    }

    public int[][] getGrid() {
        return grid;
    }

    public void initialize() {
        grid = new int[SIZE][SIZE];
        addRandomTile();
        addRandomTile();
    }

    public void printBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                System.out.print(grid[i][j] + "\t");
            }
            System.out.println();
        }
    }
}
