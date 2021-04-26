package org.dreambot.walker.dax.engine.pathfinding;

import java.util.List;

public class BFS extends Pathfinder {
    public BFS(final Region region) {
        super(region);
    }

    @Override
    List<PathTile> getPath(final PathTile start, final PathTile end) {
        throw new IllegalStateException("XD");
    }
}
