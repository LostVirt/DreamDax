package org.dreambot.walker.dax.dreamutils;

import org.dreambot.api.wrappers.interactive.GameObject;

import java.util.function.Predicate;

public class GameObjectHandler {

    public static boolean hasAction(GameObject gameObject, Predicate<String> predicate) {
        if (gameObject.getActions() != null) {
            for (String action : gameObject.getActions()) {
                if (predicate.test(action)) {
                    return true;
                }
            }
        }
        return false;
    }

}
