package org.dreambot;

import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.walker.Walker;
import org.dreambot.walker.dax.models.RSBank;

@ScriptManifest(category = Category.MISC, author = "LostVirt", name = "LostWalker", description = "Uses DaxWalker to traverse the map", version = 1)
public class Main extends AbstractScript {

    @Override
    public void onStart() {
        // use teleports if possible
        Walker.getDaxWalker().setUseTeleports(false);
    }

    @Override
    public int onLoop() {
//        if (!RSBank.VARROCK_EAST.getPosition().getArea(10).contains(getLocalPlayer())) {
//            Walker.getDaxWalker().walkToBank(RSBank.VARROCK_EAST);
//        }
        //Walker.getDaxWalker().walkToBank();
        Walker.getDaxWalker().walkTo(new Tile(3225, 3254, 0));

        return (int) Calculations.nextGaussianRandom(400, 250);
    }
}
