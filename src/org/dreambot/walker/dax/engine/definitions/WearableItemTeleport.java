package org.dreambot.walker.dax.engine.definitions;





import org.dreambot.walker.dax.dreamutils.DialogueHandler;
import org.dreambot.walker.dax.dreamutils.EquipmentHandler;
import org.dreambot.walker.dax.dreamutils.InventoryHandler;
import org.dreambot.walker.dax.dreamutils.ItemHandler;
import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.wrappers.items.Item;

import java.util.regex.Pattern;

public class WearableItemTeleport {

    public static final Pattern RING_OF_WEALTH_MATCHER = Pattern.compile("(?i)ring of wealth.?\\(.+");
    public static final Pattern RING_OF_DUELING_MATCHER = Pattern.compile("(?i)ring of dueling.?\\(.+");
    public static final Pattern NECKLACE_OF_PASSAGE_MATCHER = Pattern.compile("(?i)necklace of passage.?\\(.+");
    public static final Pattern COMBAT_BRACE_MATCHER = Pattern.compile("(?i)combat brace.+\\(.+");
    public static final Pattern GAMES_NECKLACE_MATCHER = Pattern.compile("(?i)game.+neck.+\\(.+");
    public static final Pattern GLORY_MATCHER = Pattern.compile("(?i).+glory.*\\(.+");


    public static boolean has(final Pattern itemMatcher) {
        return Inventory.contains(item -> itemMatcher.matcher(item.getName()).matches() && !item.isStackable()) || Equipment.contains(item -> itemMatcher.matcher(item.getName()).matches());
    }

    public static boolean teleport(Pattern itemMatcher, Pattern option, int index) {
        MethodProvider.log("Start Teleport");
        if (teleportEquipment(itemMatcher, index)) return true;

        MethodProvider.log("Get item");
        Item inventoryItem = Inventory.get((item) -> itemMatcher.matcher(item.getName()).matches() && !item.isStackable());
        if (inventoryItem == null) {
            MethodProvider.log("item was null, return false");
            return false;
        }

        MethodProvider.log("interact item");
        if (!InventoryHandler.interactItem(inventoryItem, ItemHandler.getAction(inventoryItem, (s) -> option.matcher(s).matches()))
                && !InventoryHandler.interactItem(inventoryItem, "Rub")) {
            MethodProvider.log("failed to interact item");
            return false;
        }

        MethodProvider.log("sleep");
        if (!MethodProvider.sleepUntil(Dialogues::inDialogue, 5000)) return false;

        MethodProvider.log("solve dialogue");
        return Dialogues.chooseFirstOption(DialogueHandler.getAction((s) -> option.matcher(s).matches()));
    }

    public static boolean teleportEquipment(final Pattern itemMatcher, final int index) {
        return EquipmentHandler.interact(item -> itemMatcher.matcher(item.getName()).matches(), index);
    }

}
