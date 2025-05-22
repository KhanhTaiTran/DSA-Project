package interfaces;

import enums.Direction;
import model.GameState;

public interface GameEngine {

    void initializeGame();

    boolean executeMove(Direction direction);

    boolean undoLastMove();

    GameState getGameState();

    boolean isGameOver();

    boolean hasWon();
}
