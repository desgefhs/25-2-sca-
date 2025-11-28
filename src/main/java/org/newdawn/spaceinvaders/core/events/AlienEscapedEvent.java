package org.newdawn.spaceinvaders.core.events;

import org.newdawn.spaceinvaders.entity.Entity;

public class AlienEscapedEvent implements Event {
    private final Entity alien;

    public AlienEscapedEvent(Entity alien) {
        this.alien = alien;
    }

    public Entity getAlien() {
        return alien;
    }
}
