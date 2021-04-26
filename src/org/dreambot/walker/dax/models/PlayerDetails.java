package org.dreambot.walker.dax.models;

import org.dreambot.api.ClientSettings;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.settings.PlayerSettings;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.wrappers.items.Item;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlayerDetails {
    private int attack;
    private int defence;
    private int strength;
    private int hitpoints;
    private int ranged;
    private int prayer;
    private int magic;
    private int cooking;
    private int woodcutting;
    private int fletching;
    private int fishing;
    private int firemaking;
    private int crafting;
    private int smithing;
    private int mining;
    private int herblore;
    private int agility;
    private int thieving;
    private int slayer;
    private int farming;
    private int runecrafting;
    private int hunter;
    private int construction;
    private List<IntPair> setting;
    private List<IntPair> varbit;
    private boolean member;
    private List<IntPair> equipment;
    private List<IntPair> inventory;

    public static PlayerDetails generate() {
        Item[] items = Inventory.all().toArray(new Item[0]);
        Item[] equipmentItems = Equipment.all().toArray(new Item[0]);
        final List<IntPair> inventory = Arrays.stream(items)
                .filter(Objects::nonNull)
                .filter((s) -> !s.getName().equals("Members object"))
                .map((rsItem) -> new IntPair(rsItem.getID(), rsItem.getAmount())).collect(Collectors.toList());
        final List<IntPair> equipment = Arrays.stream(equipmentItems)
                .filter(Objects::nonNull)
                .filter((s) -> !s.getName().equals("Members object"))
                .map((rsItem) -> new IntPair(rsItem.getID(), rsItem.getAmount())).collect(Collectors.toList());
        final LinkedHashMap<Integer, Integer> map = ClientSettings.getClientParameters();
        final List<IntPair> settings = Stream.of(new Integer[]{176, 32, 71, 273, 144, 63, 179, 145, 68, 655, 10, 964, 399, 869, 314, 794, 440, 622, 131, 335, 299,
                896, 671, 810, 17, 11, 347, 302, 111, 116, 482, 307, 165, 150, 425, 365, 1630}).filter(map::containsValue).map(value -> new IntPair(value, map.get(value))).distinct().collect(Collectors.toList());
        final List<IntPair> varbit = Arrays.stream(new int[]{5087, 5088, 5089, 5090, 4895})
                .mapToObj(value -> new IntPair(value, PlayerSettings.getBitValue(value))).distinct().collect(Collectors.toList());
        return new PlayerDetails(
                Skills.getRealLevel(Skill.ATTACK),
                Skills.getRealLevel(Skill.DEFENCE),
                Skills.getRealLevel(Skill.STRENGTH),
                Skills.getRealLevel(Skill.HITPOINTS),
                Skills.getRealLevel(Skill.RANGED),
                Skills.getRealLevel(Skill.PRAYER),
                Skills.getRealLevel(Skill.MAGIC),
                Skills.getRealLevel(Skill.COOKING),
                Skills.getRealLevel(Skill.WOODCUTTING),
                Skills.getRealLevel(Skill.FLETCHING),
                Skills.getRealLevel(Skill.FISHING),
                Skills.getRealLevel(Skill.FIREMAKING),
                Skills.getRealLevel(Skill.CRAFTING),
                Skills.getRealLevel(Skill.SMITHING),
                Skills.getRealLevel(Skill.MINING),
                Skills.getRealLevel(Skill.HERBLORE),
                Skills.getRealLevel(Skill.AGILITY),
                Skills.getRealLevel(Skill.THIEVING),
                Skills.getRealLevel(Skill.SLAYER),
                Skills.getRealLevel(Skill.FARMING),
                Skills.getRealLevel(Skill.RUNECRAFTING),
                Skills.getRealLevel(Skill.HUNTER),
                Skills.getRealLevel(Skill.CONSTRUCTION),
                settings,
                varbit,
                Objects.requireNonNull(Worlds.getMyWorld()).isMembers(),
                equipment,
                inventory);
    }

    public PlayerDetails(int attack, int defence, int strength, int hitpoints, int ranged, int prayer, int magic, int cooking, int woodcutting, int fletching, int fishing, int firemaking, int crafting, int smithing, int mining, int herblore, int agility, int thieving, int slayer, int farming, int runecrafting, int hunter, int construction, List<IntPair> setting, List<IntPair> varbit, boolean member, List<IntPair> equipment, List<IntPair> inventory) {
        this.attack = attack;
        this.defence = defence;
        this.strength = strength;
        this.hitpoints = hitpoints;
        this.ranged = ranged;
        this.prayer = prayer;
        this.magic = magic;
        this.cooking = cooking;
        this.woodcutting = woodcutting;
        this.fletching = fletching;
        this.fishing = fishing;
        this.firemaking = firemaking;
        this.crafting = crafting;
        this.smithing = smithing;
        this.mining = mining;
        this.herblore = herblore;
        this.agility = agility;
        this.thieving = thieving;
        this.slayer = slayer;
        this.farming = farming;
        this.runecrafting = runecrafting;
        this.hunter = hunter;
        this.construction = construction;
        this.setting = setting;
        this.varbit = varbit;
        this.member = member;
        this.equipment = equipment;
        this.inventory = inventory;
    }
}
