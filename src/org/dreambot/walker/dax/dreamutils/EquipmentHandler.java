package org.dreambot.walker.dax.dreamutils;

import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.container.impl.equipment.EquipmentSlot;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.widgets.WidgetChild;

import java.util.Arrays;
import java.util.function.Predicate;

public class EquipmentHandler {

    public static EquipmentSlot getSlotForItem(Predicate<Item> itemPredicate) {
        for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
            Item item = Equipment.getItemInSlot(equipmentSlot);
            if (item != null && itemPredicate.test(item)) {
                return equipmentSlot;
            }
        }
        return null;
    }

    public static boolean interact(Predicate<Item> itemPredicate, Predicate<String> actionPredicate) {
        if (Equipment.contains(itemPredicate)) {
            EquipmentSlot equipmentSlot = getSlotForItem(itemPredicate);
            if (equipmentSlot != null) {
                Item item = Equipment.getItemInSlot(equipmentSlot);
                if (item != null && item.getActions() != null) {
                    for (String action : item.getActions()) {
                        if (actionPredicate.test(action)) {
                            return Equipment.interact(equipmentSlot, action);
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean interact(Predicate<Item> itemPredicate, final int index) {
        MethodProvider.log("[EQUIPMENT]");
        EquipmentSlot equipmentSlot = getSlotForItem(itemPredicate);
        if (equipmentSlot != null) {
            MethodProvider.log("[EQUIPMENT] slot not null");
            Item item = Equipment.getItemInSlot(equipmentSlot);
            WidgetChild widgetChild = Equipment.getWidgetForSlot(equipmentSlot);
            if (widgetChild != null) {
                String[] actions = widgetChild.getActions();
                if (item != null && actions != null && actions.length >= index){
                    MethodProvider.log("Actions: " + Arrays.toString(actions));
                    MethodProvider.log("[EQUIPMENT] interact");
                    return Equipment.interact(equipmentSlot, actions[index]);
                }
            }
        }
        return false;
    }


    /*
    public static boolean teleportEquipment(final Pattern itemMatcher, final Pattern option) {
        return Equipment.interact(item -> itemMatcher.matcher(item.getName()).matches(), s -> option.matcher(s).matches());
    }


    public static boolean teleportEquipment(final Pattern itemMatcher, final int index) {
        return Equipment.interact(item -> itemMatcher.matcher(item.getName()).matches(), index);
    }
     */
}
