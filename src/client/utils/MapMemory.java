package client.utils;

import client.World;
import client.model.Cell;
import client.routing.NullCell;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MapMemory {
    private final Cell[][] cells;
    private final int width;
    private final int height;
    private final Set<Point> unknownBorder;

    public MapMemory(int width, int height) {
        this.width = width;
        this.height = height;
        this.cells = new Cell[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                cells[i][j] = new NullCell(i, j);
            }
        }
        unknownBorder = new HashSet<>();
    }

    public void updateData(World world) {
        int d = world.getAnt().getViewDistance();
        int currX = world.getAnt().getCurrentX();
        int currY = world.getAnt().getCurrentY();

        for (int dx = -d; dx < d; dx++) {
            for (int dy = -d; dy < d; dy++) {
                Cell cell = world.getAnt().getVisibleMap().getRelativeCell(dx, dy);
                if (cell != null) {
                    updateCell(currX + dx, currY + dy, cell);
                }
            }
        }
    }

    private void updateCell(int x, int y, Cell newCell) {
        Point normalPoint = normalizeCoordinates(x, y);
        Cell currentCell = getCellNormalized(normalPoint);
        if (currentCell.getType() == null) {
            unknownBorder.remove(normalPoint);
            getNeighborPoints(normalPoint).forEach(point -> {
                if (getCellNormalized(point).getType() == null) {
                    unknownBorder.add(point);
                }
            });
        }
        setCellNormalized(normalPoint, newCell);
    }

    public void setCell(int x, int y, Cell cell) {
        Point normalPoint = normalizeCoordinates(x, y);
        setCellNormalized(normalPoint, cell);
    }

    public Cell getCell(int x, int y) {
        Point normalPoint = normalizeCoordinates(x, y);
        return getCellNormalized(normalPoint);
    }

    public Cell getCell(Point point) {
        Point normalPoint = normalizeCoordinates(point);
        return getCellNormalized(normalPoint);
    }

    private Cell getCellNormalized(Point point) {
        return cells[point.x][point.y];
    }

    private void setCellNormalized(Point point, Cell cell) {
        cells[point.x][point.y] = cell;
    }

    public Point normalizeCoordinates(Point point) {
        return normalizeCoordinates(point.x, point.y);
    }

    public Point normalizeCoordinates(int x, int y) {
        x %= width;
        y %= height;

        if (x < 0) {
            x += width;
        }
        if (y < 0) {
            y += height;
        }
        return new Point(x, y);
    }

    public List<Point> getNeighborPoints(Point centerPoint) {
        centerPoint = normalizeCoordinates(centerPoint);
        int x = centerPoint.x, y = centerPoint.y;
        List<Point> neighbors = new ArrayList<>();
        neighbors.add(normalizeCoordinates(x + 1, y));
        neighbors.add(normalizeCoordinates(x - 1, y));
        neighbors.add(normalizeCoordinates(x, y + 1));
        neighbors.add(normalizeCoordinates(x, y - 1));
        return neighbors;
    }

    public int getDistanceBetweenPoints(Point first, Point second) {
        int dist = 0;
        int bigX, smallX, bigY, smallY;
        if (first.x > second.x) {
            bigX = first.x;
            smallX = second.x;
        } else {
            bigX = second.x;
            smallX = first.x;
        }
        if (first.y > second.y) {
            bigY = first.y;
            smallY = second.y;
        } else {
            bigY = second.y;
            smallY = first.y;
        }
        dist += Math.min(bigX - smallX, width + smallX - bigX);
        dist += Math.min(bigY - smallY, height + smallY - bigY);
        return dist;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Cell[][] getCells() {
        return cells;
    }

    public Set<Point> getUnknownBorder() {
        return unknownBorder;
    }
}
