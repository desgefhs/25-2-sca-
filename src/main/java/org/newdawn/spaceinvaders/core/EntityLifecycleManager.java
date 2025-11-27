package org.newdawn.spaceinvaders.core;

import org.newdawn.spaceinvaders.core.events.*;
import org.newdawn.spaceinvaders.entity.*;
import org.newdawn.spaceinvaders.entity.Enemy.Enemy;

import java.util.ArrayList;
import java.util.List;

public class EntityLifecycleManager {

    public void processStateChanges(List<Entity> entities, EventBus eventBus, org.newdawn.spaceinvaders.entity.EntityManager entityManager) {
        for (Entity entity : new ArrayList<>(entities)) { // Iterate over a copy to avoid ConcurrentModificationException
            if (entity.isDestroyed()) {
                // Publish event based on entity type
                if (entity instanceof ShipEntity) {
                    eventBus.publish(new PlayerDiedEvent());
                } else if (entity instanceof Enemy) { // Covers AlienEntity, BossEntity, ThreeWayShooter, BurstShooterEntity, MeteorEnemyEntity, BombEntity
                    eventBus.publish(new AlienKilledEvent());
                } else if (entity instanceof org.newdawn.spaceinvaders.entity.Enemy.MeteorEntity) { // Note: This is different from MeteorEnemyEntity
                    eventBus.publish(new MeteorDestroyedEvent(((org.newdawn.spaceinvaders.entity.Enemy.MeteorEntity) entity).getScoreValue()));
                } else if (entity instanceof ItemEntity) {
                    eventBus.publish(new ItemCollectedEvent());
                }

                // Add the entity to the remove list for cleanup
                entityManager.removeEntity(entity);
            }
        }
    }
}