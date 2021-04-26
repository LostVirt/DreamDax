package org.dreambot.walker.dax.dreamutils;

import org.dreambot.api.methods.dialogues.Dialogues;

import java.util.function.Predicate;

public class DialogueHandler {

    public static String getAction(Predicate<String> predicate) {
        if (Dialogues.getOptions() != null) {
            for (String option : Dialogues.getOptions()) {
                if (predicate.test(option)) {
                    return option;
                }
            }
        }
        return "";
    }

}
