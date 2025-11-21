package org.newdawn.spaceinvaders.entity;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.events.AlienEscapedEvent;
import org.newdawn.spaceinvaders.entity.Enemy.*;
import org.newdawn.spaceinvaders.entity.weapon.Weapon;
import org.newdawn.spaceinvaders.player.PlayerStats;
import org.newdawn.spaceinvaders.wave.Formation;

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
    private final EnemyFactory enemyFactory;
    private ShipEntity ship;
    private int alienCount;

    public EntityManager(GameContext context) {
        this.context = context;
        this.enemyFactory = new EnemyFactory(context);
    }

    public void initShip(PlayerStats stats, Weapon weapon) {
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

    public void spawnFormation(Formation formation, int wave, boolean forceUpgrade) {
        int cycle = (wave - 1) / 5;
        double cycleMultiplier = Math.pow(1.2, cycle); // Slower scaling for normal aliens

        for (SpawnInfo info : formation.getSpawnList()) {
            Entity newEntity = enemyFactory.createEnemy(info, wave, cycleMultiplier);

            if (newEntity != null) {
                if (newEntity instanceof Enemy) {
                    // Upgrade if forced by the game manager, by the spawn info, or by random chance
                    if (forceUpgrade || info.forceUpgrade || (info.upgradeChance > 0 && Math.random() < info.upgradeChance)) {
                        ((Enemy) newEntity).upgrade();
                    }
                }

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
        // Find off-screen entities that need to be reported as escaped
        List<Entity> escapedEnemies = new ArrayList<>();
        for (Entity entity : entities) {
            if (entity.getX() < -50 || entity.getX() > 550 || entity.getY() < -300 || entity.getY() > 650) {
                if (entity instanceof Enemy) {
                    escapedEnemies.add(entity);
                }
            }
        }

        // Notify the game manager about escaped enemies
        for (Entity entity : escapedEnemies) {
            // This call will also add the entity to the removeList via the entity manager
            context.getEventBus().publish(new AlienEscapedEvent(entity));
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