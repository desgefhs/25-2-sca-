package org.newdawn.spaceinvaders.entity.Enemy;


import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.*;
import org.newdawn.spaceinvaders.entity.Effect.AnimatedExplosionEntity;
import org.newdawn.spaceinvaders.entity.Projectile.LaserBeamEntity;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileEntity;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileType;
import org.newdawn.spaceinvaders.graphics.Sprite;
import org.newdawn.spaceinvaders.graphics.SpriteStore;

import java.awt.Graphics;

/**
 * 기본적인 적을 나타내는 엔티티
 */
public class AlienEntity extends Entity implements Enemy {
    private GameContext context;
    /** 이동 속도 */
	private double moveSpeed = 100;
	/** 기본 최대 체력 */
	private static final int MAX_HEALTH = 2;
	/** 마지막 발사 시간 */
	private long lastFire = 0;
	/** 발사 간격 */
	private static final long firingInterval = 1000;

    /** 강화 상태 여부 */
    private boolean isUpgraded = false;

    /** 이동 패턴 */
    private final MovementPattern movementPattern;
    /** 초기 x 좌표 (사인 곡선 이동에 사용) */
    private final double initialX;

    // 엔진 불꽃 효과
    /** 불꽃 애니메이션 프레임 */
    private final Sprite[] fireFrames = new Sprite[3];
    /** 불꽃 프레임 지속 시간 */
    private final long fireFrameDuration = 100; // ms
    /** 마지막 불꽃 프레임 변경 시간 */
    private long fireLastFrameChange;
    private int fireFrameNumber;
    /** 불꽃 스프라이트 크기 배율 */
    private final double fireSpriteScale = 0.8;


	/**
	 * AlienEntity 객체를 생성
	 *
	 * @param context         게임 컨텍스트
	 * @param x               x 좌표
	 * @param y               y 좌표
	 * @param health          체력
	 * @param movementPattern 이동 패턴
	 */
	public AlienEntity(GameContext context, int x, int y, int health, MovementPattern movementPattern) {
		super("sprites/enemy/alien.gif", x, y);
		this.health = new HealthComponent(this, health);
		this.context = context;
		this.movementPattern = movementPattern;
		this.initialX = x;
		this.dy = moveSpeed; // 기본적으로 아래로 이동

        // 불꽃 프레임 미리 로드
        fireFrames[0] = SpriteStore.get().getSprite("sprites/fire effect/18 Ion.png");
        fireFrames[1] = SpriteStore.get().getSprite("sprites/fire effect/19 Ion.png");
        fireFrames[2] = SpriteStore.get().getSprite("sprites/fire effect/20 Ion.png");
	}

	// 생성자 오버로딩
	public AlienEntity(GameContext context, int x, int y) { this(context, x, y, MAX_HEALTH, MovementPattern.STRAIGHT_DOWN); }

    /**
     * 적을 강화 상태로
     */
    public void upgrade() {
        this.isUpgraded = true;
    }

	/**
	 * 발사를 시도 강화 상태일 경우 유도탄을 발사
	 */
	private void tryToFire() {
		if (System.currentTimeMillis() - lastFire < firingInterval) {
			return;
		}

		lastFire = System.currentTimeMillis();
        
        if (isUpgraded) {
            ProjectileType type = ProjectileType.FOLLOWING_SHOT; // 강화된 유도탄
            ProjectileEntity shot = new ProjectileEntity(context, type, 1, getX() + (width/2), getY() + height);
            context.addEntity(shot);
        }
	}

	public void move(long delta) {
		// 낮은 확률로 발사 시도
		if (Math.random() < 0.002) {
			tryToFire();
		}

        // 불꽃 애니메이션 업데이트
        fireLastFrameChange += delta;
        if (fireLastFrameChange > fireFrameDuration) {
            fireLastFrameChange = 0;
            fireFrameNumber = (fireFrameNumber + 1) % fireFrames.length;
        }

        // 이동 패턴에 따른 로직 처리
        switch (movementPattern) {
            case STRAIGHT_DOWN:
                dx = 0;
                break;
            case SINUSOIDAL:
                // y 위치에 따라 x 좌표를 계산하여 부드러운 웨이브 움직임 생성
                double waveAmplitude = 50;
                double waveFrequency = 0.02;
                double newX = initialX + (Math.sin(y * waveFrequency) * waveAmplitude);
                setX(newX);
                dx = 0; // 이 패턴에서는 dx를 직접 사용하지 않음
                break;
            case STATIC:
                dx = 0;
                dy = 0;
                break;
            default:
                break;
        }

		super.move(delta);

        // 화면 경계에 닿으면 튕기는 로직
        if ((dx < 0 && x < 10) || (dx > 0 && x > 490 - width)) {
            dx = -dx;
        }
        if ((dy < 0 && y < 10) || (dy > 0 && y > 590 - height)) {
            dy = -dy;
        }
	}

    @Override
    public void draw(Graphics g) {
        // 에일리언 뒤에 불꽃 효과를 먼저 그림
        Sprite fireSprite = fireFrames[fireFrameNumber];
        int fireWidth = (int) (fireSprite.getWidth() * fireSpriteScale);
        int fireHeight = (int) (fireSprite.getHeight() * fireSpriteScale);
        double fireX = this.x + (this.width / 2.0) - (fireWidth / 2.0);
        double fireY = this.y - fireHeight + 20; // 위쪽 후방에 위치
        g.drawImage(fireSprite.getImage(), (int) fireX, (int) fireY, fireWidth-30, fireHeight-30, null);

        // 그 다음 에일리언 본체를 그림
        super.draw(g);
    }

    @Override
    public void onDestroy() {
        // 특별한 정리 작업 필요 없음
    }

    public double getMoveSpeed() {
        return moveSpeed;
    }

    @Override
    public void collidedWith(Entity other) {
        // 플레이어의 발사체와 충돌
        if (other instanceof ProjectileEntity) {
            ProjectileEntity shot = (ProjectileEntity) other;
            if (shot.getType().targetType == ProjectileType.TargetType.ENEMY) {
                if (health.isAlive()) {
                    if (!health.decreaseHealth(shot.getDamage())) {
                        // 사망 시 자신을 제거하고, 처치 알림
                        context.removeEntity(this);
                        context.notifyAlienKilled();
                    }
                }
            }
        } 
        // 플레이어의 레이저 빔과 충돌
        else if (other instanceof LaserBeamEntity) {
            LaserBeamEntity laser = (LaserBeamEntity) other;
            if (health.isAlive()) {
                if (!health.decreaseHealth(laser.getDamage())) {
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
