package model;

public class GameState {
    private final Grid grid;
    private final int score;
    private final boolean gameOver;
    private final boolean won;
    private final int moveCount;

    public GameState(Grid grid, int score) {
        this(grid, score, false, false, 0);
    }

    public GameState(Grid grid, int score, boolean gameOver, boolean won, int moveCount) {
        this.grid = new Grid(grid); // Create a copy to ensure immutability
        this.score = score;
        this.gameOver = gameOver;
        this.won = won;
        this.moveCount = moveCount;
    }

    public Grid getGrid() {
        return new Grid(grid); // Return a copy to maintain immutability
    }

    public int getScore() {
        return score;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isWon() {
        return won;
    }

    public int getMoveCount() {
        return moveCount;
    }

    public GameState withScore(int newScore) {
        return new GameState(this.grid, newScore, this.gameOver, this.won, this.moveCount);
    }

    public GameState withGrid(Grid newGrid) {
        return new GameState(newGrid, this.score, this.gameOver, this.won, this.moveCount);
    }

    public GameState withGameOver(boolean gameOver) {
        return new GameState(this.grid, this.score, gameOver, this.won, this.moveCount);
    }

    public GameState withWon(boolean won) {
        return new GameState(this.grid, this.score, this.gameOver, won, this.moveCount);
    }

    public GameState withIncrementedMoveCount() {
        return new GameState(this.grid, this.score, this.gameOver, this.won, this.moveCount + 1);
    }

    public GameState withUpdates(Grid newGrid, int newScore, boolean gameOver, boolean won) {
        return new GameState(newGrid, newScore, gameOver, won, this.moveCount + 1);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        GameState other = (GameState) obj;
        return score == other.score &&
                gameOver == other.gameOver &&
                won == other.won &&
                moveCount == other.moveCount &&
                grid.equals(other.grid);
    }

    @Override
    public int hashCode() {
        int result = grid.hashCode();
        result = 31 * result + score;
        result = 31 * result + (gameOver ? 1 : 0);
        result = 31 * result + (won ? 1 : 0);
        result = 31 * result + moveCount;
        return result;
    }

    @Override
    public String toString() {
        return String.format("GameState{score=%d, gameOver=%b, won=%b, moves=%d, grid=\n%s}",
                score, gameOver, won, moveCount, grid.toString());
    }
}
