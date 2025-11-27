package org.newdawn.spaceinvaders.entity.Enemy;

import org.newdawn.spaceinvaders.core.GameContext;

import org.newdawn.spaceinvaders.entity.*;
import org.newdawn.spaceinvaders.entity.Effect.AnimatedExplosionEntity;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileEntity;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileType;

public class MeteorEnemyEntity extends Entity implements Enemy {
    private static final long FIRING_INTERVAL = 400L; // 0.4초
    private long lastFire = 0;
    private final GameContext context;
    private final double moveSpeed = 75;

    private enum FiringState { FIRING, COOLDOWN }
    private final FiringState currentState = FiringState.FIRING;
    private final long stateTimer = 2000L; // FIRING 상태에서 시작
    private static final long FIRING_DURATION = 2000L;
    private static final long COOLDOWN_DURATION = 1000L;

    public MeteorEnemyEntity(GameContext context, int x, int y) {
        super("sprites/enemy/meteorEnemy.gif", x, y);
        this.context = context;
        this.health = new HealthComponent(this, 3); // 초기 체력을 3으로 설정
        this.dy = moveSpeed; // 아래로 이동
        setScale(1.5);
    }

    private void tryToFire() {
        if (System.currentTimeMillis() - lastFire < FIRING_INTERVAL) {
            return;
        }
        lastFire = System.currentTimeMillis();

        ProjectileType type = ProjectileType.FAST_NORMAL_SHOT;
        ProjectileEntity shot = new ProjectileEntity(context, type, 1, getX() + (width / 2) - 15, getY() + height, -type.moveSpeed, 0);
        context.addEntity(shot);
    }

    @Override
    public void move(long delta) {
        super.move(delta);
        tryToFire();
    }

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

    @Override
    public void upgrade() {
        // 이 엔티티는 업그레이드할 수 없습니다.
    }
}