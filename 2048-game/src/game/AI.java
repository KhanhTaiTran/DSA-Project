package game;

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
        if (depth == 0 || isGameOver(board)) {
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

    // Helper method to check if game is over
    private boolean isGameOver(Board board) {
        int[][] grid = board.getGrid();
        int size = grid.length;
        
        // Check for empty cells
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (grid[row][col] == 0) {
                    return false;
                }
            }
        }
        
        // Check for adjacent matching cells
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if ((row < size - 1 && grid[row][col] == grid[row + 1][col]) || 
                    (col < size - 1 && grid[row][col] == grid[row][col + 1])) {
                    return false;
                }
            }
        }
        
        return true;
    }

    private int evaluateBoard(Board board) {
        int[][] grid = board.getGrid();
        int score = 0;
        int emptyCells = 0;
        int mergeScore = 0;
        int smoothness = 0;
        
        // Count empty cells and calculate score
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == 0) {
                    emptyCells++;
                } else {
                    score += grid[i][j];
                    
                    // Check for potential merges
                    if (i < grid.length - 1 && grid[i][j] == grid[i + 1][j]) {
                        mergeScore += grid[i][j];
                    }
                    if (j < grid[i].length - 1 && grid[i][j] == grid[i][j + 1]) {
                        mergeScore += grid[i][j];
                    }
                    
                    // Calculate smoothness (penalty for non-adjacent different values)
                    if (i < grid.length - 1 && grid[i][j] != 0 && grid[i + 1][j] != 0) {
                        smoothness -= Math.abs(Math.log(grid[i][j]) - Math.log(grid[i + 1][j]));
                    }
                    if (j < grid[i].length - 1 && grid[i][j] != 0 && grid[i][j + 1] != 0) {
                        smoothness -= Math.abs(Math.log(grid[i][j]) - Math.log(grid[i][j + 1]));
                    }
                }
            }
        }
        
        // Weight the different factors
        return score + (emptyCells * 10) + (mergeScore * 8) + (smoothness * 4);
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