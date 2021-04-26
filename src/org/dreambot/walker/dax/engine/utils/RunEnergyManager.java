package org.dreambot.walker.dax.engine.utils;


import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.walking.impl.Walking;

public class RunEnergyManager {
    private int runActivation;

    public RunEnergyManager() {
        runActivation = getRandomRunTarget();
    }

    public void trigger() {
        if (Walking.isRunEnabled()) return;
        if (Walking.getRunEnergy() >= runActivation) {
            Walking.toggleRun();
            MethodProvider.sleep(1000, 2000);
            runActivation = getRandomRunTarget();
        }
    }

    private int getRandomRunTarget() {
        return Calculations.random(1, 30);
    }
}
