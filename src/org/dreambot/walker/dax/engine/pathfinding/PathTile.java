

package org.dreambot.walker.dax.engine.pathfinding;


import org.dreambot.api.methods.map.Tile;

public class PathTile {
    private int x;
    private int y;
    private int z;
    private boolean blocked;
    private boolean blockedN;
    private boolean blockedE;
    private boolean blockedS;
    private boolean blockedW;

    PathTile(final int x, final int y, final int z, final int flag) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.blocked = TileFlag.BLOCKED.valid(flag);
        this.blockedN = TileFlag.BLOCKED_NORTH.valid(flag);
        this.blockedE = TileFlag.BLOCKED_EAST.valid(flag);
        this.blockedS = TileFlag.BLOCKED_SOUTH.valid(flag);
        this.blockedW = TileFlag.BLOCKED_WEST.valid(flag);
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    public boolean isBlocked() {
        return this.blocked;
    }

    public boolean isBlockedN() {
        return this.blockedN;
    }

    public boolean isBlockedE() {
        return this.blockedE;
    }

    public boolean isBlockedS() {
        return this.blockedS;
    }

    public boolean isBlockedW() {
        return this.blockedW;
    }

    public Tile toPosition() {
        return new Tile(this.x, this.y, this.z);
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PathTile)) return false;

        PathTile tile = (PathTile) o;

        if (getX() != tile.getX()) return false;
        if (getY() != tile.getY()) return false;
        if (getZ() != tile.getZ()) return false;
        if (isBlocked() != tile.isBlocked()) return false;
        if (isBlockedN() != tile.isBlockedN()) return false;
        if (isBlockedE() != tile.isBlockedE()) return false;
        if (isBlockedS() != tile.isBlockedS()) return false;
        return isBlockedW() == tile.isBlockedW();
    }

    @Override
    public int hashCode() {
        int result = getX();
        result = 31 * result + getY();
        result = 31 * result + getZ();
        result = 31 * result + (isBlocked() ? 1 : 0);
        result = 31 * result + (isBlockedN() ? 1 : 0);
        result = 31 * result + (isBlockedE() ? 1 : 0);
        result = 31 * result + (isBlockedS() ? 1 : 0);
        result = 31 * result + (isBlockedW() ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("(x=%d, y=%d, z=%d)", this.x, this.y, this.z);
    }
}
