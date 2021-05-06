package org.dreambot.walker.dax.engine.pathfinding;


import org.dreambot.api.Client;
import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Map;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.wrappers.map.impl.CollisionMap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class Region {
    private PathTile[][] map;
    private Tile base;

    public Region() {
        this(new Tile(Client.getBaseX(), Client.getBaseY(), Client.getPlane()), Client.getCollisionMaps()[Client.getBase().getZ()].getFlags());
    }

    public Region(final Tile b, final int[][] collision) {
        this.base = b;
        map = new PathTile[collision.length][collision[0].length];
        if (map.length != map[0].length) throw new IllegalStateException("Collision data error");
        Tile position = Players.localPlayer().getTile();
        for (int i = 0; i < this.map.length; i++) {
            for (int j = 0; j < this.map[i].length; j++) {
                map[i][j] = new PathTile(i + b.getX(), j + b.getY(), b.getZ(), collision[i][j]);
                if (map[i][j].getX() == position.getX() && map[i][j].getY() == position.getY()) map[i][j].setBlocked(false);
            }
        }

    }

    public boolean contains(Tile position) {
        if (position.getZ() != base.getZ()) return false;
        if (position.getX() < base.getX()) return false;
        if (position.getX() > base.getX() + 104) return false;
        if (position.getY() < base.getY()) return false;
        if (position.getY() > base.getY() + 104) return false;
        return true;
    }

    public Tile getBase() {
        return base;
    }

    public PathTile[][] getMap() {
        return map;
    }
}
