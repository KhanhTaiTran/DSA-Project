package interfaces;

/**
 * Interface defining operations for score management
 */
public interface ScoreService {
    int getBestScore();

    boolean updateScore(int score);
}
