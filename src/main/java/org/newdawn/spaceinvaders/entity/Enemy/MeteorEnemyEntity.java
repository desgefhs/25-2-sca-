package org.newdawn.spaceinvaders.entity.Enemy;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.*;
import org.newdawn.spaceinvaders.entity.Effect.AnimatedExplosionEntity;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileEntity;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileType;

/**
 * 주기적으로 총알을 발사하며 아래로 이동하는 적 엔티티
 */
public class MeteorEnemyEntity extends Entity implements Enemy {
    /** 발사 간격 (0.4초) */
    private static final long FIRING_INTERVAL = 400L;
    /** 마지막 발사 시간 */
    private long lastFire = 0;
    private GameContext context;
    /** 이동 속도 */
    private double moveSpeed = 75;

    /** 발사 상태  */
    private enum FiringState { FIRING, COOLDOWN }
    private FiringState currentState = FiringState.FIRING;
    private long stateTimer = 2000L;
    private static final long FIRING_DURATION = 2000L;
    private static final long COOLDOWN_DURATION = 1000L;

    /**
     * MeteorEnemyEntity 객체를 생성
     *
     * @param context 게임 컨텍스트
     * @param x       x 좌표
     * @param y       y 좌표
     */
    public MeteorEnemyEntity(GameContext context, int x, int y) {
        super("sprites/enemy/meteorEnemy.gif", x, y);
        this.context = context;
        this.health = new HealthComponent(this, 3); // 초기 체력 3
        this.dy = moveSpeed; // 아래로 이동
        setScale(1.5);
    }

    /**
     * 발사 간격에 따라 총알을 발사
     */
    private void tryToFire() {
        if (System.currentTimeMillis() - lastFire < FIRING_INTERVAL) {
            return;
        }
        lastFire = System.currentTimeMillis();

        ProjectileType type = ProjectileType.FAST_NORMAL_SHOT;
        // 왼쪽으로 비스듬히 발사
        ProjectileEntity shot = new ProjectileEntity(context, type, 1, getX() + (width / 2) - 15, getY() + height, -type.moveSpeed, 0);
        context.addEntity(shot);
    }

    @Override
    public void move(long delta) {
        super.move(delta);
        tryToFire(); // 매 프레임마다 발사 시도
    }

    public void collidedWith(Entity other) {
        // 플레이어의 발사체와 충돌 시
        if (other instanceof ProjectileEntity) {
            ProjectileEntity shot = (ProjectileEntity) other;
            if (shot.getType().targetType == ProjectileType.TargetType.ENEMY) {
                if (health.isAlive()) {
                    // 체력 감소 및 사망 처리
                    if (!health.decreaseHealth(shot.getDamage())) {
                        // 사망 시 폭발 효과 생성
                        AnimatedExplosionEntity explosion = new AnimatedExplosionEntity(context, 0, 0);
                        explosion.setScale(0.1);
                        int centeredX = this.getX() + (this.getWidth() / 2) - (explosion.getWidth() / 2);
                        int centeredY = (this.getY() + this.getHeight() / 2) - (explosion.getHeight() / 2);
                        explosion.setX(centeredX);
                        explosion.setY(centeredY);
                        context.addEntity(explosion);

                        // 자신을 제거하고 처치 알림
                        context.removeEntity(this);
                        context.notifyAlienKilled();
                    }
                }
                // 충돌한 플레이어 발사체 제거
                context.removeEntity(shot);
            }
        }
    }

    @Override
    public void upgrade() {
        // 이 엔티티는 강화될 수 없음
    }
}
