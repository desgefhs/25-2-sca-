package org.newdawn.spaceinvaders.entity;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.Enemy.*;
import org.newdawn.spaceinvaders.entity.Pet.PetEntity;
import org.newdawn.spaceinvaders.entity.weapon.Weapon;
import org.newdawn.spaceinvaders.player.PlayerStats;
import org.newdawn.spaceinvaders.wave.Formation;

import java.util.ArrayList;
import java.util.List;

/**
 * 게임에 존재하는 모든 엔티티를 생성, 저장, 관리하는 클래스
 * 엔티티의 추가, 제거, 업데이트를 중앙에서 처리
 */
public class EntityManager {

    private final GameContext context;
    /** 현재 게임에 존재하는 모든 엔티티 목록 */
    private final List<Entity> entities = new ArrayList<>();
    /** 다음 프레임에 제거될 엔티티 목록 */
    private final List<Entity> removeList = new ArrayList<>();
    /** 다음 프레임에 추가될 엔티티 목록 */
    private final List<Entity> addList = new ArrayList<>();
    private ShipEntity ship;
    private int alienCount;

    /**
     * EntityManager 객체를 생성
     *
     * @param context 게임 컨텍스트
     */
    public EntityManager(GameContext context) {
        this.context = context;
    }

    /**
     * 플레이어의 우주선을 초기화하거나 재설정
     *
     * @param stats  플레이어의 능력치
     * @param weapon 장착할 무기
     */
    public void initShip(PlayerStats stats, Weapon weapon) {
        if (ship == null) {
            ship = new ShipEntity(context, "sprites/ship.gif", Game.GAME_WIDTH / 2, 550, stats.getMaxHealth());
            entities.add(ship);
        } else {
            ship.setMaxHealth(stats.getMaxHealth());
        }
        ship.setWeapon(weapon);
        ship.reset();

        // 우주선을 제외한 모든 엔티티 제거
        entities.removeIf(entity -> !(entity instanceof ShipEntity));

        addList.clear();
        removeList.clear();
        alienCount = 0;
    }

    /**
     * 주어진 포메이션 정보에 따라 적들을 생성
     *
     * @param formation    생성할 적들의 포메이션 정보
     * @param wave         현재 웨이브 번호
     * @param forceUpgrade 적을 강제로 업그레이드할지 여부
     */
    public void spawnFormation(Formation formation, int wave, boolean forceUpgrade) {
        int cycle = (wave - 1) / 5;
        double cycleMultiplier = Math.pow(1.2, cycle); // 일반 적을 위한 느린 스케일링
        int baseAlienHealth = 2;

        for (SpawnInfo info : formation.getSpawnList()) {
            Entity newEntity = null;
            switch (info.entityType) {
                case ALIEN:
                    int alienHealth = (int) (baseAlienHealth * cycleMultiplier) + (wave / 2);
                    AlienEntity alien = new AlienEntity(context, info.x, info.y, alienHealth, info.movementPattern);
                    if (info.movementPattern == MovementPattern.STRAIGHT_UP) {
                        alien.setVerticalMovement(-alien.getMoveSpeed());
                    }
                    newEntity = alien;
                    break;
                case THREE_WAY_SHOOTER:
                    newEntity = new ThreeWayShooter(context, info.x, info.y, info.movementPattern);
                    break;
                case BOMB:
                    newEntity = new BombEntity(context, info.x, info.y);
                    break;
                case METEOR_ENEMY:
                    newEntity = new MeteorEnemyEntity(context, info.x, info.y);
                    break;
                case BURST_SHOOTER:
                    newEntity = new BurstShooterEntity(context, info.x, info.y);
                    break;
            }

            if (newEntity != null) {
                if (newEntity instanceof Enemy) {
                    // 게임 매니저, 스폰 정보, 또는 확률에 의해 강제 업그레이드
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

    /**
     * 다음 프레임에 추가할 엔티티 목록에 엔티티를 추가
     *
     * @param entity 추가할 엔티티
     */
    public void addEntity(Entity entity) {
        addList.add(entity);
    }

    /**
     * 다음 프레임에 제거할 엔티티 목록에 엔티티를 추가
     *
     * @param entity 제거할 엔티티
     */
    public void removeEntity(Entity entity) {
        removeList.add(entity);
    }

    /**
     * 매 프레임의 끝에서 호출되어 엔티티 목록을 정리
     * 화면 밖으로 나간 엔티티를 처리하고, 제거 목록에 있는 엔티티를 실제로 제거하며, 추가 목록에 있는 엔티티를 추가
     */
    public void cleanup() {
        // 화면 밖으로 나가 탈출한 것으로 처리해야 할 엔티티 찾기
        List<Entity> escapedEnemies = new ArrayList<>();
        for (Entity entity : entities) {
            if (entity.getX() < -50 || entity.getX() > 550 || entity.getY() < -300 || entity.getY() > 650) {
                if (entity instanceof Enemy) {
                    escapedEnemies.add(entity);
                }
            }
        }

        // 게임 매니저에게 탈출한 적 알림
        for (Entity entity : escapedEnemies) {
            context.notifyAlienEscaped(entity);
        }

        // ConcurrentModificationException을 피하기 위해 복사본을 만들어 반복
        List<Entity> toDestroy = new ArrayList<>(removeList);
        for (Entity entity : toDestroy) {
            entity.onDestroy();
        }

        // 이제 removeList에 자식 엔티티(예: 폭발 효과)가 포함될 수 있으므로 모두 제거
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
     * 모든 엔티티를 지정된 시간만큼 움직
     * @param delta 프레임 간 시간 간격
     */
    public void moveAll(long delta) {
        for (Entity entity : entities) {
            entity.move(delta);
        }
    }

    /**
     * 모든 엔티티의 개별 로직을 실행 (예: 에일리언 방향 전환).
     */
    public void doLogicAll() {
        for (Entity entity : entities) {
            entity.doLogic();
        }
    }
}