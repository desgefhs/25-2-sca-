package org.newdawn.spaceinvaders.entity.Enemy;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.events.AlienKilledEvent;
import org.newdawn.spaceinvaders.entity.*;
import org.newdawn.spaceinvaders.entity.Effect.AnimatedExplosionEntity;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileEntity;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileType;

public class BurstShooterEntity extends Entity implements Enemy {
    private final double moveSpeed = 75;
    private final GameContext context;

    private long lastBurstTime = 0;
    private final long burstInterval = 2000; // 버스트 간 시간

    // 발사 상태
    private enum FiringState {IDLE, NORMAL_BURST, UPGRADED_BURST}

    private FiringState firingState = FiringState.IDLE;
    private int shotsFiredInBurst = 0;
    private long lastShotInBurstTime = 0;

    // 일반 버스트
    private static final int NORMAL_BURST_COUNT = 5;
    private static final long NORMAL_BURST_INTERVAL = 400; // 2초 동안 5발

    // 업그레이드된 버스트
    private static final int UPGRADED_BURST_COUNT = 6;
    private static final long UPGRADED_BURST_INTERVAL = 250; // 3초 동안 6발

    // 업그레이드 상태
    private boolean isUpgraded = false;

    public BurstShooterEntity(GameContext context, int x, int y) {
        super("sprites/enemy/Razer_A.gif", x, y);
        this.context = context;
        this.health = new HealthComponent(this, 8);
        dy = moveSpeed;
    }

    public void upgrade() {
        this.isUpgraded = true;
    }

    @Override
    public void collidedWith(Entity other) {
        if (other instanceof ProjectileEntity) {
            ProjectileEntity shot = (ProjectileEntity) other;
            if (shot.getType().targetType == ProjectileType.TargetType.ENEMY) {
                if (health.isAlive()) {
                    if (!health.decreaseHealth(shot.getDamage())) {
                        AnimatedExplosionEntity explosion = new AnimatedExplosionEntity(context, 0, 0);
                        explosion.setScale(0.1);
                        int centeredX = this.getX() + (this.getWidth() / 2) - (explosion.getWidth() / 2);
                        int centeredY = (this.getY() + this.getHeight()) - (explosion.getHeight() / 2);
                        explosion.setX(centeredX);
                        explosion.setY(centeredY);
                        context.addEntity(explosion);
                        context.removeEntity(this);
                        context.getEventBus().publish(new AlienKilledEvent());
                    }
                }
            }
        }
    }

    private void tryToStartBurst() {
        if (firingState != FiringState.IDLE) return;
        if (System.currentTimeMillis() - lastBurstTime < burstInterval) return;

        lastBurstTime = System.currentTimeMillis();
        shotsFiredInBurst = 0;
        firingState = isUpgraded ? FiringState.UPGRADED_BURST : FiringState.NORMAL_BURST;
    }

    @Override
    public void move(long delta) {
        super.move(delta);
        tryToStartBurst();

        long currentTime = System.currentTimeMillis();

        if (firingState == FiringState.NORMAL_BURST) {
            if (currentTime > lastShotInBurstTime + NORMAL_BURST_INTERVAL) {
                lastShotInBurstTime = currentTime;
                shotsFiredInBurst++;

                ProjectileType type = ProjectileType.FAST_NORMAL_SHOT;
                ProjectileEntity shot = new ProjectileEntity(context, type, 1, getX() + (width / 2), getY() + height, 0, type.moveSpeed);
                context.addEntity(shot);

                if (shotsFiredInBurst >= NORMAL_BURST_COUNT) {
                    firingState = FiringState.IDLE;
                }
            }
        } else if (firingState == FiringState.UPGRADED_BURST) {
            if (currentTime > lastShotInBurstTime + UPGRADED_BURST_INTERVAL) {
                lastShotInBurstTime = currentTime;
                shotsFiredInBurst++;

                // 3방향 버스트 발사
                ProjectileType type = ProjectileType.FAST_NORMAL_SHOT;
                int damage = 1;
                double shotMoveSpeed = type.moveSpeed;
                double angle = Math.toRadians(30);

                // 중앙 발사 (직진)
                context.addEntity(new ProjectileEntity(context, type, damage, getX() + (width/2), getY() + height, 0, shotMoveSpeed));
                // 왼쪽 발사 (각도)
                context.addEntity(new ProjectileEntity(context, type, damage, getX() + (width/2), getY() + height, -Math.sin(angle) * shotMoveSpeed, Math.cos(angle) * shotMoveSpeed));
                // 오른쪽 발사 (각도)
                context.addEntity(new ProjectileEntity(context, type, damage, getX() + (width/2), getY() + height, Math.sin(angle) * shotMoveSpeed, Math.cos(angle) * shotMoveSpeed));

                if (shotsFiredInBurst >= UPGRADED_BURST_COUNT) {
                    firingState = FiringState.IDLE;
                }
            }
        }
    }
}
