package org.newdawn.spaceinvaders.core;

import org.newdawn.spaceinvaders.entity.Entity;
import org.newdawn.spaceinvaders.entity.EntityManager;
import org.newdawn.spaceinvaders.player.PlayerManager;
import org.newdawn.spaceinvaders.sound.SoundManager;

public class GameEventHandler {

    private final GameContext gameContext;
    private final PlayerManager playerManager;
    private final EntityManager entityManager;
    private final SoundManager soundManager;

    public static final int ALIEN_SCORE = 10;

    public GameEventHandler(GameContext gameContext, PlayerManager playerManager, EntityManager entityManager, SoundManager soundManager) {
        this.gameContext = gameContext;
        this.playerManager = playerManager;
        this.entityManager = entityManager;
        this.soundManager = soundManager;
    }

    public void notifyDeath() {
        gameContext.setNextState(GameState.Type.GAME_OVER);
        soundManager.stopAllSounds("ship-death-sound");
        soundManager.playSound("ship-death-sound");
    }

    public void notifyWin() {
        soundManager.stopAllSounds();
        gameContext.setCurrentState(GameState.Type.GAME_WON);
    }

    public void notifyAlienKilled() {
        playerManager.increaseScore(ALIEN_SCORE);
        entityManager.decreaseAlienCount();

        // Buff drop logic
        double roll = Math.random();
        if (roll < 0.05) { // 5% chance for invincibility
            gameContext.getShip().getBuffManager().addBuff(org.newdawn.spaceinvaders.player.BuffType.INVINCIBILITY);
        } else if (roll < 0.10) { // 5% chance for speed boost
            gameContext.getShip().getBuffManager().addBuff(org.newdawn.spaceinvaders.player.BuffType.SPEED_BOOST);
        } else if (roll < 0.15) { // 5% chance for heal
            gameContext.getShip().getBuffManager().addBuff(org.newdawn.spaceinvaders.player.BuffType.HEAL);
        }
    }

    public void notifyAlienEscaped(Entity entity) {
        entityManager.removeEntity(entity);
        entityManager.decreaseAlienCount();
    }

    public void notifyMeteorDestroyed(int scoreValue) {
        playerManager.increaseScore(scoreValue);
    }
}
