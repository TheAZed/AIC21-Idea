package client.routing;

import client.utils.Point;

public interface HeuristicScorer {
    double calculateHeuristicCost(Point from);
}
