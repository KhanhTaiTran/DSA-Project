package game;

import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import interfaces.AIStrategy;

public class AI implements AIStrategy {
    private static final int MAX_DEPTH = 5;

    // Game state class to represent a node in the A* search
    private class GameState {
        private Board board;
        private String move;
        private String movePath;
        private int depth;
        private int score;

        public GameState(Board board, String move, String movePath, int depth) {
            this.board = board;
            this.move = move;
            this.movePath = movePath;
            this.depth = depth;
            this.score = evaluateBoard(board) - depth; // f(n) = h(n) - g(n)
        }
    }

    @Override
    public String findBestMove(Board board) {
        // Use A* search algorithm to find the best move
        PriorityQueue<GameState> frontier = new PriorityQueue<>(Comparator.comparingInt(state -> -state.score));
        Set<String> explored = new HashSet<>();

        // Add initial moves to frontier
        String[] possibleMoves = { "up", "down", "left", "right" };
        for (String move : possibleMoves) {
            Board newBoard = simulateMove(board, move);
            if (newBoard != null) {
                frontier.add(new GameState(newBoard, move, move, 1));
            }
        }

        // Track the best first move found
        String bestFirstMove = null;
        int bestScore = Integer.MIN_VALUE;

        // A* search
        while (!frontier.isEmpty()) {
            GameState current = frontier.poll();

            // Skip if we've seen this board state
            String boardState = getBoardStateString(current.board);
            if (explored.contains(boardState))
                continue;
            explored.add(boardState);

            // Update best move if this is a first-level move with better score
            if (current.depth == 1 && current.score > bestScore) {
                bestScore = current.score;
                bestFirstMove = current.move;
            }

            // Stop expanding if we've reached max depth
            if (current.depth >= MAX_DEPTH)
                continue;

            // Expand this node by trying all possible moves
            for (String move : possibleMoves) {
                Board newBoard = simulateMove(current.board, move);
                if (newBoard != null) {
                    frontier.add(new GameState(
                            newBoard,
                            current.depth == 1 ? current.move : current.move, // Keep initial move
                            current.movePath + "," + move,
                            current.depth + 1));
                }
            }

            // Limit exploration for performance
            if (explored.size() > 1000)
                break;
        }

        return bestFirstMove;
    }

    // Helper method to get string representation of board state for comparison
    private String getBoardStateString(Board board) {
        int[][] grid = board.getGrid();
        StringBuilder sb = new StringBuilder();
        for (int[] row : grid) {
            for (int cell : row) {
                sb.append(cell).append(",");
            }
        }
        return sb.toString();
    }

    @Override
    public int evaluateBoard(Board board) {
        int[][] grid = board.getGrid();
        int score = 0;
        int emptyCells = 0;
        int mergeScore = 0;
        int smoothness = 0;
        int monotonicity = 0;

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

            // Assess monotonicity (preference for values building up towards a corner)
            // Check horizontal monotonicity
            boolean increasing = true;
            boolean decreasing = true;
            for (int j = 1; j < grid[i].length; j++) {
                if (grid[i][j - 1] > grid[i][j])
                    increasing = false;
                if (grid[i][j - 1] < grid[i][j])
                    decreasing = false;
            }
            monotonicity += (increasing || decreasing) ? 100 : 0;
        }

        // Check vertical monotonicity
        for (int j = 0; j < grid[0].length; j++) {
            boolean increasing = true;
            boolean decreasing = true;
            for (int i = 1; i < grid.length; i++) {
                if (grid[i - 1][j] > grid[i][j])
                    increasing = false;
                if (grid[i - 1][j] < grid[i][j])
                    decreasing = false;
            }
            monotonicity += (increasing || decreasing) ? 100 : 0;
        }

        // Weight the different factors - adjusted for A*
        return score + (emptyCells * 30) + (mergeScore * 15) + (smoothness * 10) + monotonicity;
    }

    private Board simulateMove(Board board, String move) {
        Board simulatedBoard = new Board();
        int[][] originalGrid = board.getGrid();
        int[][] simulatedGrid = simulatedBoard.getGrid();

        for (int i = 0; i < originalGrid.length; i++) {
            System.arraycopy(originalGrid[i], 0, simulatedGrid[i], 0, originalGrid[i].length);
        }

        simulatedBoard.gameLogic.move(move);

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