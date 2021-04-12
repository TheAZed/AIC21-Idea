package client.utils;

import client.World;
import client.model.Cell;
import client.model.enums.Direction;
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
    private boolean lastTimeExplored = false;

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
        this.lastTimeExplored = false;
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

    public void updateCell(Point point, Cell newCell) {
        this.updateCell(point.x, point.y, newCell);
    }

    public void updateCell(int x, int y, Cell newCell) {
        Point normalPoint = normalizeCoordinates(x, y);
        Cell currentCell = getCellNormalized(normalPoint);
        if (currentCell.getType() == null) {
            this.lastTimeExplored = true;
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

    public List<Point> getBorderPointsForDirection(Point center, Direction direction, int viewRange) {
        List<Point> points = new ArrayList<>();
        int centerX = center.x, centerY = center.y;
        switch (direction) {  // These depend of MapMemory#updateData format
            case RIGHT:
                for (int x = 0; x < viewRange; x++) {
                    points.add(normalizeCoordinates(centerX + x, centerY + viewRange - x));
                    points.add(normalizeCoordinates(centerX + x, centerY - viewRange + x));
                }
                points.add(normalizeCoordinates(centerX + viewRange, centerY));
                return points;
            case UP:
                for (int x = -viewRange; x <= viewRange; x++) {
                    points.add(normalizeCoordinates(centerX + x, centerY + viewRange - Math.abs(x)));
                }
                return points;
            case LEFT:
                points.add(normalizeCoordinates(centerX - viewRange, centerY));
                for (int x = -viewRange + 1; x <= 0; x++) {
                    points.add(normalizeCoordinates(centerX + x, centerY + viewRange + x));
                    points.add(normalizeCoordinates(centerX + x, centerY - viewRange - x));
                }
                return points;
            case DOWN:
                for (int x = -viewRange; x <= viewRange; x++) {
                    points.add(normalizeCoordinates(centerX + x, centerY - viewRange + Math.abs(x)));
                }
                return points;
            default:
                return points;
        }
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

    public boolean isLastTimeExplored() {
        return lastTimeExplored;
    }
}
