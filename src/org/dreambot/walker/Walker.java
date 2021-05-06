package org.dreambot.walker;

import org.dreambot.walker.dax.DaxWalker;
import org.dreambot.walker.dax.Server;

public class Walker {

    private static DaxWalker daxWalker;

    public static DaxWalker getDaxWalker() {
        return daxWalker;
    }

    static {
        try {
            daxWalker = new DaxWalker(new Server("sub_DPjXXzL5DeSiPf", "PUBLIC-KEY"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
