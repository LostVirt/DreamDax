package org.dreambot.walker.dax.models;

public class IntPair {
    private int key;
    private int value;

    public IntPair() {
    }

    public IntPair(int key, int value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return this.key;
    }

    public int getValue() {
        return this.value;
    }
}
