package org.dreambot.walker.dax.models;

import java.util.List;

public class PathResult {
    private PathStatus pathStatus;
    private List<Point3D> path;
    private int cost;

    public PathStatus getPathStatus() {
        return this.pathStatus;
    }

    public List<Point3D> getPath() {
        return this.path;
    }

    public int getCost() {
        return this.cost;
    }
}
