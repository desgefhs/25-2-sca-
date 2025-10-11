package org.newdawn.spaceinvaders.entity.Enemy;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.*;
import org.newdawn.spaceinvaders.entity.Effect.AnimatedExplosionEntity;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileEntity;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileType;

/**
 * 짧은 간격으로 여러 발의 총알을 연사(burst)하는 적 엔티티
 * 강화 시 3-Way 연사를 합니다.
 */
public class BurstShooterEntity extends Entity implements Enemy {

    private GameContext context;
    /** 이동 속도 */
    private double moveSpeed = 75;
    /** 마지막 연사 시작 시간 */
    private long lastBurstTime = 0;
    /** 연사 사이의 간격 */
    private long burstInterval = 2000;

    /** 발사 상태 (대기, 일반 연사, 강화 연사) */
    private enum FiringState {IDLE, NORMAL_BURST, UPGRADED_BURST}
    private FiringState firingState = FiringState.IDLE;
    /** 현재 연사에서 발사된 총알 수 */
    private int shotsFiredInBurst = 0;
    /** 현재 연사 중 마지막 발사 시간 */
    private long lastShotInBurstTime = 0;

    // 일반 연사 설정
    /** 일반 연사 시 총알 수 */
    private static final int NORMAL_BURST_COUNT = 5;
    /** 일반 연사 시 총알 발사 간격 */
    private static final long NORMAL_BURST_INTERVAL = 400;

    // 강화 연사 설정
    /** 강화 연사 시 총알 수 (3발씩 6회) */
    private static final int UPGRADED_BURST_COUNT = 6;
    /** 강화 연사 시 총알 발사 간격 */
    private static final long UPGRADED_BURST_INTERVAL = 250;

    /** 강화 상태 여부 */
    private boolean isUpgraded = false;

    /**
     * BurstShooterEntity 객체를 생성
     *
     * @param context 게임 컨텍스트
     * @param x       x 좌표
     * @param y       y 좌표
     */
    public BurstShooterEntity(GameContext context, int x, int y) {
        super("sprites/enemy/Razer_A.gif", x, y);
        this.context = context;
        this.health = new HealthComponent(this, 8);
        dy = moveSpeed;
    }

    /**
     * 엔티티를 강화 상태로 만듬
     */
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
                        // 사망 시 폭발 효과 생성 후 자신을 제거하고, 처치 알림
                        AnimatedExplosionEntity explosion = new AnimatedExplosionEntity(context, 0, 0);
                        explosion.setScale(0.1);
                        int centeredX = this.getX() + (this.getWidth() / 2) - (explosion.getWidth() / 2);
                        int centeredY = (this.getY() + this.getHeight()) - (explosion.getHeight() / 2);
                        explosion.setX(centeredX);
                        explosion.setY(centeredY);
                        context.addEntity(explosion);
                        context.removeEntity(this);
                        context.notifyAlienKilled();
                    }
                }
            }
        }
    }

    /**
     * 연사 시작을 시도
     * 현재 대기 상태이고, 마지막 연사 이후 충분한 시간이 지났을 때 새로운 연사를 시작
     */
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

        // 일반 연사 상태 처리
        if (firingState == FiringState.NORMAL_BURST) {
            if (currentTime > lastShotInBurstTime + NORMAL_BURST_INTERVAL) {
                lastShotInBurstTime = currentTime;
                shotsFiredInBurst++;

                ProjectileType type = ProjectileType.FAST_NORMAL_SHOT;
                ProjectileEntity shot = new ProjectileEntity(context, type, 1, getX() + (width / 2), getY() + height, 0, type.moveSpeed);
                context.addEntity(shot);

                if (shotsFiredInBurst >= NORMAL_BURST_COUNT) {
                    firingState = FiringState.IDLE; // 연사 완료 후 대기 상태로 전환
                }
            }
        } 
        // 강화 연사 상태 처리
        else if (firingState == FiringState.UPGRADED_BURST) {
            if (currentTime > lastShotInBurstTime + UPGRADED_BURST_INTERVAL) {
                lastShotInBurstTime = currentTime;
                shotsFiredInBurst++;

                // 3-Way 연사
                ProjectileType type = ProjectileType.FAST_NORMAL_SHOT;
                double angle = Math.toRadians(30);

                // 중앙, 왼쪽, 오른쪽으로 3발 동시 발사
                context.addEntity(new ProjectileEntity(context, type, 1, getX() + (width/2), getY() + height, 0, type.moveSpeed));
                context.addEntity(new ProjectileEntity(context, type, 1, getX() + (width/2), getY() + height, -Math.sin(angle) * type.moveSpeed, Math.cos(angle) * type.moveSpeed));
                context.addEntity(new ProjectileEntity(context, type, 1, getX() + (width/2), getY() + height, Math.sin(angle) * type.moveSpeed, Math.cos(angle) * type.moveSpeed));

                if (shotsFiredInBurst >= UPGRADED_BURST_COUNT) {
                    firingState = FiringState.IDLE; // 연사 완료 후 대기 상태로 전환
                }
            }
        }
    }
}