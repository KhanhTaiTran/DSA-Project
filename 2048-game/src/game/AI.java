
package game;

import java.util.ArrayList;
import java.util.List;

public class AI {
    private static final int MAX_DEPTH = 3;

    public String findBestMove(Board board) {
        String[] moves = { "up", "down", "left", "right" };
        String bestMove = null;
        int bestScore = Integer.MIN_VALUE;

        for (String move : moves) {
            Board simulatedBoard = simulateMove(board, move);
            if (simulatedBoard != null) {
                int score = minimax(simulatedBoard, MAX_DEPTH, false);
                if (score > bestScore) {
                    bestScore = score;
                    bestMove = move;
                }
            }
        }

        return bestMove;
    }

    private int minimax(Board board, int depth, boolean isMaximizing) {
        if (depth == 0 || board.isGameOver()) {
            return evaluateBoard(board);
        }

        String[] moves = { "up", "down", "left", "right" };
        if (isMaximizing) {
            int maxEval = Integer.MIN_VALUE;
            for (String move : moves) {
                Board simulatedBoard = simulateMove(board, move);
                if (simulatedBoard != null) {
                    int eval = minimax(simulatedBoard, depth - 1, false);
                    maxEval = Math.max(maxEval, eval);
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (String move : moves) {
                Board simulatedBoard = simulateMove(board, move);
                if (simulatedBoard != null) {
                    int eval = minimax(simulatedBoard, depth - 1, true);
                    minEval = Math.min(minEval, eval);
                }
            }
            return minEval;
        }
    }

    private int evaluateBoard(Board board) {
        int[][] grid = board.getGrid();
        int score = 0;

        for (int[] row : grid) {
            for (int tile : row) {
                score += tile;
            }
        }

        return score;
    }

    private Board simulateMove(Board board, String move) {
        Board simulatedBoard = new Board();
        int[][] originalGrid = board.getGrid();
        int[][] simulatedGrid = simulatedBoard.getGrid();

        for (int i = 0; i < originalGrid.length; i++) {
            System.arraycopy(originalGrid[i], 0, simulatedGrid[i], 0, originalGrid[i].length);
        }

        simulatedBoard.move(move);

        if (isSameGrid(originalGrid, simulatedGrid)) {
            return null; // No change in board state, invalid move
        }

        return simulatedBoard;
    }

    private boolean isSameGrid(int[][] grid1, int[][] grid2) {
        for (int i = 0; i < grid1.length; i++) {
            for (int j = 0; j < grid1[i].length; j++) {
                if (grid1[i][j] != grid2[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
}