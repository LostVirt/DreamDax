package org.dreambot.walker.dax.engine;


import org.dreambot.walker.dax.engine.definitions.PathHandleState;
import org.dreambot.walker.dax.engine.definitions.PathLink;
import org.dreambot.walker.dax.engine.definitions.Teleport;
import org.dreambot.walker.dax.engine.definitions.WalkCondition;
import org.dreambot.walker.dax.engine.pathfinding.BFSMapCache;
import org.dreambot.walker.dax.engine.pathfinding.Region;
import org.dreambot.walker.dax.engine.utils.RunManager;
import org.dreambot.walker.dax.engine.utils.ShipHandler;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.ScriptManager;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class PathHandler {
    public static boolean walk(final List<Tile> path, final WalkCondition walkCondition, final int maxRetries, final List<PathLink> pathLinks) {
        if (!handleTeleports(path)) {
            MethodProvider.logError("[DaxWalker] Failed to handle teleports...");
            return false;
        }
        int fail = 0;
        Tile previous = null;
        while (ScriptManager.getScriptManager().isRunning() && !isFinishedPathing(path.get(path.size() - 1))) {
            if (ScriptManager.getScriptManager().isPaused()) {
                MethodProvider.sleep(500);
            } else {
                MethodProvider.sleep(50);
                if (fail >= maxRetries) return false;
                Tile next = furthestTileInPath(path, randomDestinationDistance());
                PathHandleState state = handleNextAction(previous, next, path, walkCondition, pathLinks);
                switch (state) {
                    case SUCCESS:
                        previous = next;
                        fail = 0;
                        break;
                    case FAILED:
                        fail++;
                        previous = next;
                        MethodProvider.sleep(3000);
                        break;
                    case EXIT:
                        return false;

                }
            }
        }
        return true;
    }

    private static PathHandleState handleNextAction(Tile previous, Tile now, List<Tile> path, WalkCondition walkCondition, List<PathLink> pathLinks) {
        if (ShipHandler.isOnShip()) return ShipHandler.getOffBoat() ? PathHandleState.SUCCESS : PathHandleState.FAILED;
        if (now == null) return PathHandleState.FAILED;

        if (previous != null && previous.equals(now)) {
            Tile destination = getNextTileInPath(now, path);
            if (destination != null) {
                MethodProvider.logInfo("[DaxWalker] " + String.format("Disconnected path: (%d,%d,%d) -> (%d,%d,%d)", now.getX(), now.getY(), now.getZ(), destination.getX(), destination.getY(), destination.getZ()));
                PathHandleState pathHandleState = BrokenPathHandler.handlePathLink(now, destination, walkCondition, pathLinks);
                return pathHandleState != null ? pathHandleState : BrokenPathHandler.handle(now, destination, walkCondition);
            }

            if (Walking.getDestination() != null && !Walking.getDestination().equals(now)) {
                MethodProvider.logInfo("[DaxWalker] Clicking supposed last pathTile in path...");

                Walking.walkExact(now);
                MethodProvider.sleep(500);
            }

            return PathHandleState.SUCCESS;
        }


        Tile destination = new BFSMapCache(now, new Region()).getRandom(2);
        if (destination == null) return PathHandleState.FAILED;

        if (!Walking.walkExact(destination)) return PathHandleState.FAILED;

        AtomicBoolean exitCondition = new AtomicBoolean(false);
        RunManager runManager = new RunManager();
        int distance = randomWaitDistance();
        MethodProvider.sleepUntil(() -> {
            if (walkCondition.getAsBoolean()) {
                exitCondition.set(true);
                return true;
            }
            return !runManager.isWalking() || destination.distance() <= distance;
        }, 15000);
        return exitCondition.get() ? PathHandleState.EXIT : PathHandleState.SUCCESS;
    }

    private static boolean isFinishedPathing(Tile destination) {
        if (Players.localPlayer().getTile().equals(destination)) return true;
        Tile walkingTo = Walking.getDestination();
        if (walkingTo != null && walkingTo.equals(destination)) return true;
        return walkingTo != null && walkingTo.distance(destination) < 5 && new BFSMapCache(walkingTo, new Region()).getMoveCost(destination) <= 2;
    }

    private static Tile getNextTileInPath(Tile current, List<Tile> path) {
        for (int i = 0; i < path.size() - 1; ++i) {
            if (path.get(i).equals(current)) return path.get(i + 1);
        }
        return null;
    }

    public static Tile furthestTileInPath(List<Tile> path, int limit) {
        Tile playerPosition = Players.localPlayer().getTile();
        BFSMapCache bfsMapCache = new BFSMapCache(playerPosition, new Region());
        for (int i = path.size() - 1; i >= 0; i--) {
            if (path.get(i).getZ() != playerPosition.getZ()) {
                continue;
            }
            boolean tileDistanceValid = path.get(i).distance() <= limit;
            int moveCost = bfsMapCache.getMoveCost(path.get(i));
            boolean moveCostValid = moveCost <= limit;
            if (tileDistanceValid && moveCostValid) return path.get(i);
        }
        return null;
    }

    private static boolean handleTeleports(List<Tile> path) {
        Tile startPosition = path.get(0);
        Tile playerPosition = Players.localPlayer().getTile();
        for (Teleport teleport : Teleport.values()) {
            if (teleport.getRequirement().satisfies()) {
                if (!teleport.isAtTeleportSpot(playerPosition) && teleport.isAtTeleportSpot(startPosition)) {
                    MethodProvider.log("[PathHandler] Lets use: " + teleport);
                    if (!teleport.trigger()) return false;
                    if (!MethodProvider.sleepUntil(() -> teleport.isAtTeleportSpot(Players.localPlayer().getTile()), 8000)) return false;
                    MethodProvider.log("[Teleport] Using teleport: " + teleport);
                    break;
                }
            }
        }
        return true;
    }

    private static int randomWaitDistance() {
        return Calculations.random(4, 15);
    }

    private static int randomDestinationDistance() {
        return Calculations.random(20, 25);
    }
}
