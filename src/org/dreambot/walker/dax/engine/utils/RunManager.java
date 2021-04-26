package org.dreambot.walker.dax.engine.utils;


import org.dreambot.api.methods.interactive.Players;

public class RunManager {
    private long initial;

    public RunManager() {
        initial = System.currentTimeMillis();
    }

    public boolean isWalking() {
        return System.currentTimeMillis() - initial < 1300 || Players.localPlayer().isMoving();
    }
}
