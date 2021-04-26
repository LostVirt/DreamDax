package org.dreambot.walker.dax.engine.definitions;


import org.dreambot.api.methods.map.Tile;

public interface PathLinkHandler {
    PathHandleState handle(final Tile p0, final Tile p1, final WalkCondition p2);
}
