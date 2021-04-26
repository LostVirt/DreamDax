package org.dreambot.walker.dax.engine.pathfinding;





import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class BFSMapCache extends BFS {
    private PathTile[][] parentMap;
    private int[][] costMap;
    private int accessibleArea;

    public BFSMapCache() {
        this(Players.localPlayer().getTile(), new Region());
    }

    public BFSMapCache(final Tile start, final Region region) {
        super(region);
        calculate(start);
    }

    @Override
    List<PathTile> getPath(final PathTile start, final PathTile end) {
        final List<PathTile> path = new ArrayList<>();
        PathTile temp = end;
        while (temp != null) {
            path.add(temp);
            temp = getParent(temp);
        }
        Collections.reverse(path);
        return path;
    }

    public boolean canReach(final Tile position) {
        if (!this.getRegion().contains(position)) return false;
        if (position.equals(Players.localPlayer().getTile())) return true;
        final PathTile pathTile = getTile(position);
        return pathTile != null && getCost(pathTile) != Integer.MAX_VALUE;
    }

    public int getMoveCost(final Tile position) {
        if (!this.getRegion().contains(position)) return Integer.MAX_VALUE;
        if (position.equals(Players.localPlayer().getTile())) return 0;
        PathTile pathTile = getTile(position);
        return pathTile != null ? getCost(pathTile) : Integer.MAX_VALUE;
    }

    public Tile getRandom(final int maxDistance) {
        for (int i = 0; i < 50000; ++i) {
            final int x = Calculations.random(0, 103);
            final int y = Calculations.random(0, 103);
            if (costMap[x][y] < maxDistance) {
                return new Tile(this.getRegion().getBase().getX() + x, this.getRegion().getBase().getY() + y);
            }
        }
        return null;
    }

    private int getCost(final PathTile pathTile) {
        return costMap[pathTile.getX() - getBase().getX()][pathTile.getY() - getBase().getY()];
    }

    public Tile getParent(final Tile position) {
        if (!this.getRegion().contains(position)) return null;
        PathTile pathTile = getTile(position);
        if (pathTile == null) return null;
        return getParent(pathTile) != null ? getParent(pathTile).toPosition() : null;

    }

    private PathTile getParent(final PathTile pathTile) {
        return this.parentMap[pathTile.getX() - getBase().getX()][pathTile.getY() - getBase().getY()];
    }

    public int getAccessibleArea() {
        return this.accessibleArea;
    }

    private void calculate(final Tile start) {
        parentMap = new PathTile[getMap().length][getMap().length];
        costMap = new int[getMap().length][getMap().length];

        // Initialize CostMap.
        for (int i = 0; i < costMap.length; i++) for (int j = 0; j < costMap[i].length; j++) costMap[i][j] = Integer.MAX_VALUE;

        PathTile initial = getTile(start);
        if (initial == null || initial.isBlocked()) return;

        LinkedList<PathTile> queue = new LinkedList<>();
        queue.push(initial);
        costMap[initial.getX() - getBase().getX()][initial.getY() - getBase().getY()] = 0;

        while (!queue.isEmpty()) {
            PathTile tile = queue.pop();
            accessibleArea++;
            for (PathTile neighbor : getNeighbors(tile)) {
                if (costMap[neighbor.getX() - getBase().getX()][neighbor.getY() - getBase().getY()] != Integer.MAX_VALUE) continue;
                costMap[neighbor.getX() - getBase().getX()][neighbor.getY() - getBase().getY()] = costMap[tile.getX() - getBase().getX()][tile.getY() - getBase().getY()] + 1;
                parentMap[neighbor.getX() - getBase().getX()][neighbor.getY() - getBase().getY()] = tile;
                queue.addLast(neighbor);
            }
        }
    }
}
