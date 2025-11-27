package org.newdawn.spaceinvaders.entity.Enemy;

import org.newdawn.spaceinvaders.core.GameContext;

import org.newdawn.spaceinvaders.entity.*;
import org.newdawn.spaceinvaders.entity.Effect.AnimatedExplosionEntity;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileEntity;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileType;

/**
 * 발사체를 발사하는 운석 형태의 적 엔티티.
 * 일정한 이동 속도로 움직이며 주기적으로 발사체를 발사하고, 플레이어의 발사체와 충돌을 처리합니다.
 */
public class MeteorEnemyEntity extends Entity implements Enemy {
    /** 발사 간격 (밀리초). */
    private static final long FIRING_INTERVAL = 400L;
    /** 마지막 발사 이후 시간. */
    private long lastFire = 0;
    /** 게임 컨텍스트. */
    private final GameContext context;
    /** 운석의 이동 속도. */
    private final double moveSpeed = 75;

    /** 발사 상태를 정의하는 열거형 (현재 사용되지 않음). */
    private enum FiringState { FIRING, COOLDOWN }
    private final FiringState currentState = FiringState.FIRING;
    private final long stateTimer = 2000L; // FIRING 상태에서 시작
    private static final long FIRING_DURATION = 2000L;
    private static final long COOLDOWN_DURATION = 1000L;

    /**
     * MeteorEnemyEntity 생성자.
     * @param context 게임 컨텍스트
     * @param x 초기 x 좌표
     * @param y 초기 y 좌표
     */
    public MeteorEnemyEntity(GameContext context, int x, int y) {
        super("sprites/enemy/meteorEnemy.gif", x, y);
        this.context = context;
        this.health = new HealthComponent(this, 3); // 초기 체력을 3으로 설정
        this.dy = moveSpeed; // 아래로 이동
        setScale(1.5);
    }

    /**
     * 발사 간격에 따라 발사체를 발사하려고 시도합니다.
     */
    private void tryToFire() {
        if (System.currentTimeMillis() - lastFire < FIRING_INTERVAL) {
            return;
        }
        lastFire = System.currentTimeMillis();

        ProjectileType type = ProjectileType.FAST_NORMAL_SHOT;
        ProjectileEntity shot = new ProjectileEntity(context, type, 1, getX() + (width / 2) - 15, getY() + height, -type.moveSpeed, 0);
        context.addEntity(shot);
    }

    /**
     * 엔티티를 이동시키고 발사를 시도합니다.
     * @param delta 마지막 업데이트 이후 경과 시간
     */
    @Override
    public void move(long delta) {
        super.move(delta);
        tryToFire();
    }

    /**
     * 다른 엔티티와의 충돌을 처리합니다.
     * 플레이어의 발사체와 충돌하면 피해를 입고, 체력이 0이 되면 파괴됩니다.
     * @param other 충돌한 다른 엔티티
     */
    @Override
    public void collidedWith(Entity other) {
        // 플레이어의 발사체와 충돌하면 피해를 입습니다.
        if (other instanceof ProjectileEntity) {
            ProjectileEntity shot = (ProjectileEntity) other;
            if (shot.getType().targetType == ProjectileType.TargetType.ENEMY) {
                if (health.isAlive()) {
                    // 체력을 감소시키고 파괴되었는지 확인합니다.
                    if (!health.decreaseHealth(shot.getDamage())) {
                        // 죽을 때 폭발을 생성합니다.
                        AnimatedExplosionEntity explosion = new AnimatedExplosionEntity(context, 0, 0);
                        explosion.setScale(0.1);
                        int centeredX = this.getX() + (this.getWidth() / 2) - (explosion.getWidth() / 2);
                        int centeredY = (this.getY() + this.getHeight() / 2) - (explosion.getHeight() / 2);
                        explosion.setX(centeredX);
                        explosion.setY(centeredY);
                        context.addEntity(explosion);

                        this.destroy();
                    }
                }
                // 충돌 시 플레이어의 발사체를 제거합니다.
                context.removeEntity(shot);
            }
        }
    }

    /**
     * 이 엔티티는 업그레이드할 수 없습니다.
     */
    @Override
    public void upgrade() {
        // 이 엔티티는 업그레이드할 수 없습니다.
    }
}