package interfaces;

import model.GameState;

public interface GameStateManager {

    void saveState(GameState gameState);

    GameState restorePreviousState();

    boolean canUndo();

    void clearHistory();

    int getHistorySize();
}
