package client.routing;

import client.World;
import client.model.Cell;
import client.model.enums.CellType;
import client.model.enums.Direction;
import client.utils.MapMemory;
import client.utils.Point;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class RoutingEngine {
    private final MapMemory mapMemory;
    private final RouteFinder routeFinder;
    private RoutingState currentState;
    private Queue<Point> currentRoute =  new LinkedList<>();
    private Point currentTarget;
    private World currentWorld;

    public RoutingEngine(MapMemory mapMemory) {
        this.mapMemory = mapMemory;
        this.routeFinder = new RouteFinder(mapMemory);
    }

    public void setTarget(int x, int y) {
        this.currentTarget = new Point(x, y);
    }

    public void setState(RoutingState state) {
        this.currentState = state;
    }

    public Direction getNextMove(World world) {
        currentWorld = world;
        switch (currentState) {
            case EXPLORING:
                if (!currentRoute.isEmpty()) {
                    Point nextPoint = currentRoute.poll();
                    return getDirection(nextPoint);
                }
                DistScorer distScorer = new DefaultDistScorer();
                HeuristicScorer heuristicScorer = from -> {
                    int minDist = Integer.MAX_VALUE;
                    Set<Point> border = mapMemory.getUnknownBorder();
                    for (Point point : border) {
                        int newDist = mapMemory.getDistanceBetweenPoints(from, point);
                        if (newDist < minDist) {
                            minDist = newDist;
                        }
                    }
                    return minDist;
                };
                TargetCheckRule targetCheckRule = node -> {
                    Point pos = node.getSelfPos();
                    Cell cell = mapMemory.getCell(pos);
                    if (cell.getType() == CellType.WALL || cell.getType() == null) {
                        return false;
                    }
                    Set<Point> border = mapMemory.getUnknownBorder();
                    int viewDist = world.getAnt().getViewDistance();
                    for (Point point : border) {
                        int newDist = mapMemory.getDistanceBetweenPoints(pos, point);
                        if (newDist < viewDist) {
                            return true;
                        }
                    }
                    return false;
                };
                Point currentPos = new Point(world.getAnt().getCurrentX(), world.getAnt().getCurrentY());
                try {
                    this.currentRoute = routeFinder.getRoute(currentPos, distScorer, heuristicScorer, targetCheckRule);
                } catch (IllegalStateException e) {
                    System.err.println(e.getMessage());
                    return Direction.CENTER;
                }
                currentRoute.poll();
                Point nextPoint = currentRoute.poll();
                if (nextPoint == null)
                    return Direction.CENTER;
                Direction direction = getDirection(nextPoint);
                System.out.println("Direction" + direction + " Next point: " + nextPoint + " Route: " + currentRoute);
                return direction;
            default:
                return Direction.CENTER;
        }
    }

    private Direction getDirection(Point currentTarget) {
        int x = currentWorld.getAnt().getCurrentX(), y = currentWorld.getAnt().getCurrentY();
        if (currentTarget.equals(mapMemory.normalizeCoordinates(x + 1, y))) {
            return Direction.RIGHT;
        }
        if (currentTarget.equals(mapMemory.normalizeCoordinates(x - 1, y))) {
            return Direction.LEFT;
        }
        if (currentTarget.equals(mapMemory.normalizeCoordinates(x, y + 1))) {
            return Direction.DOWN;
        }
        if (currentTarget.equals(mapMemory.normalizeCoordinates(x, y - 1))) {
            return Direction.UP;
        }
        return Direction.CENTER;
    }
}
