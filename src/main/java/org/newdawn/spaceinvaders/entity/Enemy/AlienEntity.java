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
 * An entity which represents one of our space invader aliens.
 * 
 * @author Kevin Glass
 */
public class AlienEntity extends Entity implements Enemy {
	private double moveSpeed = 100;
	private GameContext context;
	private static final int MAX_HEALTH = 2;
	private static final int SHOT_DAMAGE = 1;

	private long lastFire = 0;
	private static final long firingInterval = 1000;

    private boolean isUpgraded = false;

    private final MovementPattern movementPattern;
    private final double initialX;

    // Integrated engine fire effect
    private final Sprite[] fireFrames = new Sprite[3];
    private final long fireFrameDuration = 100; // ms
    private long fireLastFrameChange;
    private int fireFrameNumber;
    private final double fireSpriteScale = 0.8;


	public AlienEntity(GameContext context, int x, int y, int health, MovementPattern movementPattern) {
		super("sprites/enemy/alien.gif", x, y);
		this.health = new HealthComponent(this, health);
		this.context = context;
		this.movementPattern = movementPattern;
		this.initialX = x;
		this.dy = moveSpeed; // Default downward movement

        // Pre-load all fire frames
        fireFrames[0] = SpriteStore.get().getSprite("sprites/fire effect/18 Ion.png");
        fireFrames[1] = SpriteStore.get().getSprite("sprites/fire effect/19 Ion.png");
        fireFrames[2] = SpriteStore.get().getSprite("sprites/fire effect/20 Ion.png");
	}

	public AlienEntity(GameContext context, int x, int y, int health, int cycle) {
		this(context, x, y, health, MovementPattern.STRAIGHT_DOWN);
	}

	public AlienEntity(GameContext context, int x, int y, int health) {
		this(context, x, y, health, MovementPattern.STRAIGHT_DOWN);
	}

	public AlienEntity(GameContext context, int x, int y) {
		this(context, x, y, MAX_HEALTH, MovementPattern.STRAIGHT_DOWN);
	}

    public void upgrade() {
        this.isUpgraded = true;
    }

	private void tryToFire() {
		if (System.currentTimeMillis() - lastFire < firingInterval) {
			return;
		}

		lastFire = System.currentTimeMillis();
        
        ProjectileType type;
        int damage = 1;

        if (isUpgraded) {
            type = ProjectileType.FOLLOWING_SHOT; // Upgraded shot
            damage = 1;
            ProjectileEntity shot = new ProjectileEntity(context, type, damage, getX() + (width/2), getY() + height);
            context.addEntity(shot);
        }


	}

	public void move(long delta) {
		if (Math.random() < 0.002) {
			tryToFire();
		}

        // Update fire animation
        fireLastFrameChange += delta;
        if (fireLastFrameChange > fireFrameDuration) {
            fireLastFrameChange = 0;
            fireFrameNumber = (fireFrameNumber + 1) % fireFrames.length;
        }

        // Movement pattern logic
        switch (movementPattern) {
            case STRAIGHT_DOWN:
                dx = 0;
                break;
            case SINUSOIDAL:
                // This pattern now calculates X directly based on Y for a smooth wave.
                // The entity will oscillate around its initial drop path.
                double waveAmplitude = 50;
                double waveFrequency = 0.02;
                // We calculate the desired X and let super.move() handle the Y movement.
                double newX = initialX + (Math.sin(y * waveFrequency) * waveAmplitude);
                setX((int)newX);
                dx = 0; // dx is not used for this pattern's horizontal movement
                break;
            case STATIC:
                dx = 0;
                dy = 0;
                break;
            default:
                break;
        }

		super.move(delta);

        // Screen boundary bouncing logic
        if ((dx < 0 && x < 10) || (dx > 0 && x > 490 - width)) {
            dx = -dx;
        }
        if ((dy < 0 && y < 10) || (dy > 0 && y > 590 - height)) {
            dy = -dy;
        }
	}

    @Override
    public void draw(Graphics g) {
        // Draw the fire effect first, so it's behind the alien
        Sprite fireSprite = fireFrames[fireFrameNumber];
        int fireWidth = (int) (fireSprite.getWidth() * fireSpriteScale);
        int fireHeight = (int) (fireSprite.getHeight() * fireSpriteScale);
        double fireX = this.x + (this.width / 2.0) - (fireWidth / 2.0);
        double fireY = this.y - fireHeight + 20; // Position it at the top-rear
        g.drawImage(fireSprite.getImage(), (int) fireX, (int) fireY, fireWidth, fireHeight-30, null);

        // Now draw the alien itself
        super.draw(g);
    }

    @Override
    public void onDestroy() {
        // No special cleanup needed for the integrated fire effect
    }

    public double getMoveSpeed() {
        return moveSpeed;
    }

    public void collidedWith(Entity other) {
        if (other instanceof ProjectileEntity) {
            ProjectileEntity shot = (ProjectileEntity) other;
            if (shot.getType().targetType == ProjectileType.TargetType.ENEMY) {
                if (health.isAlive()) {
                    if (!health.decreaseHealth(shot.getDamage())) {
                        context.removeEntity(this);
                        context.notifyAlienKilled();
                    }
                }
            }
        } else if (other instanceof LaserBeamEntity) {
            LaserBeamEntity laser = (LaserBeamEntity) other;
            if (health.isAlive()) {
                if (!health.decreaseHealth(laser.getDamage())) {
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
