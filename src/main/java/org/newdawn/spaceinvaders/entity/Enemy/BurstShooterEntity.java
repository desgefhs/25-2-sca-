package org.newdawn.spaceinvaders.entity.Enemy;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.events.AlienKilledEvent;
import org.newdawn.spaceinvaders.entity.*;
import org.newdawn.spaceinvaders.entity.Effect.AnimatedExplosionEntity;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileEntity;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileType;

/**
 * 발사체를 연사(버스트)하는 적 엔티티.
 * 업그레이드 여부에 따라 발사 패턴이 달라집니다.
 */
public class BurstShooterEntity extends Entity implements Enemy {
    /** 기본 이동 속도. */
    private final double moveSpeed = 75;
    /** 게임 컨텍스트. */
    private final GameContext context;

    /** 마지막 버스트 공격 시간. */
    private long lastBurstTime = 0;
    /** 버스트 공격 간 시간 간격 (밀리초). */
    private final long burstInterval = 2000;

    /**
     * 발사 상태를 정의하는 열거형.
     */
    private enum FiringState {IDLE, NORMAL_BURST, UPGRADED_BURST}

    private FiringState firingState = FiringState.IDLE;
    private int shotsFiredInBurst = 0;
    private long lastShotInBurstTime = 0;

    // 일반 버스트 관련 상수
    private static final int NORMAL_BURST_COUNT = 5;
    private static final long NORMAL_BURST_INTERVAL = 400;

    // 업그레이드된 버스트 관련 상수
    private static final int UPGRADED_BURST_COUNT = 6;
    private static final long UPGRADED_BURST_INTERVAL = 250;

    /** 이 슈터가 업그레이드되었는지 여부. */
    private boolean isUpgraded = false;

    /**
     * BurstShooterEntity 생성자.
     * @param context 게임 컨텍스트
     * @param x 초기 x 좌표
     * @param y 초기 y 좌표
     */
    public BurstShooterEntity(GameContext context, int x, int y) {
        super("sprites/enemy/Razer_A.gif", x, y);
        this.context = context;
        this.health = new HealthComponent(this, 8); // 초기 체력
        dy = moveSpeed;
    }

    /**
     * 이 엔티티를 업그레이드 상태로 만듭니다.
     */
    @Override
    public void upgrade() {
        this.isUpgraded = true;
    }

    /**
     * 다른 엔티티와의 충돌을 처리합니다.
     * 발사체와 충돌 시 체력을 감소시키고, 체력이 0이 되면 파괴됩니다.
     * @param other 충돌한 다른 엔티티
     */
    @Override
    public void collidedWith(Entity other) {
        if (other instanceof ProjectileEntity) {
            ProjectileEntity shot = (ProjectileEntity) other;
            // 적을 대상으로 하는 발사체에만 반응
            if (shot.getType().targetType == ProjectileType.TargetType.ENEMY) {
                if (health.isAlive()) {
                    if (!health.decreaseHealth(shot.getDamage())) {
                        // 파괴 시 폭발 애니메이션 생성
                        AnimatedExplosionEntity explosion = new AnimatedExplosionEntity(context, 0, 0);
                        explosion.setScale(0.1);
                        int centeredX = this.getX() + (this.getWidth() / 2) - (explosion.getWidth() / 2);
                        int centeredY = (this.getY() + this.getHeight()) - (explosion.getHeight() / 2);
                        explosion.setX(centeredX);
                        explosion.setY(centeredY);
                        context.addEntity(explosion);
                        this.destroy(); // 자신을 파괴
                        context.getEventBus().publish(new AlienKilledEvent()); // 외계인 처치 이벤트 발행
                    }
                }
            }
        }
    }

    /**
     * 새로운 버스트 공격을 시작할지 시도합니다.
     * `burstInterval`에 따라 주기적으로 호출됩니다.
     */
    private void tryToStartBurst() {
        if (firingState != FiringState.IDLE) return; // 이미 발사 중이면 시작하지 않음
        if (System.currentTimeMillis() - lastBurstTime < burstInterval) return; // 간격이 아직 안 지났으면 시작하지 않음

        lastBurstTime = System.currentTimeMillis();
        shotsFiredInBurst = 0;
        firingState = isUpgraded ? FiringState.UPGRADED_BURST : FiringState.NORMAL_BURST;
    }

    /**
     * 엔티티를 이동시키고, 현재 발사 상태에 따라 버스트 공격을 진행합니다.
     * @param delta 마지막 업데이트 이후 경과 시간
     */
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
                    firingState = FiringState.IDLE; // 버스트 완료
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
                    firingState = FiringState.IDLE; // 버스트 완료
                }
            }
        }
    }
}
