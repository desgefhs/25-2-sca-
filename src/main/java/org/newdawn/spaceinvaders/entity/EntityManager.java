package org.newdawn.spaceinvaders.entity;

import org.newdawn.spaceinvaders.core.GameContext;

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

    public void initShip(PlayerStats stats) {
        if (ship == null) {
            ship = new ShipEntity(context, "sprites/ship.gif", 370, 550, stats.getMaxHealth());
            ship.reset();
        } else {
            ship.setMaxHealth(stats.getMaxHealth());
        }
        entities.clear();
        addList.clear();
        removeList.clear();
        entities.add(ship);
        alienCount = 0;
    }

    public void spawnNext(int wave, int lineCount) {
        int effectiveWave = ((wave - 1) % 5) + 1;
        int cycle = (wave - 1) / 5;
        int waveInCycle = (wave - 1) % 5;

        // Exponential scaling per cycle (1.5x every 5 waves)
        double cycleMultiplier = Math.pow(1.5, cycle);

        if (effectiveWave == 5) {
            // Boss wave - spawn boss on the first line count
            if (lineCount == 0) {
                int baseBossHealth = (int) (50 * cycleMultiplier);
                int fixedBossBonus = waveInCycle * 10; // Add 10 health for each wave in the cycle
                int bossHealth = baseBossHealth + fixedBossBonus;
                Entity boss = new BossEntity(context, 350, 50, bossHealth, cycle);
                addList.add(boss);
                alienCount++;
            }
        } else {
            // Normal alien wave: spawn a single line
            int baseAlienHealth = (int) (2 * cycleMultiplier);
            int fixedAlienBonus = waveInCycle; // Add 1 health for each wave in the cycle
            int alienHealth = baseAlienHealth + fixedAlienBonus;
            int aliensInLine = 10;
            for (int i = 0; i < aliensInLine; i++) {
                Entity alien = new AlienEntity(context, 50 + (i * 60), -50, alienHealth, cycle);
                addList.add(alien);
                alienCount++;
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
