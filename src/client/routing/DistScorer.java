package client.routing;

import client.utils.Point;

public interface DistScorer {
    double calculateCost(Point from, Point to);
}
