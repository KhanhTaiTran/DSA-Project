package interfaces;

import model.Grid;

public interface ScoreCalculator {

    int calculateMergeScore(int mergedValue);

    int calculateBonusScore(Grid grid);

    void reset();
}
