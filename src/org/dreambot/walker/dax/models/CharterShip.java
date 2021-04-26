

package org.dreambot.walker.dax.models;


import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.widget.Widgets;
import org.dreambot.api.script.ScriptManager;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.widgets.WidgetChild;

public class CharterShip {
    private static final int CHARTER_SHIP_PARENT = 72;

    public static boolean isInterfaceOpen() {
        return Widgets.getWidget(CHARTER_SHIP_PARENT) != null;
    }

    public static boolean charter(Destination destination) {
        boolean successful = false;

        while (ScriptManager.getScriptManager().isRunning()) {
            if (destination.tile.distance() < 50) {
                successful = true;
                break;
            }
            if (isInterfaceOpen()) {
                WidgetChild widgetChild = Widgets.getWidgetChild(CHARTER_SHIP_PARENT, destination.index);
                if (widgetChild != null && widgetChild.isVisible()) {
                    if (widgetChild.interact(destination.action)) {
                        MethodProvider.sleepUntil(Dialogues::inDialogue, Calculations.random(2500, 5000));
                    }
                }
            } else if (!(Dialogues.inDialogue() && Dialogues.chooseFirstOption("Okay"))) {
                break;
            }

            MethodProvider.sleep((int) Calculations.nextGaussianRandom(250, 100));
        }

        return successful;
    }

    public static boolean open() {
        NPC charterNpc = NPCs.closest(npc -> npc.getName().equals("Trader Crewmember"));
        String action = "Charter";
        if (charterNpc != null && charterNpc.interact(action)) {
            return MethodProvider.sleepUntil(CharterShip::isInterfaceOpen, Calculations.random(2000, 3000));
        }
        return false;
    }

    public enum Destination {
        PORT_TYRAS(new Tile(2142, 3122, 0), 2, "Port Tyras"),
        PORT_PHASMATYS(new Tile(3702, 3503, 0), 5, "Port Phasmatys"),
        CATHERBY(new Tile(2792, 3414, 0), 8, "Catherby"),
        SHIPYARD(new Tile(3001, 3032, 0), 11, "Shipyard"),
        MUSA_POINT(new Tile(2954, 3158, 0), 14, "Musa Point"),
        BRIMHAVEN(new Tile(2760, 3239, 0), 17, "Brimhaven"),
        PORT_KHAZARD(new Tile(2674, 3144, 0), 20, "Port Khazard"),
        PORT_SARIM(new Tile(3038, 3192, 0), 23, "Port Sarim"),
        MOST_LE_HARMLESS(new Tile(3671, 2931, 0), 26, "Mos Le'Harmless"),
        CORSAIR_COVE(new Tile(2587, 2851, 0), 32, "Corsair Cove");

        private Tile tile;
        private int index;
        private String action;

        Destination(Tile tile, int index, String action) {
            this.tile = tile;
            this.index = index;
            this.action = action;
        }

        public Tile getTile() {
            return this.tile;
        }

        public int getIndex() {
            return this.index;
        }

        public String getAction() {
            return this.action;
        }
    }
}
