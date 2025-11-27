package org.newdawn.spaceinvaders.core.events;

import org.newdawn.spaceinvaders.core.Event;

public class MeteorDestroyedEvent implements Event {
    private final int scoreValue;

    public MeteorDestroyedEvent(int scoreValue) {
        this.scoreValue = scoreValue;
    }

    public int getScoreValue() {
        return scoreValue;
    }
}
