package org.newdawn.spaceinvaders.entity;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.Enemy.AlienEntity;
import org.newdawn.spaceinvaders.entity.Enemy.BombEntity;
import org.newdawn.spaceinvaders.entity.Enemy.BurstShooterEntity;
import org.newdawn.spaceinvaders.entity.Enemy.MeteorEnemyEntity;
import org.newdawn.spaceinvaders.entity.Enemy.ThreeWayShooter;

public class EnemyFactory {

    private final GameContext context;

    public EnemyFactory(GameContext context) {
        this.context = context;
    }

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
