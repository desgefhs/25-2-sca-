package org.newdawn.spaceinvaders.entity;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.Enemy.AlienEntity;
import org.newdawn.spaceinvaders.entity.Enemy.BombEntity;
import org.newdawn.spaceinvaders.entity.Enemy.BurstShooterEntity;
import org.newdawn.spaceinvaders.entity.Enemy.MeteorEnemyEntity;
import org.newdawn.spaceinvaders.entity.Enemy.ThreeWayShooter;

/**
 * 적(Enemy) 객체 생성을 전담하는 팩토리 클래스.
 * EntityManager의 생성 로직을 분리하여 단일 책임 원칙을 따릅니다.
 */
public class EnemyFactory {

    /**
     * 게임의 전반적인 컨텍스트.
     */
    private final GameContext context;

    /**
     * EnemyFactory 생성자.
     *
     * @param context 게임 컨텍스트
     */
    public EnemyFactory(GameContext context) {
        this.context = context;
    }

    /**
     * 스폰 정보에 따라 적 엔티티를 생성합니다.
     *
     * @param info 스폰될 적의 정보 (위치, 타입 등)
     * @param wave 현재 웨이브 번호
     * @param cycleMultiplier 난이도 조정을 위한 배율
     * @return 생성된 적 엔티티
     * @throws IllegalArgumentException 알려지지 않은 엔티티 타입이 주어질 경우
     */
    public Entity createEnemy(SpawnInfo info, int wave, double cycleMultiplier) {
        Entity newEntity = null;
        int baseAlienHealth = 2;

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
            default:
                throw new IllegalArgumentException("Unknown entity type: " + info.entityType);
        }
        return newEntity;
    }
}
