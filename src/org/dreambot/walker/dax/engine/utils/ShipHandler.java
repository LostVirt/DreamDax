package org.dreambot.walker.dax.engine.utils;



import org.dreambot.walker.dax.engine.pathfinding.BFSMapCache;
import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.wrappers.interactive.GameObject;

import java.util.Arrays;

public class ShipHandler {
    private ShipHandler() {
    }

    public static boolean isOnShip() {
        return hasShipObjects() && getGangPlank() != null && new BFSMapCache().getAccessibleArea() < 100;
    }

    public static boolean getOffBoat() {
        GameObject gangplank = getGangPlank();
        if (gangplank == null) return false;
        return gangplank.interact(s -> s.matches("(?i)(walk.a)?cross")) && MethodProvider.sleepUntil(() -> !isOnShip(), 5000);
    }

    private static GameObject getGangPlank() {
        GameObject[] gameObjects = GameObjects.all(sceneObject -> sceneObject.distance() < 10 && sceneObject.getName().matches("(?i)(gang.?plank)")).toArray(new GameObject[0]);
        return Arrays.stream(gameObjects).findAny().orElse(null);
    }

    private static boolean hasShipObjects() {
        return GameObjects.all(sceneObject -> sceneObject.distance() < 10 && sceneObject.getName().matches("(?i)(ship.s.+|anchor)")).size() > 0;
    }
}
