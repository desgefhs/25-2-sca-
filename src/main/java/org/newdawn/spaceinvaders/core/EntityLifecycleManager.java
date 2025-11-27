package org.newdawn.spaceinvaders.core;

import org.newdawn.spaceinvaders.core.events.*;
import org.newdawn.spaceinvaders.entity.*;
import org.newdawn.spaceinvaders.entity.Enemy.Enemy;

import java.util.ArrayList;
import java.util.List;

public class EntityLifecycleManager {

    public void processStateChanges(List<Entity> entities, EventBus eventBus, org.newdawn.spaceinvaders.entity.EntityManager entityManager) {
        for (Entity entity : new ArrayList<>(entities)) { // ConcurrentModificationException을 피하기 위해 복사본을 순회합니다.
            if (entity.isDestroyed()) {
                // 엔티티 유형에 따라 이벤트를 발행합니다.
                if (entity instanceof ShipEntity) {
                    eventBus.publish(new PlayerDiedEvent());
                } else if (entity instanceof Enemy) { // AlienEntity, BossEntity, ThreeWayShooter, BurstShooterEntity, MeteorEnemyEntity, BombEntity를 포함합니다.
                    eventBus.publish(new AlienKilledEvent());
                } else if (entity instanceof org.newdawn.spaceinvaders.entity.Enemy.MeteorEntity) { // 참고: MeteorEnemyEntity와는 다릅니다.
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