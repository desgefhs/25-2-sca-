package org.newdawn.spaceinvaders.core;

public class GameSession {

    private int collectedItems = 0;
    private long playerAttackDisabledUntil = 0;

    public void notifyItemCollected() {
        collectedItems++;
    }

    public boolean hasCollectedAllItems() {
        return collectedItems >= 2;
    }

    public void resetItemCollection() {
        collectedItems = 0;
    }

    public void stunPlayer(long duration) {
        this.playerAttackDisabledUntil = System.currentTimeMillis() + duration;
    }

    public boolean canPlayerAttack() {
        return System.currentTimeMillis() > playerAttackDisabledUntil;
    }
}
