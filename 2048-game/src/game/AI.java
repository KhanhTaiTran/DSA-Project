package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import interfaces.AIStrategy;
import service.ScoreManager;

public class AI implements AIStrategy {
    private static final int MAX_DEPTH = 5; // Maximum search depth
    private final Random random = new Random();
    private ScoreManager scoreManager = new ScoreManager();

    // Cache to store previously evaluated board states
    private Map<String, Integer> evaluationCache = new HashMap<>();

    @Override
    public String findBestMove(Board board) {
        // Clear the cache for a fresh evaluation
        evaluationCache.clear();

        String[] possibleMoves = { "up", "down", "left", "right" };
        String bestMove = null;
        int bestScore = scoreManager.getBestScore();

        // Try each possible initial move
        for (String move : possibleMoves) {
            Board newBoard = simulateMove(board, move);
            if (newBoard != null) {
                // Use backtracking to evaluate this move sequence
                List<String> movePath = new ArrayList<>();
                movePath.add(move);

                int score = backtrackSearch(newBoard, movePath, 1);

                // Update best move if this sequence is better
                if (score > bestScore) {
                    bestScore = score;
                    bestMove = move;
                }
            }
        }

        return bestMove;
    }

    private int backtrackSearch(Board board, List<String> movePath, int depth) {
        // Check if we've evaluated this state before
        String boardState = getBoardStateString(board);
        if (evaluationCache.containsKey(boardState)) {
            return evaluationCache.get(boardState);
        }

        // Base case: reached maximum depth
        if (depth >= MAX_DEPTH) {
            int score = evaluateBoard(board);
            evaluationCache.put(boardState, score);
            return score;
        }

        String[] possibleMoves = { "up", "down", "left", "right" };
        int bestScore = scoreManager.getBestScore();
        boolean anyValidMove = false;

        // Try each possible move from this state
        for (String move : possibleMoves) {
            Board newBoard = simulateMove(board, move);

            // Skip invalid moves
            if (newBoard == null) {
                continue;
            }

            anyValidMove = true;

            // Add move to path
            movePath.add(move);

            // Recursively search
            int score = backtrackSearch(newBoard, movePath, depth + 1);

            // Update best score
            if (score > bestScore) {
                bestScore = score;
            }

            // Backtrack: remove the last move to try other paths
            movePath.remove(movePath.size() - 1);
        }

        // No valid moves from this state - terminal state
        if (!anyValidMove) {
            int score = evaluateBoard(board);
            evaluationCache.put(boardState, score);
            return score;
        }

        // Cache and return the best score
        evaluationCache.put(boardState, bestScore);
        return bestScore;
    }

    /**
     * Get string representation of board for caching
     */
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

    // Create a deep copy of a board
    private Board copyBoard(Board original) {
        Board copy = new Board();

        // Initialize gameLogic if needed
        if (copy.gameLogic == null) {
            copy.gameLogic = new GameLogic(copy);
        }

        int[][] originalGrid = original.getGrid();
        int[][] copyGrid = copy.getGrid();

        for (int i = 0; i < originalGrid.length; i++) {
            System.arraycopy(originalGrid[i], 0, copyGrid[i], 0, originalGrid[i].length);
        }

        return copy;
    }

