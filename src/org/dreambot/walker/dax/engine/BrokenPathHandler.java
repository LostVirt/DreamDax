package org.dreambot.walker.dax.engine;



import org.dreambot.walker.dax.dreamutils.GameObjectHandler;
import org.dreambot.walker.dax.engine.definitions.PathHandleState;
import org.dreambot.walker.dax.engine.definitions.PathLink;
import org.dreambot.walker.dax.engine.definitions.PopUpInterfaces;
import org.dreambot.walker.dax.engine.definitions.WalkCondition;
import org.dreambot.walker.dax.engine.pathfinding.BFSMapCache;
import org.dreambot.walker.dax.engine.utils.RunManager;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.wrappers.interactive.GameObject;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class BrokenPathHandler {

    public enum NextMove {
        UNDERGROUND(Pattern.compile("(?i)(climb|jump|walk).down"), generateCase(sceneObject -> sceneObject.getName().matches("(?i)(trap.?door|manhole)"), Pattern.compile("(?i)Open|(Climb.down)"))),
        FLOOR_UNDER(Pattern.compile("(?i)(climb|jump|walk).down"), generateCase(sceneObject -> sceneObject.getName().matches("(?i)(trap.?door|manhole)"), Pattern.compile("(?i)Open|(Climb.down)"))),
        FLOOR_ABOVE(Pattern.compile("(?i)(pass|climb|jump|walk).(up|through)")),
        SAME_FLOOR(Pattern.compile("(Exit|Use(?i-m)|pass|(walk|jump|climb).(across|over|under|into)|(open|push|enter)|(.+.through)|cross|board|mine)"));

        private Pattern pattern;
        private DestinationStateSpecialCase[] specialCases;

        NextMove(Pattern pattern, DestinationStateSpecialCase... specialCases) {
            this.pattern = pattern;
            this.specialCases = specialCases;
        }

        public boolean objectSatisfies(final GameObject gameObject) {
            for (DestinationStateSpecialCase specialCase : specialCases) {
                if (specialCase.satisfies(gameObject)) return true;
            }
            if (!gameObject.getName().matches("chest.*")) return false;
            return containsAction(gameObject, this.pattern);
        }

        public boolean handle(final GameObject gameObject) {
            for (DestinationStateSpecialCase specialCase : specialCases) {
                if (specialCase.satisfies(gameObject)) return specialCase.handle(gameObject);
            }
            return gameObject.interact(s -> this.pattern.matcher(s).matches());
        }

        public boolean handle() {
            GameObject[] gameObjects = GameObjects.all(this::objectSatisfies).toArray(new GameObject[0]);
            GameObject gameObject = Arrays.stream(gameObjects).min(Comparator.comparingDouble(GameObject::distance)).orElse(null);
            return gameObject != null && handle(gameObject);
        }
    }

    private static DestinationStateSpecialCase generateCase(Predicate<GameObject> predicate, Pattern pattern) {
        return new DestinationStateSpecialCase() {
            @Override
            public boolean satisfies(GameObject gameObject) {
                return predicate.test(gameObject);
            }

            @Override
            public Pattern getPattern() {
                return pattern;
            }
        };
    }

    private interface DestinationStateSpecialCase {
        boolean satisfies(final GameObject p0);

        Pattern getPattern();

        default boolean handle(GameObject gameObject) {
            return gameObject.interact(s -> getPattern().matcher(s).matches());
        }
    }

    public static PathHandleState handle(Tile start, Tile end, WalkCondition walkCondition) {
        GameObject gameObject = getBlockingObject(start, end);
        if (gameObject != null) return handleObject(start, end, gameObject, walkCondition);
        MethodProvider.logInfo("[PathHandler] " + String.format("No PathLink handler for (%d, %d, %d)->(%d, %d, %d)", start.getX(), start.getY(), start.getZ(), end.getX(), end.getY(), end.getZ()));
        return PathHandleState.FAILED;
    }

    public static PathHandleState handlePathLink(Tile start, Tile end, WalkCondition walkCondition, List<PathLink> pathLinks) {
        PathLink pathLink = pathLinks.stream()
                .filter(link -> link.getStart().equals(start) && link.getEnd().equals(end))
                .findAny()
                .orElse(null);
        if (pathLink != null) return pathLink.handle(walkCondition);
        return null;
    }

    private static PathHandleState handleObject(final Tile start, final Tile end, final GameObject gameObject, final WalkCondition walkCondition) {
        if (gameObject.getName().matches("(?i)Gate of (.+)")) return handleStrongHoldDoor(start, end, gameObject, walkCondition);

        MethodProvider.logInfo("[DaxDebug-handleObject] Handling: " + gameObject.getName() + "->" + Arrays.asList(gameObject.getActions()));
        if (!determine(start, end).handle(gameObject)) return PathHandleState.FAILED;
        MethodProvider.logInfo("[DaxDebug-handleObject] 1");

        RunManager runManager = new RunManager();
        final AtomicBoolean exitCondition = new AtomicBoolean(false);
        if (!MethodProvider.sleepUntil(() -> {
            if (!runManager.isWalking()) {
                MethodProvider.sleepUntil(() ->
                        PopUpInterfaces.resolve()
                                || end.distance(Players.localPlayer()) <= 2
                                || new BFSMapCache().canReach(end),
                        Players.localPlayer().isAnimating() ? Calculations.random(1200, 2000) : Calculations.random(3500, 4500));
                return true;
            }
            if (walkCondition.getAsBoolean()) {
                exitCondition.set(true);
                return true;
            }
            return false;
        }, 15000)) {
            MethodProvider.logInfo("[DaxDebug-handleObject] FAILED");
            return PathHandleState.FAILED;
        }

        MethodProvider.logInfo("[DaxDebug-handleObject] Resolve");
        PopUpInterfaces.resolve();
        if (exitCondition.get()) return PathHandleState.EXIT;

        MethodProvider.logInfo("[DaxDebug-handleObject] after exit");
        if (Dialogues.inDialogue()) {
            MethodProvider.logInfo("[DaxDebug-handleObject] Dialog open, handle convo");
            return EntityHandler.handleConversation() ? PathHandleState.SUCCESS : PathHandleState.FAILED;
        }
        return PathHandleState.SUCCESS;
    }

    private static PathHandleState handleStrongHoldDoor(Tile start, Tile end, GameObject gameObject, WalkCondition walkCondition) {
        if (!determine(start, end).handle(gameObject)) return PathHandleState.FAILED;

        RunManager runManager = new RunManager();
        AtomicBoolean exitCondition = new AtomicBoolean(false);
        if (!MethodProvider.sleepUntil(() -> {
            if (!runManager.isWalking()) {
                return true;
            }
            if (walkCondition.getAsBoolean()) {
                exitCondition.set(true);
                return true;
            }
            return false;
        }, 15000)) {
            return PathHandleState.FAILED;
        }

        if (exitCondition.get()) return PathHandleState.EXIT;
        if (!Dialogues.inDialogue()) return PathHandleState.FAILED;
        return EntityHandler.handleConversation() ? PathHandleState.SUCCESS : PathHandleState.FAILED;
    }

    private static GameObject getBlockingObject(Tile start, Tile end) {
        NextMove state = determine(start, end);
        GameObject[] gameObjects = GameObjects.all(sceneObject -> sceneObject.getTile().distance(start) <= 5 && state.objectSatisfies(sceneObject)).toArray(new GameObject[0]);
        return Arrays.stream(gameObjects).min(Comparator.comparingDouble(o -> o.distance(start))).orElse(null);
    }

    private static boolean containsAction(GameObject gameObject, Pattern regex) {
        String[] actions = gameObject.getActions();
        if (actions == null) return false;
        return GameObjectHandler.hasAction(gameObject, s -> regex.matcher(s).matches());
    }

    private static NextMove determine(Tile start, Tile end) {
        if (start.getZ() < end.getZ() || isStrongHoldUp(start, end)) return NextMove.FLOOR_ABOVE;
        if (isStrongHold(start) && end.getY() < 3500) return NextMove.FLOOR_ABOVE;
        if (start.getY() > 5000 && end.getY() < 5000) return NextMove.FLOOR_ABOVE;
        if (start.getY() < 5000 && end.getY() > 5000) return NextMove.UNDERGROUND;
        if (start.getZ() > end.getZ() || isStrongHoldDown(start, end)) return NextMove.FLOOR_UNDER;
        return NextMove.SAME_FLOOR;
    }

    private static boolean isStrongHold(final Tile position) {
        return position.getX() >= 1850 && position.getX() <= 2380 && position.getY() >= 5175 && position.getY() <= 5317;
    }

    private static boolean isStrongHoldUp(final Tile start, final Tile end) {
        return isStrongHold(start) && isStrongHold(end) && Math.abs(end.getX() - start.getX()) > 52 && start.getX() > end.getX();
    }

    private static boolean isStrongHoldDown(final Tile start, final Tile end) {
        return isStrongHold(start) && isStrongHold(end) && Math.abs(end.getX() - start.getX()) > 52 && start.getX() < end.getX();
    }


}
