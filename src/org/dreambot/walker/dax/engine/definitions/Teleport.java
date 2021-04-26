package org.dreambot.walker.dax.engine.definitions;

import org.dreambot.walker.dax.dreamutils.InventoryHandler;
import org.dreambot.walker.dax.models.Requirement;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.wrappers.items.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public enum Teleport {
    VARROCK_TELEPORT(35, new Tile(3212, 3424, 0), () -> Magic.canCast(Normal.VARROCK_TELEPORT), () -> Magic.castSpell(Normal.VARROCK_TELEPORT)),
    VARROCK_TELEPORT_TAB(35, new Tile(3212, 3424, 0), () -> Inventory.count(8007) > 0, () -> {
        Item item = Inventory.get(8007);
        return item != null && InventoryHandler.interactItem(item, "Break");
    }),
    LUMBRIDGE_TELEPORT(35, new Tile(3225, 3219, 0), () -> Magic.canCast(Normal.LUMBRIDGE_TELEPORT), () -> Magic.castSpell(Normal.LUMBRIDGE_TELEPORT)),
    LUMBRIDGE_TELEPORT_TAB(35, new Tile(3225, 3219, 0), () -> Inventory.count(8008) > 0, () -> {
        Item item = Inventory.get(8008);
        return item != null && InventoryHandler.interactItem(item, "Break");
    }),
    FALADOR_TELEPORT(35, new Tile(2966, 3379, 0), () -> Magic.canCast(Normal.FALADOR_TELEPORT), () -> Magic.castSpell(Normal.FALADOR_TELEPORT)),
    FALADOR_TELEPORT_TAB(35, new Tile(2966, 3379, 0), () -> Inventory.count(8009) > 0, () -> {
        Item item = Inventory.get(8009);
        return item != null && InventoryHandler.interactItem(item, "Break");
    }),
    CAMELOT_TELEPORT(35, new Tile(2757, 3479, 0), () -> Magic.canCast(Normal.CAMELOT_TELEPORT), () -> Magic.castSpell(Normal.CAMELOT_TELEPORT)),
    CAMELOT_TELEPORT_TAB(35, new Tile(2757, 3479, 0), () -> Inventory.count(8010) > 0, () -> {
        Item item = Inventory.get(8010);
        return item != null && InventoryHandler.interactItem(item, "Break");
    }),
    PISCATORIS_TELEPORT(35, new Tile(2339, 3649, 0), () -> Inventory.count(12408) > 0, () -> {
        Item item = Inventory.get(12408);
        return item != null && InventoryHandler.interactItem(item, "Teleport");
    }),
    WATSON_TELEPORT(35, new Tile(1645, 3579, 0), () -> Inventory.count(23387) > 0, () -> {
        Item item = Inventory.get(23387);
        return item != null && InventoryHandler.interactItem(item, "Teleport");
    }),
    RING_OF_WEALTH_GRAND_EXCHANGE(35, new Tile(3161, 3478, 0), () -> WearableItemTeleport.has(WearableItemTeleport.RING_OF_WEALTH_MATCHER), () -> WearableItemTeleport.teleport(WearableItemTeleport.RING_OF_WEALTH_MATCHER, Pattern.compile("(?i)Grand Exchange"), 2)),
    RING_OF_WEALTH_FALADOR(35, new Tile(2994, 3377, 0), () -> WearableItemTeleport.has(WearableItemTeleport.RING_OF_WEALTH_MATCHER), () -> WearableItemTeleport.teleport(WearableItemTeleport.RING_OF_WEALTH_MATCHER, Pattern.compile("(?i)falador.*"), 3)),
    RING_OF_DUELING_DUEL_ARENA(35, new Tile(3313, 3233, 0), () -> WearableItemTeleport.has(WearableItemTeleport.RING_OF_DUELING_MATCHER), () -> WearableItemTeleport.teleport(WearableItemTeleport.RING_OF_DUELING_MATCHER, Pattern.compile("(?i).*duel arena.*"), 1)),
    RING_OF_DUELING_CASTLE_WARS(35, new Tile(2440, 3090, 0), () -> WearableItemTeleport.has(WearableItemTeleport.RING_OF_DUELING_MATCHER), () -> WearableItemTeleport.teleport(WearableItemTeleport.RING_OF_DUELING_MATCHER, Pattern.compile("(?i).*Castle Wars.*"), 2)),
    RING_OF_DUELING_FEROX_ENCLAVE(35, new Tile(3151, 3637, 0), () -> WearableItemTeleport.has(WearableItemTeleport.RING_OF_DUELING_MATCHER), () -> WearableItemTeleport.teleport(WearableItemTeleport.RING_OF_DUELING_MATCHER, Pattern.compile("(?i).*Ferox Enclave.*"), 3)),
    NECKLACE_OF_PASSAGE_WIZARD_TOWER(35, new Tile(3113, 3179, 0), () -> WearableItemTeleport.has(WearableItemTeleport.NECKLACE_OF_PASSAGE_MATCHER), () -> WearableItemTeleport.teleport(WearableItemTeleport.NECKLACE_OF_PASSAGE_MATCHER, Pattern.compile("(?i).*wizard.+tower.*"), 1)),
    NECKLACE_OF_PASSAGE_OUTPOST(35, new Tile(2430, 3347, 0), () -> WearableItemTeleport.has(WearableItemTeleport.NECKLACE_OF_PASSAGE_MATCHER), () -> WearableItemTeleport.teleport(WearableItemTeleport.NECKLACE_OF_PASSAGE_MATCHER, Pattern.compile("(?i).*the.+outpost.*"), 2)),
    NECKLACE_OF_PASSAGE_EYRIE(35, new Tile(3406, 3156, 0), () -> WearableItemTeleport.has(WearableItemTeleport.NECKLACE_OF_PASSAGE_MATCHER), () -> WearableItemTeleport.teleport(WearableItemTeleport.NECKLACE_OF_PASSAGE_MATCHER, Pattern.compile("(?i).*eagl.+eyrie.*"), 3)),
    COMBAT_BRACE_WARRIORS_GUILD(35, new Tile(2882, 3548, 0), () -> WearableItemTeleport.has(WearableItemTeleport.COMBAT_BRACE_MATCHER), () -> WearableItemTeleport.teleport(WearableItemTeleport.COMBAT_BRACE_MATCHER, Pattern.compile("(?i).*warrior.+guild.*"), 1)),
    COMBAT_BRACE_CHAMPIONS_GUILD(35, new Tile(3193, 3369, 0), () -> WearableItemTeleport.has(WearableItemTeleport.COMBAT_BRACE_MATCHER), () -> WearableItemTeleport.teleport(WearableItemTeleport.COMBAT_BRACE_MATCHER, Pattern.compile("(?i).*champion.+guild.*"), 2)),
    COMBAT_BRACE_MONASTRY(35, new Tile(3053, 3486, 0), () -> WearableItemTeleport.has(WearableItemTeleport.COMBAT_BRACE_MATCHER), () -> WearableItemTeleport.teleport(WearableItemTeleport.COMBAT_BRACE_MATCHER, Pattern.compile("(?i).*monastery.*"), 3)),
    COMBAT_BRACE_RANGE_GUILD(35, new Tile(2656, 3442, 0), () -> WearableItemTeleport.has(WearableItemTeleport.COMBAT_BRACE_MATCHER), () -> WearableItemTeleport.teleport(WearableItemTeleport.COMBAT_BRACE_MATCHER, Pattern.compile("(?i).*rang.+guild.*"), 4)),
    GAMES_NECK_BURTHORPE(35, new Tile(2897, 3551, 0), () -> WearableItemTeleport.has(WearableItemTeleport.GAMES_NECKLACE_MATCHER), () -> WearableItemTeleport.teleport(WearableItemTeleport.GAMES_NECKLACE_MATCHER, Pattern.compile("(?i).*burthorpe.*"), 1)),
    GAMES_NECK_BARBARIAN_OUTPOST(35, new Tile(2520, 3570, 0), () -> WearableItemTeleport.has(WearableItemTeleport.GAMES_NECKLACE_MATCHER), () -> WearableItemTeleport.teleport(WearableItemTeleport.GAMES_NECKLACE_MATCHER, Pattern.compile("(?i).*barbarian.*"), 2)),
    GAMES_NECK_CORPREAL(35, new Tile(2965, 4832, 2), () -> WearableItemTeleport.has(WearableItemTeleport.GAMES_NECKLACE_MATCHER), () -> WearableItemTeleport.teleport(WearableItemTeleport.GAMES_NECKLACE_MATCHER, Pattern.compile("(?i).*corpreal.*"), 3)),
    GAMES_NECK_WINTER(35, new Tile(1623, 3937, 0), () -> WearableItemTeleport.has(WearableItemTeleport.GAMES_NECKLACE_MATCHER), () -> WearableItemTeleport.teleport(WearableItemTeleport.GAMES_NECKLACE_MATCHER, Pattern.compile("(?i).*wintertodt.*"), 5)),
    GLORY_EDGE(35, new Tile(3087, 3496, 0), () -> WearableItemTeleport.has(WearableItemTeleport.GLORY_MATCHER), () -> WearableItemTeleport.teleport(WearableItemTeleport.GLORY_MATCHER, Pattern.compile("(?i).*edgeville.*"), 1)),
    GLORY_KARAMJA(35, new Tile(2918, 3176, 0), () -> WearableItemTeleport.has(WearableItemTeleport.GLORY_MATCHER), () -> WearableItemTeleport.teleport(WearableItemTeleport.GLORY_MATCHER, Pattern.compile("(?i).*karamja.*"), 2)),
    GLORY_DRAYNOR(35, new Tile(3105, 3251, 0), () -> WearableItemTeleport.has(WearableItemTeleport.GLORY_MATCHER), () -> WearableItemTeleport.teleport(WearableItemTeleport.GLORY_MATCHER, Pattern.compile("(?i).*draynor.*"), 3)),
    GLORY_AL_KHARID(35, new Tile(3293, 3163, 0), () -> WearableItemTeleport.has(WearableItemTeleport.GLORY_MATCHER), () -> WearableItemTeleport.teleport(WearableItemTeleport.GLORY_MATCHER, Pattern.compile("(?i).*al kharid.*"), 4));

    private int moveCost;
    private Tile location;
    private Requirement requirement;
    private Action action;

    Teleport(int moveCost, Tile location, Requirement requirement, Action action) {
        this.moveCost = moveCost;
        this.location = location;
        this.requirement = requirement;
        this.action = action;
    }

    public int getMoveCost() {
        return this.moveCost;
    }

    public Tile getLocation() {
        return this.location;
    }

    public Requirement getRequirement() {
        return this.requirement;
    }

    public boolean trigger() {
        return this.action.trigger();
    }

    public boolean isAtTeleportSpot(final Tile position) {
        return position.distance(this.location) < 15;
    }

    public static List<Tile> getValidStartingPositions() {
        final List<Tile> positions = new ArrayList<>();
        for (Teleport teleport : values()) {
            if (!teleport.requirement.satisfies()) continue;
            positions.add(teleport.location);
        }
        return positions;
    }

    private interface Action {
        boolean trigger();
    }
}
