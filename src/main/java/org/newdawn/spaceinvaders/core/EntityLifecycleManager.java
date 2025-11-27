package org.newdawn.spaceinvaders.core;

import org.newdawn.spaceinvaders.core.events.*;
import org.newdawn.spaceinvaders.entity.Entity;
import org.newdawn.spaceinvaders.entity.EntityManager;
import org.newdawn.spaceinvaders.entity.ItemEntity;
import org.newdawn.spaceinvaders.entity.ShipEntity;
import org.newdawn.spaceinvaders.entity.Enemy.Enemy;

import java.util.ArrayList;
import java.util.List;

/**
 * 엔티티의 생명주기 중 '파괴' 상태를 처리하는 클래스.
 * 파괴된 엔티티를 감지하고, 그에 맞는 이벤트를 발행하며, 최종적으로 게임 월드에서 제거되도록 예약합니다.
 */
public class EntityLifecycleManager {

    /**
     * 엔티티 목록을 순회하며 파괴(destroyed) 상태인 엔티티를 처리합니다.
     * 파괴된 엔티티의 타입에 따라 적절한 이벤트(예: {@link PlayerDiedEvent})를 발행하고,
     * {@link EntityManager}의 제거 목록에 추가합니다.
     *
     * @param entities 현재 게임 월드의 엔티티 목록
     * @param eventBus 이벤트를 발행할 이벤트 버스
     * @param entityManager 엔티티 제거를 예약할 엔티티 매니저
     */
    public void processStateChanges(List<Entity> entities, EventBus eventBus, EntityManager entityManager) {
        // ConcurrentModificationException을 피하기 위해 복사본을 순회합니다.
        for (Entity entity : new ArrayList<>(entities)) {
            if (entity.isDestroyed()) {
                // 엔티티 유형에 따라 이벤트를 발행합니다.
                if (entity instanceof ShipEntity) {
                    eventBus.publish(new PlayerDiedEvent());
                } else if (entity instanceof Enemy) {
                    eventBus.publish(new AlienKilledEvent());
                } else if (entity instanceof org.newdawn.spaceinvaders.entity.Enemy.MeteorEntity) {
                    eventBus.publish(new MeteorDestroyedEvent(((org.newdawn.spaceinvaders.entity.Enemy.MeteorEntity) entity).getScoreValue()));
                } else if (entity instanceof ItemEntity) {
                    eventBus.publish(new ItemCollectedEvent());
                }

                // 정리를 위해 제거 목록에 엔티티를 추가합니다.
                entityManager.removeEntity(entity);
            }
        }
    }
}