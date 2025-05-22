package interfaces;

import model.GameState;
import enums.Direction;

public interface AIStrategy {

    Direction getBestMove(GameState gameState);

    double evaluateState(GameState gameState);

    void setSearchDepth(int depth);
}
