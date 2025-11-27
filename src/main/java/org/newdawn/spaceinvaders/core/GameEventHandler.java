package org.newdawn.spaceinvaders.core;

import org.newdawn.spaceinvaders.core.events.*;
import org.newdawn.spaceinvaders.entity.EntityManager;
import org.newdawn.spaceinvaders.player.PlayerManager;
import org.newdawn.spaceinvaders.sound.SoundManager;

/**
 * 게임 내에서 발생하는 모든 이벤트를 수신하고 처리하는 중앙 이벤트 핸들러.
 * {@link EventListener}를 구현하며, {@link EventBus}에 등록되어
 * 다양한 타입의 이벤트를 각각의 처리 메소드로 위임합니다.
 */
public class GameEventHandler implements EventListener {

    private final GameContext gameContext;
    private final PlayerManager playerManager;
    private final EntityManager entityManager;
    private final SoundManager soundManager;
    private final GameSession gameSession;

    /** 외계인 처치 시 획득하는 점수. */
    public static final int ALIEN_SCORE = 10;

    /**
     * GameEventHandler 생성자.
     * @param gameContext 게임 컨텍스트
     * @param playerManager 플레이어 매니저
     * @param entityManager 엔티티 매니저
     * @param soundManager 사운드 매니저
     * @param gameSession 현재 게임 세션
     */
    public GameEventHandler(GameContext gameContext, PlayerManager playerManager, EntityManager entityManager, SoundManager soundManager, GameSession gameSession) {
        this.gameContext = gameContext;
        this.playerManager = playerManager;
        this.entityManager = entityManager;
        this.soundManager = soundManager;
        this.gameSession = gameSession;
    }

    /**
     * {@link EventBus}로부터 이벤트를 수신하여 타입에 맞는 핸들러 메소드를 호출합니다.
     * @param event 발행된 이벤트 객체
     */
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

    /** 아이템 수집 이벤트를 처리합니다. */
    private void handleItemCollected() {
        gameSession.notifyItemCollected();
    }

    /** 플레이어 사망 이벤트를 처리합니다. */
    private void handlePlayerDied() {
        gameContext.setNextState(GameState.Type.GAME_OVER);
        soundManager.stopAllSounds("ship-death-sound");
        soundManager.playSound("ship-death-sound");
    }

    /** 게임 승리 이벤트를 처리합니다. */
    private void handleGameWon() {
        soundManager.stopAllSounds();
        gameContext.setCurrentState(GameState.Type.GAME_WON);
    }

    /** 외계인 처치 이벤트를 처리합니다. */
    private void handleAlienKilled() {
        playerManager.increaseScore(ALIEN_SCORE);
        entityManager.decreaseAlienCount();

        // 버프 드롭 로직
        double roll = Math.random();
        if (roll < 0.05) { // 5% 확률로 무적
            gameContext.getShip().getBuffManager().addBuff(org.newdawn.spaceinvaders.player.BuffType.INVINCIBILITY);
        } else if (roll < 0.10) { // 5% 확률로 속도 증가
            gameContext.getShip().getBuffManager().addBuff(org.newdawn.spaceinvaders.player.BuffType.SPEED_BOOST);
        } else if (roll < 0.15) { // 5% 확률로 회복
            gameContext.getShip().getBuffManager().addBuff(org.newdawn.spaceinvaders.player.BuffType.HEAL);
        }
    }

    /** 외계인 탈출 이벤트를 처리합니다. */
    private void handleAlienEscaped(AlienEscapedEvent event) {
        entityManager.removeEntity(event.getAlien());
        entityManager.decreaseAlienCount();
    }

    /** 메테오 파괴 이벤트를 처리합니다. */
    private void handleMeteorDestroyed(MeteorDestroyedEvent event) {
        playerManager.increaseScore(event.getScoreValue());
    }
}