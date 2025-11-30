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
 * 게임의 가장 기본적인 외계인 적을 나타내는 엔티티입니다.
 * 다양한 이동 패턴을 가지며, 주기적으로 발사체를 발사하고, 플레이어와의 충돌을 처리합니다.
 * 
 * @author Kevin Glass (original)
 */
public class AlienEntity extends Entity implements Enemy {
	/** 외계인의 기본 이동 속도. */
	private final double MOVE_SPEED = 100;
	/** 게임 컨텍스트. */
	private final GameContext context;

	/** 마지막 발사 이후 시간. */
	private long lastFire = 0;
	/** 발사 간격 (밀리초). */
	private static final long FIRING_INTERVAL = 1000;

    /** 이 외계인이 업그레이드되었는지 여부. */
    private boolean isUpgraded = false;

    /** 이 외계인의 이동 패턴. */
    private final MovementPattern movementPattern;
    /** 이 외계인의 초기 x 좌표. */
    private final double initialX;

    // 통합된 엔진 화염 효과 관련 필드
    /** 화염 애니메이션의 프레임 배열. */
    private final Sprite[] fireFrames = new Sprite[3];
    /** 각 화염 프레임의 지속 시간 (밀리초). */
    private final long FIRE_FRAME_DURATION = 100;
    /** 마지막 화염 프레임 변경 이후 경과 시간. */
    private long fireLastFrameChange;
    /** 현재 화염 애니메이션 프레임 번호. */
    private int fireFrameNumber;
    /** 화염 스프라이트의 크기 배율. */
    private final double FIRE_SPRITE_SCALE = 0.8;


	/**
	 * AlienEntity 생성자.
	 * @param context 게임 컨텍스트
	 * @param x 초기 x 좌표
	 * @param y 초기 y 좌표
	 * @param health 초기 체력
	 * @param movementPattern 이 외계인이 사용할 이동 패턴
	 */
	public AlienEntity(GameContext context, int x, int y, int health, MovementPattern movementPattern) {
		super("sprites/enemy/alien.gif", x, y);
		this.health = new HealthComponent(this, health);
		this.context = context;
		this.movementPattern = movementPattern;
		this.initialX = x;
		this.dy = MOVE_SPEED; // 기본 하향 이동

        // 모든 화염 프레임을 미리 로드합니다.
        fireFrames[0] = SpriteStore.get().getSprite("sprites/fire effect/18 Ion.png");
        fireFrames[1] = SpriteStore.get().getSprite("sprites/fire effect/19 Ion.png");
        fireFrames[2] = SpriteStore.get().getSprite("sprites/fire effect/20 Ion.png");
	}

    /**
     * 외계인을 업그레이드 상태로 만듭니다.
     * 업그레이드된 외계인은 다른 종류의 발사체를 발사할 수 있습니다.
     */
    @Override
    public void upgrade() {
        this.isUpgraded = true;
    }

    /**
     * 발사 간격에 따라 발사체를 발사하려고 시도합니다.
     * 업그레이드된 외계인은 다른 발사체를 발사합니다.
     */
	private void tryToFire() {
		if (System.currentTimeMillis() - lastFire < FIRING_INTERVAL) {
			return;
		}

		lastFire = System.currentTimeMillis();
        
        ProjectileType type;
        int damage = 1;

        if (isUpgraded) {
            type = ProjectileType.FOLLOWING_SHOT; // 업그레이드된 발사체
            damage = 1;
            ProjectileEntity shot = new ProjectileEntity(context, type, damage, getX() + (width/2), getY() + height);
            context.addEntity(shot);
        }
        // 기본 발사체 로직은 여기에 추가될 수 있음 (현재는 비어있음)
	}

