package org.dreambot.walker.dax;

import org.dreambot.walker.dax.engine.WalkerEngine;
import org.dreambot.walker.dax.engine.definitions.Teleport;
import org.dreambot.walker.dax.engine.definitions.WalkCondition;
import org.dreambot.walker.dax.models.*;
import org.dreambot.walker.dax.models.exceptions.AuthorizationException;
import org.dreambot.walker.dax.models.exceptions.RateLimitException;
import org.dreambot.walker.dax.models.exceptions.UnknownException;
import org.dreambot.walker.dax.store.DaxStore;
import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.wrappers.interactive.Locatable;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class DaxWalker {
    private Server server;
    private WalkerEngine walkerEngine;
    private DaxStore store;
    private boolean useTeleports;

    public DaxWalker(Server server) {
        this.server = server;
        this.store = new DaxStore();
        this.walkerEngine = new WalkerEngine(null, this);
        this.useTeleports = true;
    }

    public DaxStore getStore() {
        return store;
    }

    public void setGlobalCondition(WalkCondition walkCondition) {
        walkerEngine.setWalkCondition(walkCondition);
    }

    public WalkState walkTo(Locatable locatable) {
        return walkTo(locatable, null);
    }

    public boolean isUseTeleports() {
        return useTeleports;
    }

    public void setUseTeleports(boolean useTeleports) {
        this.useTeleports = useTeleports;
    }

    public WalkState walkTo(Locatable locatable, WalkCondition walkCondition) {
        MethodProvider.logInfo("[DaxWalker] " + String.format("Navigating to (%d,%d,%d)", locatable.getX(), locatable.getY(), locatable.getZ()));
        List<PathRequestPair> pathRequestPairs = useTeleports ? getPathTeleports(locatable.getTile()) : new ArrayList<>();
        pathRequestPairs.add(new PathRequestPair(Point3D.from(localPosition()), Point3D.from(locatable.getTile())));

        BulkPathRequest request = new BulkPathRequest(PlayerDetails.generate(), pathRequestPairs);

        try {
            return walkerEngine.walk(server.getPaths(request), walkCondition) ? WalkState.SUCCESS : WalkState.FAILED;
        } catch (RateLimitException e2) {
            MethodProvider.logError("[" + Level.WARNING + "] DaxWalker: RATE_LIMIT");
            e2.printStackTrace();
            return WalkState.RATE_LIMIT;
        } catch (AuthorizationException | UnknownException ex2) {
            MethodProvider.logError("[" + Level.WARNING + "] DaxWalker: ERROR");
            ex2.printStackTrace();
            return WalkState.ERROR;
        }
    }

    public WalkState walkToBank() {
        return walkToBank(null, null);
    }

    public WalkState walkToBank(WalkCondition walkCondition) {
        return walkToBank(null, walkCondition);
    }

    public WalkState walkToBank(RSBank bank) {
        return walkToBank(bank, null);
    }

    public WalkState walkToBank(final RSBank bank, final WalkCondition walkCondition) {
        if (bank != null) return walkTo(bank.getPosition());
        List<BankPathRequestPair> pathRequestPairs = useTeleports ? getBankPathTeleports() : new ArrayList<>();
        pathRequestPairs.add(new BankPathRequestPair(Point3D.from(this.localPosition()), null));

        BulkBankPathRequest request = new BulkBankPathRequest(PlayerDetails.generate(), pathRequestPairs);
        try {
            return walkerEngine.walk(server.getBankPaths(request), walkCondition) ? WalkState.SUCCESS : WalkState.FAILED;
        } catch (RateLimitException e2) {
            return WalkState.RATE_LIMIT;
        } catch (AuthorizationException | UnknownException ex2) {
            return WalkState.ERROR;
        }
    }

    private Tile localPosition() {
        return Players.localPlayer().getTile();
    }

    private List<BankPathRequestPair> getBankPathTeleports() {
        return Teleport.getValidStartingPositions().stream().map((position) -> new BankPathRequestPair(Point3D.from(position), null)).collect(Collectors.toList());
    }

    private List<PathRequestPair> getPathTeleports(Tile start) {
        return Teleport.getValidStartingPositions().stream().map((position) -> new PathRequestPair(Point3D.from(position), Point3D.from(start))).collect(Collectors.toList());
    }
}
