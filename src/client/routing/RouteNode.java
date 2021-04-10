package client.routing;

import client.utils.Point;

public class RouteNode implements Comparable<RouteNode> {
    private final Point selfPos;
    private Point parentPos;
    private double routeScore;
    private double estimatedScore;

    public RouteNode(Point selfPos) {
        this(selfPos, null, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    public RouteNode(Point selfPos, Point parentPos, double routeScore, double estimatedScore) {
        this.selfPos = selfPos;
        this.parentPos = parentPos;
        this.routeScore = routeScore;
        this.estimatedScore = estimatedScore;
    }

    public Point getSelfPos() {
        return selfPos;
    }

    public Point getParentPos() {
        return parentPos;
    }

    public double getRouteScore() {
        return routeScore;
    }

    public double getEstimatedScore() {
        return estimatedScore;
    }

    public void setParentPos(Point parentPos) {
        this.parentPos = parentPos;
    }

    public void setRouteScore(double routeScore) {
        this.routeScore = routeScore;
    }

    public void setEstimatedScore(double estimatedScore) {
        this.estimatedScore = estimatedScore;
    }

    @Override
    public int compareTo(RouteNode other) {
        if (this.estimatedScore > other.estimatedScore){
            return 1;
        } else if (this.estimatedScore < other.estimatedScore) {
            return -1;
        }
        return 0;
    }
}
