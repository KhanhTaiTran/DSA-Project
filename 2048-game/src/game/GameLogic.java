
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
        if (canMoveUp() || canMoveDown() || canMoveLeft() || canMoveRight()) {
            int[][] gridCopy = new int[SIZE][SIZE];
            for (int i = 0; i < SIZE; i++) {
                System.arraycopy(grid[i], 0, gridCopy[i], 0, SIZE);
            }
            undoStack.push(new PrevState(gridCopy, score));
        }
    }

    // undo the last move
    public void undo() {
        if (!undoStack.isEmpty()) {
            PrevState prevState = undoStack.pop();
            grid = new int[SIZE][SIZE];
            for (int i = 0; i < SIZE; i++) {
                System.arraycopy(prevState.grid[i], 0, grid[i], 0, SIZE); // restore the grid from the previous state
            }
            score = prevState.score; // restore the score from the previous state
        } else {
            System.out.println("No moves to undo.");
        }
    }

    public void move(String direction) {
        saveState(); // save the current state before making a move
        int[][] before = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) { // create a copy of the current grid
            System.arraycopy(grid[i], 0, before[i], 0, SIZE);
        }
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

    void moveUp() {
        for (int col = 0; col < SIZE; col++) {
            int[] compressed = compressColumnUp(col);
            mergeTiles(compressed);
            setColumnMoveUp(col, compressed);
        }
    }

    void moveDown() {
        for (int col = 0; col < SIZE; col++) {
            int[] compressed = compressColumnDown(col);
            mergeTiles(compressed);
            setColumnMoveDown(col, compressed);
        }
    }

    void moveLeft() {
        for (int row = 0; row < SIZE; row++) {
            int[] compressed = compressRowLeft(row);
            mergeTiles(compressed);
            setRowMoveLeft(row, compressed);
        }
    }

    void moveRight() {
        for (int row = 0; row < SIZE; row++) {
            int[] compressed = compressRowRight(row);
            mergeTiles(compressed);
            setRowMoveRight(row, compressed);
        }
    }

    private int[] compressRowLeft(int row) {
        int[] compressed = new int[SIZE];
        int index = 0;
        for (int col = 0; col < SIZE; col++) {
            if (grid[row][col] != 0) {
                compressed[index++] = grid[row][col];
            }
        }
        return compressed;
    }

    private int[] compressRowRight(int row) {
        int[] compressed = new int[SIZE];
        int index = SIZE - 1;
        for (int col = SIZE - 1; col >= 0; col--) {
            if (grid[row][col] != 0) {
                compressed[index--] = grid[row][col];
            }
        }
        return compressed;
    }

    private int[] compressColumnUp(int col) {
        int[] compressed = new int[SIZE];
        int index = 0;
        for (int row = 0; row < SIZE; row++) {
            if (grid[row][col] != 0) {
                compressed[index++] = grid[row][col];
            }
        }
        return compressed;
    }

    private int[] compressColumnDown(int col) {
        int[] compressed = new int[SIZE];
        int index = SIZE - 1;
        for (int row = SIZE - 1; row >= 0; row--) {
            if (grid[row][col] != 0) {
                compressed[index--] = grid[row][col];
            }
        }
        return compressed;
    }

    // can merge tiles?
    public boolean isMerge = false;

    private void mergeTiles(int[] line) {
        // compress (slide non-zero tiles to the front)
        int[] compressed = new int[SIZE];
        int index = 0;
        for (int value : line) {
            if (value != 0) {
                compressed[index++] = value;
            }
        }

        // Merge
        for (int i = 0; i < SIZE - 1; i++) {
            if (compressed[i] != 0 && compressed[i] == compressed[i + 1]) {
                compressed[i] *= 2;
                score += compressed[i];
                compressed[i + 1] = 0;
                i++; // Skip next tile (merged)
            }
        }

        // Compress again
        int[] merged = new int[SIZE];
        index = 0;
        for (int value : compressed) {
            if (value != 0) {
                merged[index++] = value;
            }
        }
        System.arraycopy(merged, 0, line, 0, SIZE);
    }

    private void setRowMoveLeft(int row, int[] line) {
        for (int col = 0; col < SIZE; col++) {
            grid[row][col] = line[col];
        }
    }

    private void setRowMoveRight(int row, int[] line) {
        // count how many non-zero tiles are there
        int count = 0;
        for (int col = 0; col < SIZE; col++) {
            if (line[col] != 0) {
                count++;
            }
        }

        // set the zero tiles at the left
        for (int col = 0; col < SIZE - count; col++) {
            grid[row][col] = 0;
        }

        // set the non-zero tiles at the right
        // We need to preserve the original order in the array
        for (int col = SIZE - count; col < SIZE; col++) {
            grid[row][col] = line[col - (SIZE - count)];
        }
    }

    private void setColumnMoveUp(int col, int[] line) {
        for (int row = 0; row < SIZE; row++) {
            grid[row][col] = line[row];
        }
    }

    private void setColumnMoveDown(int col, int[] line) {
        // count how many non-zero tiles are there
        int count = 0;
        for (int row = 0; row < SIZE; row++) {
            if (line[row] != 0) {
                count++;
            }
        }

        // set the zero tiles at the top
        for (int row = 0; row < SIZE - count; row++) {
            grid[row][col] = 0;
        }

        // set the non-zero tiles at the bottom
        // We need to preserve the original order in the array
        for (int row = SIZE - count; row < SIZE; row++) {
            grid[row][col] = line[row - (SIZE - count)];
        }
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score2) {
        this.score = score2;
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