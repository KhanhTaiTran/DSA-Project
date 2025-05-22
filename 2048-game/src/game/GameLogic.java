
package game;

import java.util.Random;
import java.util.Stack;

public class GameLogic {
    private int[][] grid;
    private static final int SIZE = 4; // size of the grid
    private Stack<PrevState> undoStack; // stack to keep track of game states for undo functionality
    private int score = 0;

    public int[][] getGrid() {
        int[][] copy = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(grid[i], 0, copy[i], 0, SIZE); // create a copy of the grid
        }
        return copy;
    }

    public void initialize() {
        grid = new int[SIZE][SIZE];
        score = 0;
        addRandomTile();
        addRandomTile();
        undoStack.clear();
    }

    public GameLogic() {
        grid = new int[SIZE][SIZE];
        undoStack = new Stack<>();
        score = 0;
    }

    // save the current state to the undo stack
    public void saveState() {
        undoStack.push(new PrevState(grid, score));
    }

    // undo the last move
    public void undo() {
        if (!undoStack.isEmpty()) { // is moved?
            PrevState prev = undoStack.pop(); // get the last saved state
            // restore the grid and score from the saved state
            this.grid = prev.grid;
            this.score = prev.score;
        } else {
            System.out.println("No moves to undo.");
        }
    }

    public void move(String direction) {
        saveState(); // save the current state before making a move
        int[][] before = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(grid[i], 0, before[i], 0, SIZE);
        }
        int oldScore = score;
        boolean canMove = false;
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
        if (canMove && !isSameGrid(before, grid)) { // check if the grid has changed
            addRandomTile(); // add a random tile if a move was made
        } else {
            System.out.println("No tiles can be moved in this direction.");
        }
    }

    // check if the current grid is the same as the previous grid
    private boolean isSameGrid(int[][] a, int[][] b) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (a[i][j] != b[i][j])
                    return false;
            }
        }
        return true;
    }

    public void addRandomTile() {
        Random random = new Random();
        int emptyCount = 0;
        // check how many empty tiles are there
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (grid[i][j] == 0) {
                    emptyCount++;
                }
            }
        }
        // if there are no empty tiles, return
        if (emptyCount == 0) {
            return;
        }
        int pos = random.nextInt(emptyCount);
        int k = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (grid[i][j] == 0) {
                    if (k == pos) {
                        grid[i][j] = (random.nextInt(10) < 9) ? 2 : 4; // 90% for 2, 10% for 4
                        return;
                    }
                    k++;
                }
            }
        }
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
        // Step 1: Compress (slide non-zero tiles to the front)
        int[] compressed = new int[SIZE];
        int index = 0;
        for (int value : line) {
            if (value != 0) {
                compressed[index++] = value;
            }
        }

        // Step 2: Merge
        for (int i = 0; i < SIZE - 1; i++) {
            if (compressed[i] != 0 && compressed[i] == compressed[i + 1]) {
                compressed[i] *= 2;
                score += compressed[i];
                compressed[i + 1] = 0;
                i++; // Skip next tile (merged)
            }
        }

        // Step 3: Compress again
        int[] merged = new int[SIZE];
        index = 0;
        for (int value : compressed) {
            if (value != 0) {
                merged[index++] = value;
            }
        }
        System.arraycopy(merged, 0, line, 0, SIZE);
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

    public int getScore() {
        return score;
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
}