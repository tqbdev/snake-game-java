package logic;

import java.util.Random;

import javafx.scene.paint.Color;

public class Grid {
    /**
     * The side length of each square point in the grid.
     */
    public static final int SIZE = 10;
    public static final Color COLOR = new Color(0.1, 0.1, 0.1, 1);

    private final int cols;     // The number of columns
    private final int rows;     // The number of rows

    public Grid(final double width, final double height) {
        rows = (int) width / SIZE;
        cols = (int) height / SIZE;
    }

    public Point wrap(Point point) {
        int x = point.getX();
        int y = point.getY();
        if (x >= rows) x = 0;
        if (y >= cols) y = 0;
        if (x < 0) x = rows - 1;
        if (y < 0) y = cols - 1;
        return new Point(x, y);
    }


    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }

    public double getWidth() {
        return rows * SIZE;
    }

    public double getHeight() {
        return cols * SIZE;
    }
}