package org.dreambot.walker.dax.dreamutils;

import org.dreambot.api.wrappers.items.Item;

import java.util.function.Predicate;

public class ItemHandler {

    public static String getAction(Item item, Predicate<String> predicate) {
        if (item.getActions() != null) {
            for (String action : item.getActions()) {
                if (predicate.test(action)) {
                    return action;
                }
            }
        }
        return "";
    }

    public static boolean hasAction(Item item, Predicate<String> predicate) {
        if (item.getActions() != null) {
            for (String action : item.getActions()) {
                if (predicate.test(action)) {
                    return true;
                }
            }
        }
        return false;
    }

}
