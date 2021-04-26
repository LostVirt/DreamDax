package org.dreambot.walker.dax.models;


import org.dreambot.api.methods.map.Tile;

public class Point3D {
    private int x;
    private int y;
    private int z;

    public Point3D(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
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

    public static Point3D from(Tile position) {
        return new Point3D(position.getX(), position.getY(), position.getZ());
    }

    public Tile toPosition() {
        return new Tile(this.x, this.y, this.z);
    }
}
