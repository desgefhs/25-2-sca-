package org.newdawn.spaceinvaders.core;

import org.newdawn.spaceinvaders.entity.Entity;
import org.newdawn.spaceinvaders.entity.EntityManager;
import org.newdawn.spaceinvaders.entity.ShipEntity;
import org.newdawn.spaceinvaders.view.Background;
import org.newdawn.spaceinvaders.wave.WaveManager;

import java.util.List;

public class GameWorld {

    private final EntityManager entityManager;
    private final Background background;
    private final WaveManager waveManager;
    private final GameContext gameContext;

    public GameWorld(EntityManager entityManager, Background background, WaveManager waveManager, GameContext gameContext) {
        this.entityManager = entityManager;
        this.background = background;
        this.waveManager = waveManager;
        this.gameContext = gameContext;
    }

    public void update(long delta) {
        background.update(delta);
        waveManager.update(delta);
        entityManager.moveAll(delta);
        new CollisionDetector().checkCollisions(entityManager.getEntities());
        entityManager.cleanup();

        if (entityManager.getAlienCount() == 0 && waveManager.getFormationsSpawnedInWave() >= waveManager.getFormationsPerWave()) {
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
