package org.newdawn.spaceinvaders.core;

import org.newdawn.spaceinvaders.entity.Entity;
import org.newdawn.spaceinvaders.entity.EntityManager;
import org.newdawn.spaceinvaders.entity.ShipEntity;
import org.newdawn.spaceinvaders.view.Background;
import org.newdawn.spaceinvaders.wave.WaveManager;
import org.newdawn.spaceinvaders.core.EntityLifecycleManager;

import java.util.List;

public class GameWorld {

    private final EntityManager entityManager;
    private final Background background;
    private final WaveManager waveManager;
    private final GameContext gameContext;
    private final EntityLifecycleManager entityLifecycleManager;

    public GameWorld(EntityManager entityManager, Background background, WaveManager waveManager, GameContext gameContext, EntityLifecycleManager entityLifecycleManager) {
        this.entityManager = entityManager;
        this.background = background;
        this.waveManager = waveManager;
        this.gameContext = gameContext;
        this.entityLifecycleManager = entityLifecycleManager;
    }

    public void update(long delta) {
        background.update(delta);
        waveManager.update(delta);
        entityManager.moveAll(delta);
        new CollisionDetector().checkCollisions(entityManager.getEntities());

        // Process entities that are marked for destruction and publish events
        entityLifecycleManager.processStateChanges(entityManager.getEntities(), gameContext.getEventBus(), entityManager);

        entityManager.cleanup();

        if (entityManager.getAlienCount() == 0 && waveManager.hasFinishedSpawning()) {
            gameContext.onWaveCleared();
        }
    }

    public void addEntity(Entity entity) {
        entityManager.addEntity(entity);
    }

    public void removeEntity(Entity entity) {
        entityManager.removeEntity(entity);
    }

    public List<Entity> getEntities() {
        return entityManager.getEntities();
    }

    public ShipEntity getShip() {
        return entityManager.getShip();
    }
    
    public EntityManager getEntityManager() {
        return entityManager;
    }

    public Background getBackground() {
        return background;
    }

    public WaveManager getWaveManager() {
        return waveManager;
    }
}
