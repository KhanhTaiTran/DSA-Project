package game;

import Constants.Constants;

public class PrevState { // use for undo functionality

    private static final int SIZE = Constants.SIZE; // Size of the grid
    int[][] grid;
    int score;

    PrevState(int[][] grid, int score) { // Constructor to create a new PrevState
        this.grid = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(grid[i], 0, this.grid[i], 0, SIZE);
        }
        this.score = score;
    }

}
