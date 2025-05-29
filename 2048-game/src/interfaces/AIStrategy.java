package interfaces;

import game.Board;

public interface AIStrategy {

    String findBestMove(Board board);

    int evaluateBoard(Board board);
}
