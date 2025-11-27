package org.newdawn.spaceinvaders.core;

import org.newdawn.spaceinvaders.core.events.*;
import org.newdawn.spaceinvaders.entity.Entity;
import org.newdawn.spaceinvaders.entity.EntityManager;
import org.newdawn.spaceinvaders.player.PlayerManager;
import org.newdawn.spaceinvaders.sound.SoundManager;

public class GameEventHandler implements EventListener {

    private final GameContext gameContext;
    private final PlayerManager playerManager;
    private final EntityManager entityManager;
    private final SoundManager soundManager;
    private final GameSession gameSession;

    public static final int ALIEN_SCORE = 10;

    public GameEventHandler(GameContext gameContext, PlayerManager playerManager, EntityManager entityManager, SoundManager soundManager, GameSession gameSession) {
        this.gameContext = gameContext;
        this.playerManager = playerManager;
        this.entityManager = entityManager;
        this.soundManager = soundManager;
        this.gameSession = gameSession;
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof PlayerDiedEvent) {
            handlePlayerDied();
        } else if (event instanceof GameWonEvent) {
            handleGameWon();
        } else if (event instanceof AlienKilledEvent) {
            handleAlienKilled();
        } else if (event instanceof AlienEscapedEvent) {
            handleAlienEscaped((AlienEscapedEvent) event);
        } else if (event instanceof MeteorDestroyedEvent) {
            handleMeteorDestroyed((MeteorDestroyedEvent) event);
        } else if (event instanceof ItemCollectedEvent) {
            handleItemCollected();
        }
    }

    private void handleItemCollected() {
        gameSession.notifyItemCollected();
    }

    private void handlePlayerDied() {
        gameContext.setNextState(GameState.Type.GAME_OVER);
        soundManager.stopAllSounds("ship-death-sound");
        soundManager.playSound("ship-death-sound");
    }

    private void handleGameWon() {
        soundManager.stopAllSounds();
        gameContext.setCurrentState(GameState.Type.GAME_WON);
    }

    private void handleAlienKilled() {
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

    private void handleAlienEscaped(AlienEscapedEvent event) {
        entityManager.removeEntity(event.getAlien());
        entityManager.decreaseAlienCount();
    }

    private void handleMeteorDestroyed(MeteorDestroyedEvent event) {
        playerManager.increaseScore(event.getScoreValue());
    }
}
