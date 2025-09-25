package org.newdawn.spaceinvaders;

import org.newdawn.spaceinvaders.entity.AlienEntity;
import org.newdawn.spaceinvaders.entity.BossEntity;
import org.newdawn.spaceinvaders.entity.Entity;
import org.newdawn.spaceinvaders.entity.ShipEntity;

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

    public void initShip() {
        if (ship == null) {
            ship = new ShipEntity(context, "sprites/ship.gif", 370, 550);
        }
        entities.clear();
        addList.clear();
        removeList.clear();
        entities.add(ship);
        alienCount = 0;
    }

    public void spawnNext(int wave, int lineCount) {
        if (wave % 5 == 0) {
            // Boss wave - spawn boss on the first line count
            if (lineCount == 0) {
                double healthModifier = 1.0 + (wave - 1) / 20.0;
                int bossHealth = (int) (50 * healthModifier);
                Entity boss = new BossEntity(context, 350, 50, bossHealth);
                addList.add(boss);
                alienCount++;
            }
        } else {
            // Normal alien wave: spawn a single line
            double healthModifier = 1.0 + (wave - 1) / 20.0;
            int alienHealth = (int) (2 * healthModifier);
            int aliensInLine = 10;
            for (int i = 0; i < aliensInLine; i++) {
                Entity alien = new AlienEntity(context, 50 + (i * 60), -50, alienHealth);
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
