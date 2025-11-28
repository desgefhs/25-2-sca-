package org.newdawn.spaceinvaders.core;

public class GameSession {

    private int collectedItems = 0;
    private long playerAttackDisabledUntil = 0;

    public void notifyItemCollected() {
        collectedItems++;
    }

    public void resetItemCollection() {
        collectedItems = 0;
    }

    public boolean canPlayerAttack() {
        return System.currentTimeMillis() > playerAttackDisabledUntil;
    }
}
