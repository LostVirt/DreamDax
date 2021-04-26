package org.dreambot.walker.dax.store;

import org.dreambot.walker.dax.engine.definitions.PathLink;

import java.util.ArrayList;
import java.util.List;

public class DaxStore {
    private List<PathLink> pathLinks;

    public DaxStore() {
        this.pathLinks = new ArrayList<>(PathLink.getValues());
    }

    public void addPathLink(final PathLink link) {
        this.pathLinks.add(link);
    }

    public void removePathLink(final PathLink link) {
        this.pathLinks.remove(link);
    }

    public List<PathLink> getPathLinks() {
        return this.pathLinks;
    }
}
