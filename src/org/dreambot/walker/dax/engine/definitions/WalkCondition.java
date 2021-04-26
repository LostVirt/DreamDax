package org.dreambot.walker.dax.engine.definitions;

import java.util.function.BooleanSupplier;

public interface WalkCondition extends BooleanSupplier {
    default WalkCondition and(WalkCondition walkCondition) {
        return () -> this.getAsBoolean() && walkCondition.getAsBoolean();
    }

    default WalkCondition or(WalkCondition walkCondition) {
        return () -> this.getAsBoolean() || walkCondition.getAsBoolean();
    }
}
