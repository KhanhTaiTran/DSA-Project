package service;

import interfaces.GameStateManager;
import model.GameState;
import java.util.Stack;

public class GameStateManagerImpl implements GameStateManager {
    private final Stack<GameState> stateHistory;
    private final int maxHistorySize;

    public GameStateManagerImpl() {
        this(50); // Default max history size
    }

    public GameStateManagerImpl(int maxHistorySize) {
        this.maxHistorySize = maxHistorySize;
        this.stateHistory = new Stack<>();
    }

    @Override
    public void saveState(GameState gameState) {
        if (gameState == null) {
            throw new IllegalArgumentException("Game state cannot be null");
        }

        // Create a deep copy to ensure immutability
        GameState stateCopy = new GameState(
                gameState.getGrid(),
                gameState.getScore(),
                gameState.isGameOver(),
                gameState.isWon(),
                gameState.getMoveCount());

        stateHistory.push(stateCopy);

        // Limit history size to prevent memory issues
        while (stateHistory.size() > maxHistorySize) {
            stateHistory.remove(0);
        }
    }

    @Override
    public GameState restorePreviousState() {
        if (stateHistory.isEmpty()) {
            return null;
        }

        return stateHistory.pop();
    }

    @Override
    public boolean canUndo() {
        return !stateHistory.isEmpty();
    }

    @Override
    public void clearHistory() {
        stateHistory.clear();
    }

    @Override
    public int getHistorySize() {
        return stateHistory.size();
    }
}
