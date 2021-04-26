package org.dreambot.walker.dax.engine.pathfinding;




import org.dreambot.api.methods.map.Tile;

import java.util.ArrayList;
import java.util.List;

public abstract class Pathfinder {
    private Region region;
    private Tile base;
    private PathTile[][] map;

    public Pathfinder(final Region region) {
        this.region = region;
        this.base = region.getBase();
        this.map = region.getMap();
    }

    abstract List<PathTile> getPath(PathTile p0, PathTile p1);

    List<Tile> getPath(Tile start, Tile end) {
        PathTile a = getTile(start);
        if (a == null || a.isBlocked()) return null;
        PathTile b = getTile(end);
        if (b == null || b.isBlocked()) return null;
        return convert(getPath(a, b));
    }

    public List<PathTile> getNeighbors(PathTile pathTile) {
        List<PathTile> neighbors = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            if (!isValidDirection(direction, pathTile)) continue;
            PathTile neighbor = getTile(direction, pathTile);
            if (neighbor == null) continue;
            neighbors.add(neighbor);
        }
        return neighbors;
    }

    public PathTile getTile(Tile position) {
        if (!region.contains(position)) return null;
        if (!isValidTile(position.getX(), position.getY())) return null;
        int x = position.getX() - base.getX();
        int y = position.getY() - base.getY();
        return map[x][y];
    }

    public List<Tile> convert(List<PathTile> path) {
        List<Tile> positions = new ArrayList<>();
        for (PathTile pathTile : path) {
            positions.add(new Tile(pathTile.getX(), pathTile.getY(), pathTile.getZ()));
        }
        return positions;
    }

    public Tile getBase() {
        return base;
    }

    public PathTile[][] getMap() {
        return map;
    }

    private boolean isValidDirection(Direction direction, PathTile pathTile) {
        if (pathTile == null) return false;

        PathTile destination = this.getTile(direction, pathTile);
        if (destination == null || destination.isBlocked()) return false;

        if (!direction.isDiagonal()) {
            switch (direction) {
                case NORTH:
                    return !pathTile.isBlockedN();
                case EAST:
                    return !pathTile.isBlockedE();
                case SOUTH:
                    return !pathTile.isBlockedS();
                case WEST:
                    return !pathTile.isBlockedW();
                default:
                    throw new IllegalStateException("There can only be 4 non diagonal neighbors...");
            }
        }
        switch (direction) {
            case NORTH_EAST:
                return isValidDirection(Direction.NORTH, pathTile) && isValidDirection(Direction.EAST, getTile(Direction.NORTH, pathTile)) && isValidDirection(Direction.EAST, pathTile) && isValidDirection(Direction.NORTH, getTile(Direction.EAST, pathTile));
            case SOUTH_WEST:
                return isValidDirection(Direction.SOUTH, pathTile) && isValidDirection(Direction.WEST, getTile(Direction.SOUTH, pathTile)) && isValidDirection(Direction.WEST, pathTile) && isValidDirection(Direction.SOUTH, getTile(Direction.WEST, pathTile));
            case SOUTH_EAST:
                return isValidDirection(Direction.SOUTH, pathTile) && isValidDirection(Direction.EAST, getTile(Direction.SOUTH, pathTile)) && isValidDirection(Direction.EAST, pathTile) && isValidDirection(Direction.SOUTH, getTile(Direction.EAST, pathTile));
            case NORTH_WEST:
                return isValidDirection(Direction.NORTH, pathTile) && isValidDirection(Direction.WEST, getTile(Direction.NORTH, pathTile)) && isValidDirection(Direction.WEST, pathTile) && isValidDirection(Direction.NORTH, getTile(Direction.WEST, pathTile));
            default:
                throw new IllegalStateException("No handler for direction: " + direction);
        }
    }

    private PathTile getTile(Direction direction, PathTile pathTile) {
        int x = direction.translateX(pathTile.getX()) - base.getX();
        int y = direction.translateY(pathTile.getY()) - base.getY();
        return isValidTile(x, y) ? map[x][y] : null;
    }

    private boolean isValidTile(int x, int y) {
        if (x > 104 || y > 104) {
            x -= base.getX();
            y -= base.getY();
        }
        return x >= 0 && y >= 0 && x < map.length && y < map.length;
    }

    public Region getRegion() {
        return region;
    }
}
