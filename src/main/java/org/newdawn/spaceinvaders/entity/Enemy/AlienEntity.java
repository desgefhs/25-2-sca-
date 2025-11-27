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
 * 우주 침략자 외계인 중 하나를 나타내는 엔티티입니다.
 * 
 * @author Kevin Glass
 */
public class AlienEntity extends Entity implements Enemy {
	private final double MOVE_SPEED = 100;
	private final GameContext context;

	private long lastFire = 0;
	private static final long FIRING_INTERVAL = 1000;

    private boolean isUpgraded = false;

    private final MovementPattern movementPattern;
    private final double initialX;

    // 통합된 엔진 화염 효과
    private final Sprite[] fireFrames = new Sprite[3];
    private final long FIRE_FRAME_DURATION = 100; // ms
    private long fireLastFrameChange;
    private int fireFrameNumber;
    private final double FIRE_SPRITE_SCALE = 0.8;


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


    public void upgrade() {
        this.isUpgraded = true;
    }

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


	}

	public void move(long delta) {
		if (Math.random() < 0.002) {
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
                dx = 0;
                break;
            case SINUSOIDAL:
                // 이 패턴은 이제 부드러운 파도를 위해 Y를 기반으로 X를 직접 계산합니다.
                // 엔티티는 초기 낙하 경로 주위에서 진동합니다.
                double waveAmplitude = 50;
                double waveFrequency = 0.02;
                // 원하는 X를 계산하고 super.move()가 Y 이동을 처리하도록 합니다.
                double newX = initialX + (Math.sin(y * waveFrequency) * waveAmplitude);
                setX((int)newX);
                dx = 0; // dx는 이 패턴의 수평 이동에 사용되지 않습니다.
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

    @Override
    public void onDestroy() {
        // 통합된 화염 효과에 대한 특별한 정리가 필요하지 않습니다.
    }

    public double getMoveSpeed() {
        return MOVE_SPEED;
    }

    public void collidedWith(Entity other) {
        if (other instanceof ProjectileEntity) {
            handleProjectileCollision((ProjectileEntity) other);
        } else if (other instanceof LaserBeamEntity) {
            handleLaserBeamCollision((LaserBeamEntity) other);
        }
    }

    private void handleProjectileCollision(ProjectileEntity shot) {
        if (shot.getType().targetType != ProjectileType.TargetType.ENEMY || !health.isAlive()) {
            return;
        }

        if (!health.decreaseHealth(shot.getDamage())) {
            this.destroy();
        }
    }

    private void handleLaserBeamCollision(LaserBeamEntity laser) {
        if (!health.isAlive()) {
            return;
        }

        if (!health.decreaseHealth(laser.getDamage())) {
            createExplosion();
            this.destroy();
        }
    }

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