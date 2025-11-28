package org.newdawn.spaceinvaders.core;

import org.newdawn.spaceinvaders.entity.Entity;
import org.newdawn.spaceinvaders.entity.EntityManager;
import org.newdawn.spaceinvaders.entity.ShipEntity;
import org.newdawn.spaceinvaders.entity.Projectile.LaserEntity;
import org.newdawn.spaceinvaders.view.Background;
import org.newdawn.spaceinvaders.wave.WaveManager;

import java.util.List;

/**
 * 활성화된 게임 플레이 세계를 캡슐화하는 클래스.
 * 엔티티, 웨이브, 배경 등 게임 월드를 구성하는 요소들을 포함하며,
 * 플레이 상태의 주요 업데이트 로직(이동, 충돌 감지, 엔티티 상태 변경 등)을 실행합니다.
 */
public class GameWorld {

    private final EntityManager entityManager;
    private final Background background;
    private final WaveManager waveManager;
    private final GameContext gameContext;
    private final EntityLifecycleManager entityLifecycleManager;

    /**
     * GameWorld 생성자.
     * @param entityManager 엔티티 매니저
     * @param background 배경
     * @param waveManager 웨이브 매니저
     * @param gameContext 게임 컨텍스트
     * @param entityLifecycleManager 엔티티 생명주기 매니저
     */
    public GameWorld(EntityManager entityManager, Background background, WaveManager waveManager, GameContext gameContext, EntityLifecycleManager entityLifecycleManager) {
        this.entityManager = entityManager;
        this.background = background;
        this.waveManager = waveManager;
        this.gameContext = gameContext;
        this.entityLifecycleManager = entityLifecycleManager;
    }

    /**
     * 게임 월드의 상태를 업데이트합니다.
     * @param delta 마지막 프레임 이후 경과 시간
     */
    public void update(long delta) {
        background.update(delta);
        waveManager.update(delta);
        entityManager.moveAll(delta);
        new CollisionDetector().checkCollisions(entityManager.getEntities());

        handleGlobalLaser();

        // 파괴되도록 표시된 엔티티를 처리하고 이벤트를 발행합니다.
        entityLifecycleManager.processStateChanges(entityManager.getEntities(), gameContext.getEventBus(), entityManager);

        entityManager.cleanup();

        // 모든 적이 처치되고 스폰이 완료되면 웨이브 클리어 처리
        if (entityManager.getAlienCount() == 0 && waveManager.hasFinishedSpawning()) {
            gameContext.onWaveCleared();
        }
    }

    /**
     * 전역 레이저 패턴의 데미지 로직을 처리합니다.
     * 레이저가 존재할 때 플레이어가 모든 아이템을 수집하지 않았다면 함선을 파괴합니다.
     */
    private void handleGlobalLaser() {
        boolean laserActive = false;
        for (Entity entity : getEntities()) {
            if (entity instanceof LaserEntity) {
                laserActive = true;
                break;
            }
        }

        if (laserActive) {
            if (!gameContext.hasCollectedAllItems()) {
                ShipEntity ship = getShip();
                if (ship != null) {
                    ship.destroy();
                }
            }
        }
    }

    // EntityManager에 대한 위임 메소드들
    public void addEntity(Entity entity) { entityManager.addEntity(entity); }
    public void removeEntity(Entity entity) { entityManager.removeEntity(entity); }
    public List<Entity> getEntities() { return entityManager.getEntities(); }
    public ShipEntity getShip() { return entityManager.getShip(); }
    
    // 내부 컴포넌트 Getter
    public EntityManager getEntityManager() { return entityManager; }
    public Background getBackground() { return background; }
    public WaveManager getWaveManager() { return waveManager; }
}