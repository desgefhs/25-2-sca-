package org.newdawn.spaceinvaders.core.events;

public class MeteorDestroyedEvent implements Event {
    private final int scoreValue;

    public MeteorDestroyedEvent(int scoreValue) {
        this.scoreValue = scoreValue;
    }

    public int getScoreValue() {
        return scoreValue;
    }
}
