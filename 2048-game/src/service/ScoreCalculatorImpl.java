package service;

import interfaces.ScoreCalculator;
import model.Grid;

public class ScoreCalculatorImpl implements ScoreCalculator {

    @Override
    public int calculateMergeScore(int mergedValue) {
        return mergedValue;
    }

    @Override
    public int calculateBonusScore(Grid grid) {
        int bonus = 0;

        // Bonus for empty cells
        bonus += grid.getEmptyCellCount() * 10;

        // Bonus for higher tiles
        int highestTile = grid.getHighestTile();
        if (highestTile >= 1024) {
            bonus += highestTile / 2;
        }

        // Bonus for organized tiles (higher values in corners)
        bonus += calculateOrganizationBonus(grid);

        return bonus;
    }

    private int calculateOrganizationBonus(Grid grid) {
        int bonus = 0;
        int size = grid.getSize();

        // Check corners for high values
        int[] corners = {
                grid.getValue(0, 0),
                grid.getValue(0, size - 1),
                grid.getValue(size - 1, 0),
                grid.getValue(size - 1, size - 1)
        };

        for (int corner : corners) {
            if (corner >= 256) {
                bonus += corner / 4;
            }
        }

        return bonus;
    }

    @Override
    public void reset() {
        // Nothing to reset for this implementation
    }
}
