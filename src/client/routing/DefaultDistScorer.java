package client.routing;

import client.utils.Point;

public class DefaultDistScorer implements DistScorer{
    @Override
    public double calculateCost(Point from, Point to) {
        return 1;
    }
}
