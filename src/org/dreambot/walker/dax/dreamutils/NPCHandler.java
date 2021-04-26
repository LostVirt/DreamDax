package org.dreambot.walker.dax.dreamutils;

import org.dreambot.api.wrappers.interactive.NPC;

import java.util.function.Predicate;

public class NPCHandler {

    public static boolean hasAction(NPC npc, Predicate<String> predicate) {
        if (npc.getActions() != null) {
            for (String action : npc.getActions()) {
                if (predicate.test(action)) {
                    return true;
                }
            }
        }
        return false;
    }

}