    @Override
    public int evaluateBoard(Board board) {
        int[][] grid = board.getGrid();
        int score = 0;
        int emptyCells = 0;
        int mergeScore = 0;
        int smoothness = 0;
        int cornerValue = 0;
        int snakePatternBonus = 0;

        // Find the highest value in the grid
        int maxValue = 0;
        int maxI = 0, maxJ = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] > maxValue) {
                    maxValue = grid[i][j];
                    maxI = i;
                    maxJ = j;
                }
            }
        }

        // Bonus for having max tile in corner
        if ((maxI == 0 || maxI == grid.length - 1) &&
                (maxJ == 0 || maxJ == grid[0].length - 1)) {
            cornerValue = maxValue * 2;
        }

        // Assess snake pattern (zigzag arrangement of increasing values)
        // This pattern is particularly effective for 2048
        snakePatternBonus = evaluateSnakePattern(grid);

        // Count empty cells
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

                    // Calculate smoothness (penalty for large differences between adjacent tiles)
                    if (i < grid.length - 1 && grid[i][j] != 0 && grid[i + 1][j] != 0) {
                        smoothness -= Math.abs(Math.log(grid[i][j]) - Math.log(grid[i + 1][j]));
                    }
                    if (j < grid[i].length - 1 && grid[i][j] != 0 && grid[i][j + 1] != 0) {
                        smoothness -= Math.abs(Math.log(grid[i][j]) - Math.log(grid[i][j + 1]));
                    }
                }
            }
        }

        // Calculate monotonicity - encourage tiles to be in descending/ascending order
        int monotonicityScore = calculateMonotonicity(grid);

        // Weight the different factors based on their importance
        return score +
                (emptyCells * 40) +
                (mergeScore * 15) +
                (smoothness * 8) +
                monotonicityScore +
                cornerValue +
                snakePatternBonus;
    }

    private int evaluateSnakePattern(int[][] grid) {
        int score = 0;
        int size = grid.length;

        // Check for snake pattern from top-left
        int[] expectedOrder = new int[size * size];
        int index = 0;

        // Create expected indices for a snake pattern
        for (int i = 0; i < size; i++) {
            if (i % 2 == 0) { // Left to right
                for (int j = 0; j < size; j++) {
                    expectedOrder[index++] = i * size + j;
                }
            } else { // Right to left
                for (int j = size - 1; j >= 0; j--) {
                    expectedOrder[index++] = i * size + j;
                }
            }
        }

        // Flatten the grid for comparison
        int[] flatGrid = new int[size * size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                flatGrid[i * size + j] = grid[i][j];
            }
        }

        // Sort indices by value in descending order
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < flatGrid.length; i++) {
            if (flatGrid[i] > 0) {
                indices.add(i);
            }
        }
        indices.sort((a, b) -> Integer.compare(flatGrid[b], flatGrid[a]));

        // Compare actual order with snake pattern
        // Award points for tiles that follow the pattern
        for (int i = 0; i < indices.size() - 1; i++) {
            int currentPos = indices.get(i);
            int nextPos = indices.get(i + 1);

            // Find positions in the expected order
            int currentExpectedIndex = -1;
            int nextExpectedIndex = -1;

            for (int j = 0; j < expectedOrder.length; j++) {
                if (expectedOrder[j] == currentPos)
                    currentExpectedIndex = j;
                if (expectedOrder[j] == nextPos)
                    nextExpectedIndex = j;
            }

            // Award points if they are adjacent in the expected order
            if (Math.abs(currentExpectedIndex - nextExpectedIndex) == 1) {
                score += Math.min(flatGrid[currentPos], flatGrid[nextPos]);
            }
        }

        return score;
    }

    // Calculate a score based on how well the grid maintains monotonicity
    private int calculateMonotonicity(int[][] grid) {
        int[] scores = new int[4]; // scores for all 4 possible directions

        // Calculate score for monotonicity in all directions
        for (int i = 0; i < grid.length; i++) {
            for (int j = 1; j < grid[i].length; j++) {
                if (grid[i][j - 1] > grid[i][j]) { // Left to right decreasing
                    scores[0] += grid[i][j - 1] - grid[i][j];
                } else { // Left to right increasing
                    scores[1] += grid[i][j] - grid[i][j - 1];
                }
            }
        }

        for (int j = 0; j < grid[0].length; j++) {
            for (int i = 1; i < grid.length; i++) {
                if (grid[i - 1][j] > grid[i][j]) { // Top to bottom decreasing
                    scores[2] += grid[i - 1][j] - grid[i][j];
                } else { // Top to bottom increasing
                    scores[3] += grid[i][j] - grid[i - 1][j];
                }
            }
        }

        // Return the maximum monotonicity score
        return 2 * Math.max(Math.max(scores[0], scores[1]), Math.max(scores[2], scores[3]));
    }

    private Board simulateMove(Board board, String move) {
        // Create a deep copy of the board
        Board simulatedBoard = copyBoard(board);

        // Track original state for comparison
        int[][] originalState = new int[simulatedBoard.getGrid().length][simulatedBoard.getGrid()[0].length];
        for (int i = 0; i < simulatedBoard.getGrid().length; i++) {
            System.arraycopy(simulatedBoard.getGrid()[i], 0, originalState[i], 0, simulatedBoard.getGrid()[i].length);
        }

        // Perform the move on our copy
        boolean moved;
        switch (move) {
            case "up":
                moved = moveUp(simulatedBoard.getGrid());
                break;
            case "down":
                moved = moveDown(simulatedBoard.getGrid());
                break;
            case "left":
                moved = moveLeft(simulatedBoard.getGrid());
                break;
            case "right":
                moved = moveRight(simulatedBoard.getGrid());
                break;
            default:
                return null;
        }

        // Check if the move actually changed anything
        if (!moved) {
            return null;
        }

        // Add a new tile (2 or 4) at a random empty position as the game would
        addRandomTile(simulatedBoard.getGrid());

        return simulatedBoard;
    }

    // Implementations of the move operations
    private boolean moveUp(int[][] grid) {
        boolean moved = false;
        int size = grid.length;

        for (int j = 0; j < size; j++) {
            int mergeIndex = 0; // Position where next merge can happen
            for (int i = 1; i < size; i++) {
                if (grid[i][j] != 0) {
                    int k = i;
                    // Move the tile up as far as possible
                    while (k > mergeIndex && grid[k - 1][j] == 0) {
                        grid[k - 1][j] = grid[k][j];
                        grid[k][j] = 0;
                        k--;
                        moved = true;
                    }

                    // Check for a merge
                    if (k > 0 && grid[k - 1][j] == grid[k][j]) {
                        grid[k - 1][j] *= 2;
                        grid[k][j] = 0;
                        mergeIndex = k; // Next merge can happen after this position
                        moved = true;
                    }
                }
            }
        }

        return moved;
    }

    private boolean moveDown(int[][] grid) {
        boolean moved = false;
        int size = grid.length;

        for (int j = 0; j < size; j++) {
            int mergeIndex = size - 1; // Position where next merge can happen
            for (int i = size - 2; i >= 0; i--) {
                if (grid[i][j] != 0) {
                    int k = i;
                    // Move the tile down as far as possible
                    while (k < mergeIndex && grid[k + 1][j] == 0) {
                        grid[k + 1][j] = grid[k][j];
                        grid[k][j] = 0;
                        k++;
                        moved = true;
                    }

                    // Check for a merge
                    if (k < size - 1 && grid[k + 1][j] == grid[k][j]) {
                        grid[k + 1][j] *= 2;
                        grid[k][j] = 0;
                        mergeIndex = k; // Next merge can happen before this position
                        moved = true;
                    }
                }
            }
        }

        return moved;
    }

    private boolean moveLeft(int[][] grid) {
        boolean moved = false;
        int size = grid.length;

        for (int i = 0; i < size; i++) {
            int mergeIndex = 0; // Position where next merge can happen
            for (int j = 1; j < size; j++) {
                if (grid[i][j] != 0) {
                    int k = j;
                    // Move the tile left as far as possible
                    while (k > mergeIndex && grid[i][k - 1] == 0) {
                        grid[i][k - 1] = grid[i][k];
                        grid[i][k] = 0;
                        k--;
                        moved = true;
                    }

                    // Check for a merge
                    if (k > 0 && grid[i][k - 1] == grid[i][k]) {
                        grid[i][k - 1] *= 2;
                        grid[i][k] = 0;
                        mergeIndex = k; // Next merge can happen after this position
                        moved = true;
                    }
                }
            }
        }

        return moved;
    }

    private boolean moveRight(int[][] grid) {
        boolean moved = false;
        int size = grid.length;

        for (int i = 0; i < size; i++) {
            int mergeIndex = size - 1; // Position where next merge can happen
            for (int j = size - 2; j >= 0; j--) {
                if (grid[i][j] != 0) {
                    int k = j;
                    // Move the tile right as far as possible
                    while (k < mergeIndex && grid[i][k + 1] == 0) {
                        grid[i][k + 1] = grid[i][k];
                        grid[i][k] = 0;
                        k++;
                        moved = true;
                    }

                    // Check for a merge
                    if (k < size - 1 && grid[i][k + 1] == grid[i][k]) {
                        grid[i][k + 1] *= 2;
                        grid[i][k] = 0;
                        mergeIndex = k; // Next merge can happen before this position
                        moved = true;
                    }
                }
            }
        }

        return moved;
    }

    private void addRandomTile(int[][] grid) {
        List<int[]> emptyCells = new ArrayList<>();

        // Find all empty cells
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == 0) {
                    emptyCells.add(new int[] { i, j });
                }
            }
        }

        if (!emptyCells.isEmpty()) {
            // Choose a random empty cell
            int[] cell = emptyCells.get(random.nextInt(emptyCells.size()));

            // Place a 2 (90% chance) or 4 (10% chance)
            grid[cell[0]][cell[1]] = (random.nextDouble() < 0.9) ? 2 : 4;
        }
    }
}