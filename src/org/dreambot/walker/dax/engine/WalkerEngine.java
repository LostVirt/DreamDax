package org.dreambot.walker.dax.engine;

import org.dreambot.walker.dax.DaxWalker;
import org.dreambot.walker.dax.engine.definitions.Teleport;
import org.dreambot.walker.dax.engine.definitions.WalkCondition;
import org.dreambot.walker.dax.engine.pathfinding.BFSMapCache;
import org.dreambot.walker.dax.engine.utils.RunEnergyManager;
import org.dreambot.walker.dax.models.PathResult;
import org.dreambot.walker.dax.models.PathStatus;
import org.dreambot.walker.dax.models.Point3D;
import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;

import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class WalkerEngine {
    private Map<Tile, Teleport> map;
    private WalkCondition walkCondition;
    private RunEnergyManager runEnergyManager;
    private DaxWalker instance;


    private PathResult lastPath;
    private BFSMapCache bfsMapCache;
    private Tile playerPosition;

    public WalkerEngine(WalkCondition walkCondition, DaxWalker instance) {
        this.instance = instance;
        map = new ConcurrentHashMap<>();
        for (Teleport teleport : Teleport.values()) {
            map.put(teleport.getLocation(), teleport);
        }
        runEnergyManager = new RunEnergyManager();
        this.walkCondition = (() -> {
            runEnergyManager.trigger();
            return false;
        });
        if (walkCondition != null) {
            this.walkCondition = this.walkCondition.and(walkCondition);
        }
    }

    public boolean walk(List<PathResult> list) {
        return this.walk(list, null);
    }

    public boolean walk(List<PathResult> list, WalkCondition walkCondition) {
        walkCondition = walkCondition != null ? this.walkCondition.or(walkCondition) : this.walkCondition;
        List<PathResult> validPaths = validPaths(list);
        PathResult pathResult = getBestPath(validPaths);
        if (pathResult == null) {
            MethodProvider.logError("[DaxWalker] No valid path found");
            return false;
        }
        lastPath = pathResult;
        MethodProvider.logError("[DaxWalker] " + String.format("Chose path of cost: %d out of %d options.", pathResult.getCost(), validPaths.size()));
        return PathHandler.walk(convert(pathResult.getPath()), walkCondition, 3, instance.getStore().getPathLinks());
    }

    public List<PathResult> validPaths(List<PathResult> list) {
        List<PathResult> result = list.stream().filter(pathResult -> pathResult.getPathStatus() == PathStatus.SUCCESS).collect(Collectors.toList());
        if (!result.isEmpty()) return result;

        if (list.stream().anyMatch(pathResult -> pathResult.getPathStatus() == PathStatus.BLOCKED_END)) {
            MethodProvider.logError("[DaxWalker] Destination is not walkable.");
        }
        return Collections.emptyList();
    }

    public PathResult getBestPath(List<PathResult> list) {
        return list.stream().min(Comparator.comparingInt(this::getPathMoveCost)).orElse(null);
    }

    public void setWalkCondition(WalkCondition walkCondition) {
        this.walkCondition = walkCondition;
    }

    public void andWalkCondition(WalkCondition walkCondition) {
        this.walkCondition.and(walkCondition);
    }

    public void orWalkCondition(WalkCondition walkCondition) {
        this.walkCondition.or(walkCondition);
    }

    private List<Tile> convert(List<Point3D> list) {
        List<Tile> positions = new ArrayList<Tile>();
        for (Point3D point3D : list) {
            positions.add(new Tile(point3D.getX(), point3D.getY(), point3D.getZ()));
        }
        return positions;
    }

    private int getPathMoveCost(PathResult pathResult) {
        if (Players.localPlayer().getTile().equals(pathResult.getPath().get(0).toPosition())) return pathResult.getCost();

        Teleport teleport = map.get(pathResult.getPath().get(0).toPosition());
        if (teleport == null) return pathResult.getCost();
        return teleport.getMoveCost() + pathResult.getCost();
    }

}
