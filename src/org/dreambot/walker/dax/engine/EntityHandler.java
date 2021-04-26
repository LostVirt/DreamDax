package org.dreambot.walker.dax.engine;


import org.dreambot.walker.dax.dreamutils.NPCHandler;
import org.dreambot.walker.dax.engine.definitions.PathHandleState;
import org.dreambot.walker.dax.engine.definitions.StrongHoldAnswers;
import org.dreambot.walker.dax.engine.definitions.WalkCondition;
import org.dreambot.walker.dax.engine.pathfinding.BFSMapCache;
import org.dreambot.walker.dax.engine.utils.RunManager;
import org.dreambot.walker.dax.models.CharterShip;
import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.wrappers.interactive.NPC;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

public class EntityHandler {
    private static final Pattern LIKELY = Pattern.compile("(?i)(yes|ok(ay)?|sure|alright|fine|(.*search away.*)|(.*can i.*?)).*");
    private static final Pattern UNLIKELY = Pattern.compile("(?i)no(.|.thank.*|.sorry*+)?");

    public static PathHandleState handleWithAction(Pattern action, Tile start, Tile end, WalkCondition walkCondition) {
        NPC target = NPCs.closest(npc -> NPCHandler.hasAction(npc, s -> action.matcher(s).matches()));

        if (target == null) return PathHandleState.FAILED;
        if (!target.interact(s -> action.matcher(s).matches())) return PathHandleState.FAILED;
        AtomicBoolean exitCondition = new AtomicBoolean(false);
        if (waitFor(end, exitCondition, walkCondition) && exitCondition.get()) return PathHandleState.EXIT;
        if (!handleConversation()) return PathHandleState.FAILED;
        if (waitFor(end, exitCondition, walkCondition) && exitCondition.get()) return PathHandleState.EXIT;
        return new BFSMapCache().canReach(end) ? PathHandleState.SUCCESS : PathHandleState.FAILED;
    }

    public static PathHandleState handleCharter(CharterShip.Destination destination, WalkCondition walkCondition) {
        if (!CharterShip.open()) return PathHandleState.FAILED;
        AtomicBoolean exitCondition = new AtomicBoolean(false);
        if (waitFor(destination.getTile(), exitCondition, walkCondition) && exitCondition.get()) return PathHandleState.EXIT;
        if (!CharterShip.isInterfaceOpen()) return PathHandleState.FAILED;
        return CharterShip.charter(destination) ? PathHandleState.SUCCESS : PathHandleState.FAILED;
    }

    public static boolean selectOption() {
        String option = Arrays.stream(Dialogues.getOptions()).max(Comparator.comparingInt(EntityHandler::getResponseValue)).orElse(null);
        return Dialogues.clickOption(option);
    }

    public static boolean handleConversation() {
        while (Dialogues.inDialogue()) {
            DialogState state = getDialogState();
            if (state == null) break;
            switch (state) {
                case CONTINUE:
                    if (!Dialogues.continueDialogue()) return false;
                    waitNextDialogAction();
                    break;
                case CHOOSE:
                    if (!selectOption()) return false;
                    waitNextDialogAction();
                    break;
                case WAITING:
                    waitNextDialogAction();
                    break;
                case CLOSED:
                    return true;
            }
        }
        return true;
    }

    public static void waitNextDialogAction() {
        MethodProvider.sleep(150);
        MethodProvider.sleepUntil(() -> {
            DialogState state = getDialogState();
            return state == DialogState.CONTINUE || state == DialogState.CHOOSE || !Dialogues.inDialogue();
        }, 3500);
    }

    private static DialogState getDialogState() {
        if (!Dialogues.inDialogue()) return DialogState.CLOSED;
        if (Dialogues.isProcessing()) return DialogState.WAITING;
        if (Dialogues.canContinue()) return DialogState.CONTINUE;
        if (Dialogues.getOptions() != null && Dialogues.getOptions().length > 0) return DialogState.CHOOSE;
        return null;
    }

    private static boolean waitFor(Tile end, AtomicBoolean exitCondition, WalkCondition walkCondition) {
        RunManager runManager = new RunManager();
        return MethodProvider.sleepUntil(() -> {
            if (!runManager.isWalking()) {
                return true;
            }
            if (walkCondition.getAsBoolean()) {
                exitCondition.set(true);
                return true;
            }
            return new BFSMapCache().canReach(end);
        }, 12000);
    }

    private static int getResponseValue(String text) {
        int a = 0;
        if (EntityHandler.LIKELY.matcher(text).matches()) ++a;
        if (EntityHandler.UNLIKELY.matcher(text).matches()) --a;
        if (StrongHoldAnswers.getInstance().isAnswer(text)) a += 2;
        return a;
    }

    private enum DialogState {
        CONTINUE,
        CHOOSE,
        WAITING,
        CLOSED;
    }
}
