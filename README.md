# DreamDax - Dreambot version of DaxWalker
#### Ported from RSPeers version of DaxWalker, this "Read me" is copied and modified from them aswell.
- [You can find it here](https://github.com/itsdax/RSPeer-Webwalker)


Fast and customizable webwalker for Dreambot. Instantly calculates a path to your destination and walks it. Accounts for path requirements (quests, items, level), teleports, and shortcuts. Handles script pauses and stops. Dynamic walking conditions and exit conditions. Features collision aware randomness when selecting which tiles to walk on.

#### Creating DaxWalker instance
```java
private static DaxWalker daxWalker;

public static DaxWalker getDaxWalker() {
    return daxWalker;
}

static {
    try {
        daxWalker = new DaxWalker(new Server("key", "PUBLIC-KEY"));
    } catch (Exception e) {
        e.printStackTrace();
    }

}
```
- [Example for this repo](https://github.com/LostVirt/DreamDax/blob/master/src/org/dreambot/walker/Walker.java)

#### or you can create it like this:
```java
DaxWalker daxWalker = new DaxWalker(new Server("sub_DPjXXzL5DeSiPf", "PUBLIC-KEY"));
```
###### [This example uses a **PUBLIC** key, shared across hundreds of users. You can get your own dedicated keys here.](https://admin.dax.cloud/)



#### Walking to location
This will check your available teleports and use if deemed necessary. 
The cost of a teleport versus walking the distance is defined in ```org.dreambot.walker.dax.engine.definitions.Teleport```
```java
Walker.getDaxWalker().walkTo(new Tile(1, 2, 3));
```

#### Walking to bank
```java
Walker.getDaxWalker().walkToBank();
```

#### Walking to specific bank
```java
Walker.getDaxWalker().walkToBank(RSBank.VARROCK_EAST);
```

#### Adding Custom Stopping Conditions/Passive Actions
This condition will be checked in between walks and idle actions.
```java
Walker.getDaxWalker().walkTo(new Tile(3145, 9914, 0), () -> {
    if (Players.localPlayer().getHealthPercent() < 20) {
        Food.eat(); // eat example
    }
    return false; // false to continue walking after check. true to exit out of walker.
});
```


#### Disabling Teleports
```java
Walker.getDaxWalker().setUseTeleports(false);
```

# Contributing
The following links will direct you to where to look for contributing to the Walker. Create a pull request and I'll look it over. Thanks for contributing!

```java
package org.dreambot.walker.dax.engine.definitions;
```

- [Adding new Pop-Up interface](https://github.com/LostVirt/DreamDax/blob/master/src/org/dreambot/walker/dax/engine/definitions/PopUpInterfaces.java)

```java
STRONGHOLD_PROMPT(() -> Widgets.getWidgetChild(579, 17)),
WILDERNESS_PROMPT(() -> Widgets.getWidgetChild(475, 11));
```


- [Adding new Teleport Method](https://github.com/LostVirt/DreamDax/blob/master/src/org/dreambot/walker/dax/engine/definitions/Teleport.java)

```java
VARROCK_TELEPORT(35, new Tile(3212, 3424, 0), () -> Magic.canCast(Normal.VARROCK_TELEPORT), () -> Magic.castSpell(Normal.VARROCK_TELEPORT)),
VARROCK_TELEPORT_TAB(35, new Tile(3212, 3424, 0), () -> Inventory.count(8007) > 0, () -> {
    Item item = Inventory.get(8007);
    return item != null && InventoryHandler.interactItem(item, "Break");
}),
```

- [Adding new Wearable Teleport Item](https://github.com/LostVirt/DreamDax/blob/master/src/org/dreambot/walker/dax/engine/definitions/WearableItemTeleport.java)

```java
public static final Pattern RING_OF_WEALTH_MATCHER = Pattern.compile("(?i)ring of wealth.?\\(.+");
public static final Pattern RING_OF_DUELING_MATCHER = Pattern.compile("(?i)ring of dueling.?\\(.+");
public static final Pattern NECKLACE_OF_PASSAGE_MATCHER = Pattern.compile("(?i)necklace of passage.?\\(.+");
public static final Pattern COMBAT_BRACE_MATCHER = Pattern.compile("(?i)combat brace.+\\(.+");
public static final Pattern GAMES_NECKLACE_MATCHER = Pattern.compile("(?i)game.+neck.+\\(.+");
public static final Pattern GLORY_MATCHER = Pattern.compile("(?i).+glory.*\\(.+");
```

- [Updating Stronghold Answers](https://github.com/LostVirt/DreamDax/blob/master/src/org/dreambot/walker/dax/engine/definitions/StrongHoldAnswers.java)

```java
private StrongHoldAnswers() {
        this.set.addAll(Arrays.asList("Use the Account Recovery System.",
                "Nobody.",
                "Don't tell them anything and click the 'Report Abuse' button.",
                "Me.",
                "Only on the RuneScape website.",
                "Report the incident and do not click any links.",
                "Authenticator and two-step login on my registered email.",
                "No way! You'll just take my gold for your own! Reported!",
                "No.",
                "Don't give them the information and send an 'Abuse Report'.",
                "Don't give them my password.",
                "The birthday of a famous person or event.",
                "Through account settings on runescape.com.",
                "Secure my device and reset my RuneScape password.",
                "Report the player for phishing.",
                "Don't click any links, forward the email to reportphishing@jagex.com.",
                "Inform Jagex by emailing reportphishing@jagex.com.",
                "Don't give out your password to anyone. Not even close friends.",
                "Politely tell them no and then use the 'Report Abuse' button.",
                "Set up 2 step authentication with my email provider.",
                "No, you should never buy a RuneScape account.",
                "Do not visit the website and report the player who messaged you.",
                "Only on the RuneScape website.",
                "Don't type in my password backwards and report the player.",
                "Virus scan my device then change my password.",
                "No, you should never allow anyone to level your account.",
                "Don't give out your password to anyone. Not even close friends.",
                "Report the stream as a scam. Real Jagex streams have a 'verified' mark.",
                "Read the text and follow the advice given.",
                "No way! I'm reporting you to Jagex!",
                "Talk to any banker in RuneScape.",
                "Secure my device and reset my RuneScape password.",
                "Don't share your information and report the player."));
    }
```


- [Adding Path Links](https://github.com/LostVirt/DreamDax/blob/master/src/org/dreambot/walker/dax/engine/definitions/PathLink.java)

```java
public static final PathLink KARAMJA_PORT_SARIM = new PathLink(new Tile(2953, 3146, 0), new Tile(3029, 3217, 0),
            (start, end, walkCondition) -> EntityHandler.handleWithAction(Pattern.compile("(?i)pay.fare"), start, end, walkCondition));
```
