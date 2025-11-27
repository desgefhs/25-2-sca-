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
 * 게임에 존재하는 모든 엔티티(적, 플레이어, 발사체 등)를 생성, 저장, 관리하는 클래스.
 * 엔티티의 생명주기(추가, 제거, 업데이트)를 담당합니다.
 */
public class EntityManager {

    /** 게임의 전반적인 컨텍스트. */
    private final GameContext context;
    /** 현재 활성화된 모든 엔티티 목록. */
    private final List<Entity> entities = new ArrayList<>();
    /** 다음 프레임에 제거될 엔티티 목록. */
    private final List<Entity> removeList = new ArrayList<>();
    /** 다음 프레임에 추가될 엔티티 목록. */
    private final List<Entity> addList = new ArrayList<>();
    /** 적 생성을 담당하는 팩토리. */
    private final EnemyFactory enemyFactory;
    /** 플레이어의 함선 엔티티. */
    private ShipEntity ship;
    /** 현재 살아있는 외계인 수. */
    private int alienCount;

    /**
     * EntityManager 생성자.
     *
     * @param context 게임 컨텍스트
     */
    public EntityManager(GameContext context) {
        this.context = context;
        this.enemyFactory = new EnemyFactory(context);
    }

    /**
     * 플레이어 함선을 초기화하거나 재설정합니다.
     * 기존 함선이 없으면 새로 생성하고, 있으면 상태를 재설정합니다.
     *
     * @param stats 플레이어의 현재 스탯
     * @param weapon 플레이어의 현재 무기
     */
    public void initShip(PlayerStats stats, Weapon weapon) {
        if (ship == null) {
            ship = new ShipEntity(context, "sprites/ship.gif", Game.GAME_WIDTH / 2, 550, stats.getMaxHealth());
            entities.add(ship);
        } else {
            ship.setMaxHealth(stats.getMaxHealth());
            if (!entities.contains(ship)) {
                entities.add(ship);
            }
        }
        ship.setWeapon(weapon);
        ship.reset();

        // 함선을 제외한 모든 엔티티 제거
        entities.removeIf(entity -> !(entity instanceof ShipEntity));

        addList.clear();
        removeList.clear();
        alienCount = 0;
    }

    /**
     * 지정된 포메이션에 따라 적들을 스폰합니다.
     *
     * @param formation 적 스폰 정보가 담긴 포메이션
     * @param wave 현재 웨이브 번호
     * @param forceUpgrade 모든 적을 강제로 업그레이드할지 여부
     */
    public void spawnFormation(Formation formation, int wave, boolean forceUpgrade) {
        int cycle = (wave - 1) / 5;
        double cycleMultiplier = Math.pow(1.2, cycle); // 일반 외계인에 대한 느린 스케일링

        for (SpawnInfo info : formation.getSpawnList()) {
            Entity newEntity = enemyFactory.createEnemy(info, wave, cycleMultiplier);

            if (newEntity != null) {
                if (newEntity instanceof Enemy) {
                    // 게임 관리자, 스폰 정보 또는 무작위 확률에 의해 강제로 업그레이드
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
     * 다음 프레임에 추가할 엔티티 목록에 엔티티를 추가합니다.
     *
     * @param entity 추가할 엔티티
     */
    public void addEntity(Entity entity) {
        addList.add(entity);
    }

    /**
     * 다음 프레임에 제거할 엔티티 목록에 엔티티를 추가합니다.
     *
     * @param entity 제거할 엔티티
     */
    public void removeEntity(Entity entity) {
        removeList.add(entity);
    }

    /**
     * 프레임 종료 시 호출되어 엔티티 목록을 정리합니다.
     * 화면을 벗어난 엔티티를 처리하고, 제거 목록에 있는 엔티티를 실제로 제거하며, 추가 목록에 있는 엔티티를 활성 목록에 추가합니다.
     */
    public void cleanup() {
        // 탈출한 것으로 보고해야 하는 화면 밖 엔티티 찾기
        List<Entity> escapedEnemies = new ArrayList<>();
        for (Entity entity : entities) {
            if (entity.getX() < -50 || entity.getX() > 550 || entity.getY() < -300 || entity.getY() > 650) {
                if (entity instanceof Enemy) {
                    escapedEnemies.add(entity);
                }
            }
        }

        // 게임 관리자에게 탈출한 적에 대해 알림
        for (Entity entity : escapedEnemies) {
            // 이 호출은 엔티티 관리자를 통해 엔티티를 removeList에 추가합니다.
            context.getEventBus().publish(new AlienEscapedEvent(entity));
        }

        // ConcurrentModificationException을 피하기 위해 복사본을 순회합니다.
        List<Entity> toDestroy = new ArrayList<>(removeList);
        for (Entity entity : toDestroy) {
            entity.onDestroy();
        }

        // 이제 removeList에 자식(예: 화염 효과)이 포함될 수 있으므로 모두 제거합니다.
        entities.removeAll(removeList);
        entities.addAll(addList);
        removeList.clear();
        addList.clear();
    }

    /**
     * 현재 활성화된 모든 엔티티 목록을 반환합니다.
     *
     * @return 활성 엔티티 목록
     */
    public List<Entity> getEntities() {
        return entities;
    }

    /**
     * 플레이어 함선 엔티티를 반환합니다.
     *
     * @return 함선 엔티티
     */
    public ShipEntity getShip() {
        return ship;
    }

    /**
     * 현재 외계인 수를 반환합니다.
     *
     * @return 외계인 수
     */
    public int getAlienCount() {
        return alienCount;
    }

    /**
     * 외계인 수를 1 감소시킵니다.
     */
    public void decreaseAlienCount() {
        alienCount--;
    }

    /**
     * 외계인 수를 지정된 값으로 설정합니다.
     *
     * @param count 설정할 외계인 수
     */
    public void setAlienCount(int count) {
        this.alienCount = count;
    }

    /**
     * 모든 활성 엔티티를 지정된 시간만큼 움직입니다.
     *
     * @param delta 프레임 간 시간 간격 (밀리초)
     */
    public void moveAll(long delta) {
        for (Entity entity : entities) {
            entity.move(delta);
        }
    }

    /**
     * 모든 활성 엔티티의 개별 로직을 실행합니다 (예: 외계인 방향 전환, 발사 시도).
     */
    public void doLogicAll() {
        for (Entity entity : entities) {
            entity.doLogic();
        }
    }
}
