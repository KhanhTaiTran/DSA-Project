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
}
