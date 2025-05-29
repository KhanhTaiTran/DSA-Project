package game;

import Constants.Constants;

public class Board {
    private int[][] grid;
    private static final int SIZE = Constants.SIZE;
    public GameLogic gameLogic;

    public Board() {
        grid = new int[SIZE][SIZE];
    }

    public int[][] getGrid() {
        int[][] copy = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(grid[i], 0, copy[i], 0, SIZE);
        }
        return copy;
    }

    // Add these methods to allow synchronizing with gameLogic
    public void setGrid(int[][] newGrid) {
        for (int i = 0; i < grid.length; i++) {
            System.arraycopy(newGrid[i], 0, grid[i], 0, grid[i].length);
        }
    }
}
