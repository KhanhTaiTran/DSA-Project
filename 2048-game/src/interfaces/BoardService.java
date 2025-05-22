package interfaces;

import model.Grid;
import enums.Direction;

public interface BoardService {

    Grid moveTiles(Grid grid, Direction direction);

    boolean canMove(Grid grid, Direction direction);

    Grid addRandomTile(Grid grid);

    boolean hasEmptySpaces(Grid grid);

    boolean canMerge(Grid grid);
}
