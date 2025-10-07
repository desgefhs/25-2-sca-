package org.newdawn.spaceinvaders.entity;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;

import org.newdawn.spaceinvaders.entity.Enemy.AlienEntity;
import org.newdawn.spaceinvaders.entity.Enemy.MeteorEntity;
import org.newdawn.spaceinvaders.entity.Enemy.ThreeWayShooter;
import org.newdawn.spaceinvaders.entity.Pet.PetEntity;
import org.newdawn.spaceinvaders.player.PlayerStats;

import java.util.ArrayList;
import java.util.List;

/**
 * 게임에 존재하는 모든 엔티티를 생성, 저장, 관리하는 책임을 가지는 클래스.
 */
public class EntityManager {

    private final GameContext context;
    private final List<Entity> entities = new ArrayList<>();
    private final List<Entity> removeList = new ArrayList<>();
    private final List<Entity> addList = new ArrayList<>();
    private ShipEntity ship;
    private int alienCount;

    public EntityManager(GameContext context) {
        this.context = context;
    }

    public void initShip(PlayerStats stats, org.newdawn.spaceinvaders.entity.weapon.Weapon weapon) {
        if (ship == null) {
            ship = new ShipEntity(context, "sprites/ship.gif", Game.GAME_WIDTH / 2, 550, stats.getMaxHealth());
            entities.add(ship);
        } else {
            ship.setMaxHealth(stats.getMaxHealth());
        }
        ship.setWeapon(weapon);
        ship.reset();

        // Remove all entities except the ship
        entities.removeIf(entity -> !(entity instanceof ShipEntity));

        addList.clear();
        removeList.clear();
        alienCount = 0;
    }

    public void spawnFormation(org.newdawn.spaceinvaders.wave.Formation formation, int wave) {
        int cycle = (wave - 1) / 5;
        double cycleMultiplier = Math.pow(1.2, cycle); // Slower scaling for normal aliens
        int baseAlienHealth = 2;

        for (SpawnInfo info : formation.getSpawnList()) {
            Entity newEntity = null;
            switch (info.entityType) {
                case ALIEN:
                    // Calculate health based on wave
                    int alienHealth = (int) (baseAlienHealth * cycleMultiplier) + (wave / 2);

                    // Create alien with movement pattern
                    AlienEntity alien = new AlienEntity(context, info.x, info.y, alienHealth, cycle, info.movementPattern);

                    // Handle random upgrade
                    if (info.upgradeChance > 0 && Math.random() < info.upgradeChance) {
                        alien.upgrade();
                    }
                    newEntity = alien;
                    break;
                case THREE_WAY_SHOOTER:
                    // This entity type also needs to be updated to support the new spawning system
                    newEntity = new ThreeWayShooter(context, info.x, info.y);
                    break;
                // Add cases for other entity types here
            }
            if (newEntity != null) {
                addEntity(newEntity);
                if (!(newEntity instanceof MeteorEntity)) {
                    alienCount++;
                }
            }
        }
    }

    public void addEntity(Entity entity) {
        addList.add(entity);
    }

    public void removeEntity(Entity entity) {
        removeList.add(entity);
    }

    /**
     * 이번 프레임에서 제거하기로 표시된 모든 엔티티를 실제로 제거합니다.
     */
    public void cleanup() {
        // Garbage collect entities that have gone off-screen
        for (Entity entity : entities) {
            if (entity.getX() < -50 || entity.getX() > 550 || entity.getY() < -50 || entity.getY() > 650) {
                if (!(entity instanceof ShipEntity) && !(entity instanceof PetEntity)) {
                    removeList.add(entity);
                }
            }
        }

        // Create a copy to iterate over, to avoid ConcurrentModificationException
        List<Entity> toDestroy = new ArrayList<>(removeList);
        for (Entity entity : toDestroy) {
            entity.onDestroy();
        }

        // Now the removeList may contain children (like fire effects), remove them all
        entities.removeAll(removeList);
        entities.addAll(addList);
        removeList.clear();
        addList.clear();
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public ShipEntity getShip() {
        return ship;
    }

    public int getAlienCount() {
        return alienCount;
    }

    public void decreaseAlienCount() {
        alienCount--;
    }

    public void setAlienCount(int count) {
        this.alienCount = count;
    }

    /**
     * 모든 엔티티를 지정된 시간만큼 움직입니다.
     * @param delta 프레임 간 시간 간격
     */
    public void moveAll(long delta) {
        for (Entity entity : entities) {
            entity.move(delta);
        }
    }

    /**
     * 모든 엔티티의 개인 로직을 실행합니다 (예: 외계인 방향 전환).
     */
    public void doLogicAll() {
        for (Entity entity : entities) {
            entity.doLogic();
        }
    }
}