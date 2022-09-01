package me.jmang.japi.jmenuapi;

import java.util.function.BinaryOperator;
import java.util.function.Function;

/** Locations in the contents of a page with rows and columns.
 *
 */
public enum PageLocation {
    TOP_LEFT((rows, cols) -> 0),
    TOP_RIGHT((rows, cols) -> cols - 1),
    BOTTOM_LEFT((rows, cols) -> (rows - 1) * cols),
    BOTTOM_RIGHT((rows, cols) -> rows * cols - 1),

    MIDDLE((rows, cols) -> (rows / 2) * cols + (cols / 2)),
    TOP_MIDDLE((rows, cols) -> cols / 2),
    BOTTOM_MIDDLE((rows, cols) -> (rows - 1) * cols + (cols / 2)),
    MIDDLE_LEFT((rows, cols) -> (rows / 2) * cols),
    MIDDLE_RIGHT((rows, cols) -> (rows / 2) * cols + cols - 1);

    private final BinaryOperator<Integer> getIndex;

    PageLocation(BinaryOperator<Integer> getIndex) {
        this.getIndex = getIndex;
    }

    int getIndex(int rows, int cols) {
        return getIndex.apply(rows, cols);
    }
}