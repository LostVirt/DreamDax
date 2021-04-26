package org.dreambot.walker.dax.engine.utils;

import org.dreambot.walker.dax.dreamutils.GameObjectHandler;
import org.dreambot.walker.dax.engine.BrokenPathHandler;
import org.dreambot.walker.dax.engine.definitions.PathHandleState;
import org.dreambot.walker.dax.engine.definitions.WalkCondition;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.wrappers.interactive.GameObject;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

public class LockPickHandler {
    private static final Pattern PICK_LOCK_ACTION = Pattern.compile("(?i)pick.*lock");
    private static final Tile HAM_TRAPDOOR = new Tile(3166, 3251, 0);

    public static PathHandleState handle(Tile position, Tile destination, WalkCondition walkCondition) {
        if (position.equals(HAM_TRAPDOOR)) return hamHideOutHandler(position, destination, walkCondition);
        return pickLock(position, destination, walkCondition);
    }

    private static GameObject getPickableObject(final Tile position) {
        GameObject[] loaded = GameObjects.all(gameObject -> GameObjectHandler.hasAction(gameObject, s -> s.matches("(?i)pick.*lock"))).toArray(new GameObject[0]);
        return Arrays.stream(loaded).min(Comparator.comparingDouble(o -> o.distance(position))).orElse(null);
    }

    private static PathHandleState pickLock(final Tile position, final Tile destination, final WalkCondition walkCondition) {
        GameObject gameObject = getPickableObject(position);
        if (gameObject == null) return PathHandleState.FAILED;

        // Pick the lock
        if (!gameObject.interact(s -> PICK_LOCK_ACTION.matcher(s).matches())) return PathHandleState.FAILED;
        AtomicBoolean exitCondition = new AtomicBoolean(false);
        if (MethodProvider.sleepUntil(() -> {
            if (walkCondition.getAsBoolean()) {
                exitCondition.set(true);
                return true;
            }
            return destination.equals(Players.localPlayer().getTile());
        }, Calculations.random(800, 2000)) && exitCondition.get()) {
            return PathHandleState.EXIT;
        }
        return PathHandleState.SUCCESS; // Multiple attempts required usually.
    }

    private static PathHandleState hamHideOutHandler(final Tile position, final Tile destination, final WalkCondition walkCondition) {
        GameObject gameObject = getPickableObject(position);

        // Lock already picked
        if (gameObject == null) return BrokenPathHandler.handle(position, destination, walkCondition);

        // Pick the lock
        if (!gameObject.interact(s -> PICK_LOCK_ACTION.matcher(s).matches())) return PathHandleState.FAILED;
        AtomicBoolean exitCondition = new AtomicBoolean(false);
        if (MethodProvider.sleepUntil(() -> {
            if (walkCondition.getAsBoolean()) {
                exitCondition.set(true);
                return true;
            }
            return destination.equals(Players.localPlayer().getTile()) || getPickableObject(position) == null;
        }, 15000) && exitCondition.get()) {
            return PathHandleState.EXIT;
        }
        return destination.equals(Players.localPlayer().getTile()) ? PathHandleState.SUCCESS : PathHandleState.FAILED;
    }

}
