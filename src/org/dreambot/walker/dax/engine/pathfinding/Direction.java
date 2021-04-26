package org.dreambot.walker.dax.engine.pathfinding;

public enum Direction {
    NORTH(new int[]{0, 1}, false),
    EAST(new int[]{1, 0}, false),
    SOUTH(new int[]{0, -1}, false),
    WEST(new int[]{-1, 0}, false),
    NORTH_EAST(new int[]{1, 1}, true),
    SOUTH_WEST(new int[]{-1, -1}, true),
    SOUTH_EAST(new int[]{1, -1}, true),
    NORTH_WEST(new int[]{-1, 1}, true);

    private int[] matrix;
    private boolean diagonal;

    Direction(final int[] matrix, final boolean diagonal) {
        this.matrix = matrix;
        this.diagonal = diagonal;
    }

    public boolean isDiagonal() {
        return this.diagonal;
    }

    public int translateX(final int initial) {
        return initial + this.matrix[0];
    }

    public int translateY(final int initial) {
        return initial + this.matrix[1];
    }
}
