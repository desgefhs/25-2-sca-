package org.newdawn.spaceinvaders;

import org.newdawn.spaceinvaders.entity.AlienEntity;
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
    private ShipEntity ship;
    private int alienCount;

    public EntityManager(GameContext context) {
        this.context = context;
    }

    /**
     * 게임 시작 시 필요한 모든 엔티티(우주선, 외계인)를 생성하고 초기화합니다.
     */
    public void initEntities() {
        entities.clear();
        // 플레이어 우주선 생성
        ship = new ShipEntity(context, "sprites/ship.gif", 370, 550);
        entities.add(ship);

        // 외계인 무리 생성
        alienCount = 0;
        for (int row = 0; row < 5; row++) {
            for (int x = 0; x < 12; x++) {
                Entity alien = new AlienEntity(context, 100 + (x * 50), (50) + row * 30);
                entities.add(alien);
                alienCount++;
            }
        }
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public void removeEntity(Entity entity) {
        removeList.add(entity);
    }

    /**
     * 이번 프레임에서 제거하기로 표시된 모든 엔티티를 실제로 제거합니다.
     */
    public void cleanup() {
        entities.removeAll(removeList);
        removeList.clear();
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
     * 모든 외계인의 이동 속도를 증가시킵니다.
     */
    public void speedUpAliens() {
        for (Entity entity : entities) {
            if (entity instanceof AlienEntity) {
                entity.setHorizontalMovement(entity.getHorizontalMovement() * 1.02);
            }
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