    /**
     * 외계인의 위치를 업데이트하고, 애니메이션 프레임을 변경하며, 이동 패턴에 따른 로직을 수행합니다.
     * @param delta 마지막 업데이트 이후 경과 시간
     */
    @Override
	public void move(long delta) {
		if (Math.random() < 0.002) { // 낮은 확률로 발사 시도
			tryToFire();
		}

        // 화염 애니메이션 업데이트
        fireLastFrameChange += delta;
        if (fireLastFrameChange > FIRE_FRAME_DURATION) {
            fireLastFrameChange = 0;
            fireFrameNumber = (fireFrameNumber + 1) % fireFrames.length;
        }

        // 이동 패턴 로직
        switch (movementPattern) {
            case STRAIGHT_DOWN:
                dx = 0; // 수평 이동 없음
                break;
            case SINUSOIDAL:
                // Y를 기반으로 X를 계산하여 사인파 형태로 이동.
                // 엔티티는 초기 낙하 경로 주위에서 진동합니다.
                double waveAmplitude = 50;
                double waveFrequency = 0.02;
                double newX = initialX + (Math.sin(y * waveFrequency) * waveAmplitude);
                setX(newX);
                dx = 0; // dx는 이 패턴의 수평 이동에 직접 사용되지 않습니다.
                break;
            case STATIC:
                dx = 0;
                dy = 0;
                break;
            default:
                break;
        }

		super.move(delta);

        // 화면 경계 튕김 로직
        if ((dx < 0 && x < 10) || (dx > 0 && x > 490 - width)) {
            dx = -dx;
        }
        if ((dy < 0 && y < 10) || (dy > 0 && y > 590 - height)) {
            dy = -dy;
        }
	}

    /**
     * 외계인 엔티티와 함께 화염 효과를 그립니다.
     * @param g 그래픽 컨텍스트
     */
    @Override
    public void draw(Graphics g) {
        // 화염 효과를 먼저 그려서 외계인 뒤에 있도록 합니다.
        Sprite fireSprite = fireFrames[fireFrameNumber];
        int fireWidth = (int) (fireSprite.getWidth() * FIRE_SPRITE_SCALE);
        int fireHeight = (int) (fireSprite.getHeight() * FIRE_SPRITE_SCALE);
        double fireX = this.x + (this.width / 2.0) - (fireWidth / 2.0);
        double fireY = this.y - fireHeight + 20; // 위쪽 후방에 위치시킵니다.
        g.drawImage(fireSprite.getImage(), (int) fireX, (int) fireY, fireWidth, fireHeight-30, null);

        // 이제 외계인 자체를 그립니다.
        super.draw(g);
    }

    /**
     * 엔티티가 제거될 때 호출됩니다.
     * 이 클래스에서는 특별한 정리 로직이 필요하지 않습니다.
     */
    @Override
    public void onDestroy() {
        // 특별한 정리가 필요하지 않습니다.
    }

    /**
     * 외계인의 이동 속도를 반환합니다.
     * @return 이동 속도
     */
    public double getMoveSpeed() {
        return MOVE_SPEED;
    }

    /**
     * 다른 엔티티와의 충돌을 처리합니다.
     * 발사체나 레이저 빔과의 충돌 시 체력을 감소시키고, 체력이 0이 되면 파괴됩니다.
     * @param other 충돌한 다른 엔티티
     */
    @Override
    public void collidedWith(Entity other) {
        if (other instanceof ProjectileEntity) {
            handleProjectileCollision((ProjectileEntity) other);
        } else if (other instanceof LaserBeamEntity) {
            handleLaserBeamCollision((LaserBeamEntity) other);
        }
    }

    /** 발사체와의 충돌을 처리합니다. */
    private void handleProjectileCollision(ProjectileEntity shot) {
        if (shot.getType().targetType != ProjectileType.TargetType.ENEMY || !health.isAlive()) {
            return;
        }

        if (!health.decreaseHealth(shot.getDamage())) {
            this.destroy();
        }
    }

    /** 레이저 빔과의 충돌을 처리합니다. */
    private void handleLaserBeamCollision(LaserBeamEntity laser) {
        if (!health.isAlive()) {
            return;
        }

        if (!health.decreaseHealth(laser.getDamage())) {
            createExplosion();
            this.destroy();
        }
    }

    /** 외계인이 파괴될 때 애니메이션 폭발 효과를 생성합니다. */
    private void createExplosion() {
        AnimatedExplosionEntity explosion = new AnimatedExplosionEntity(context, 0, 0);
        explosion.setScale(0.1);
        int centeredX = this.getX() + (this.getWidth() / 2) - (explosion.getWidth() / 2);
        int centeredY = (this.getY() + this.getHeight()) - (explosion.getHeight() / 2);
        explosion.setX(centeredX);
        explosion.setY(centeredY);
        context.addEntity(explosion);
    }
}