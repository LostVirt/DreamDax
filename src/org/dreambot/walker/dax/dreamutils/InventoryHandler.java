package org.dreambot.walker.dax.dreamutils;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.filter.Filter;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.wrappers.items.Item;

public class InventoryHandler {

    public static boolean interactItem(Item item, String action) {
        return Tabs.isOpen(Tab.INVENTORY) ? item.interact(action) : Tabs.open(Tab.INVENTORY) && interactItem(item, action);
    }

    public static boolean interactItem(Filter<Item> filter, String action) {
        return Tabs.isOpen(Tab.INVENTORY) ? Inventory.get(filter).interact(action) : Tabs.open(Tab.INVENTORY) && interactItem(filter, action);
    }

    public static boolean interactItem(int itemId, String action) {
        return Tabs.isOpen(Tab.INVENTORY) ? Inventory.get(itemId).interact(action) : Tabs.open(Tab.INVENTORY) && interactItem(itemId, action);
    }

}
