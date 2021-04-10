package client.routing;

import client.model.Cell;
import client.model.enums.CellType;
import client.utils.MapMemory;
import client.utils.Point;

import java.util.*;

class RouteFinder {
    private final MapMemory mapMemory;

    public RouteFinder(MapMemory mapMemory) {
        this.mapMemory = mapMemory;
    }

    public Queue<Point> getRoute(Point from, DistScorer distScorer, HeuristicScorer heuristicScorer, TargetCheckRule targetCheckRule) throws IllegalStateException {
        Queue<RouteNode> openSet = new PriorityQueue<>();
        Map<Point, RouteNode> pointNodeMap = new HashMap<>();

        RouteNode start = new RouteNode(from, null, 0.0, heuristicScorer.calculateHeuristicCost(from));
        openSet.add(start);
        pointNodeMap.put(from, start);

        while (!openSet.isEmpty()) {
            RouteNode next = openSet.poll();
            if (targetCheckRule.isTarget(next)) {
                Queue<Point> route = new LinkedList<>();
                RouteNode currentNode = next;
                do {
                    route.add(currentNode.getSelfPos());
                    currentNode = pointNodeMap.get(currentNode.getParentPos());
                } while (currentNode != null);
                return route;
            }
            List<Point> neighbors = getNodeNeighbors(next);
            neighbors.forEach(neighborPos -> {
                RouteNode neighbor = pointNodeMap.get(neighborPos);
                if (neighbor == null) {
                    neighbor = pointNodeMap.getOrDefault(neighborPos, new RouteNode(neighborPos));
                    pointNodeMap.put(neighborPos, neighbor);
                }

                double newScore = next.getRouteScore() + distScorer.calculateCost(next.getSelfPos(), neighbor.getSelfPos());
                if (newScore < neighbor.getRouteScore()) {
                    neighbor.setParentPos(next.getSelfPos());
                    neighbor.setRouteScore(newScore);
                    neighbor.setEstimatedScore(newScore + heuristicScorer.calculateHeuristicCost(neighborPos));
                    openSet.add(neighbor);
                }
            });
        }

        throw new IllegalStateException("Route not found!");
    }

    private List<Point> getNodeNeighbors(RouteNode node) {
        Point cellPos = node.getSelfPos();
        Cell cell = mapMemory.getCell(cellPos);
        if (cell.getType() == CellType.WALL || cell.getType() == null) {
            return new ArrayList<>();
        }
        return mapMemory.getNeighborPoints(cellPos);
    }
}
