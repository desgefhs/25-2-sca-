package org.newdawn.spaceinvaders.entity.Enemy;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;

import org.newdawn.spaceinvaders.entity.*;
import org.newdawn.spaceinvaders.entity.Effect.AnimatedExplosionEntity;
import org.newdawn.spaceinvaders.entity.Projectile.LaserBeamEntity;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileEntity;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileType;
import org.newdawn.spaceinvaders.graphics.Sprite;
import org.newdawn.spaceinvaders.graphics.SpriteStore;

import java.awt.Graphics;

public class ThreeWayShooter extends Entity implements Enemy {
    private final double moveSpeed = 150; // Adjusted for horizontal movement
    private final GameContext context;
    private MovementPattern movementPattern;

    private long lastFire = 0;
    private final long firingInterval = 2000; // Fires every 2 seconds

    // Upgrade state
    private boolean isUpgraded = false;
    private boolean specialShotPending = false;
    private long normalShotTime = 0;
    private static final long SPECIAL_SHOT_DELAY = 500; // 0.5 seconds

    // Integrated engine fire effect
    private final Sprite[] fireFrames = new Sprite[3];
    private final long fireFrameDuration = 100; // ms
    private long fireLastFrameChange;
    private int fireFrameNumber;
    private final double fireSpriteScale = 0.8;

    public ThreeWayShooter(GameContext context, int x, int y, MovementPattern pattern) {
        super("sprites/enemy/ThreeWayShooter.gif", x, y);
        this.context = context;
        this.health = new HealthComponent(this, 5); // Example health
        this.movementPattern = pattern;

        // Set initial velocity based on pattern
        if (pattern == MovementPattern.HORIZ_TO_CENTER_AND_STOP) {
            this.dy = 0;
            if (x < Game.GAME_WIDTH / 2) {
                this.dx = moveSpeed; // Move right
            } else {
                this.dx = -moveSpeed; // Move left
            }
        } else {
            // Default behavior
            this.movementPattern = MovementPattern.STRAIGHT_DOWN;
            this.dy = 100;
        }

        // Pre-load all fire frames
        fireFrames[0] = SpriteStore.get().getSprite("sprites/fire effect/18 Ion.png");
        fireFrames[1] = SpriteStore.get().getSprite("sprites/fire effect/19 Ion.png");
        fireFrames[2] = SpriteStore.get().getSprite("sprites/fire effect/20 Ion.png");
    }

    public ThreeWayShooter(GameContext context, int x, int y) {
        this(context, x, y, MovementPattern.STRAIGHT_DOWN);
    }

    public void upgrade() {
        this.isUpgraded = true;
    }

    private void tryToFire() {
        if (System.currentTimeMillis() - lastFire < firingInterval) {
            return;
        }
        lastFire = System.currentTimeMillis();

        ProjectileType type = ProjectileType.NORMAL_SHOT;
        int damage = 1;
        double shotMoveSpeed = type.moveSpeed;
        double angle = Math.toRadians(30);

        // Center shot (0 degrees)
        context.addEntity(new ProjectileEntity(context, type, damage, getX() + (width/2), getY() + height, 0, shotMoveSpeed));

        // Left shot (-30 degrees)
        double dxLeft = -Math.sin(angle) * shotMoveSpeed;
        double dyLeft = Math.cos(angle) * shotMoveSpeed;
        context.addEntity(new ProjectileEntity(context, type, damage, getX() + (width/2), getY() + height, dxLeft, dyLeft));

        // Right shot (+30 degrees)
        double dxRight = Math.sin(angle) * shotMoveSpeed;
        double dyRight = Math.cos(angle) * shotMoveSpeed;
        context.addEntity(new ProjectileEntity(context, type, damage, getX() + (width/2), getY() + height, dxRight, dyRight));

        if (isUpgraded) {
            specialShotPending = true;
            normalShotTime = System.currentTimeMillis();
        }
    }

    @Override
    public void move(long delta) {
        if (movementPattern == MovementPattern.HORIZ_TO_CENTER_AND_STOP) {
            // If moving and reached center, stop
            if (dx != 0) {
                float centerX = Game.GAME_WIDTH / 2.0f;
                // Check if we are close to the center
                if (Math.abs(x + (width/2) - centerX) < 10) {
                    if (dx > 0) { // Was moving right
                        x = centerX - width; // Stop on the left side of center
                    } else { // Was moving left
                        x = centerX; // Stop on the right side of center
                    }
                    dx = 0;
                    this.movementPattern = MovementPattern.STATIC; // Become static
                }
            }
        }

        super.move(delta);

        // Update fire animation
        fireLastFrameChange += delta;
        if (fireLastFrameChange > fireFrameDuration) {
            fireLastFrameChange = 0;
            fireFrameNumber = (fireFrameNumber + 1) % fireFrames.length;
        }

        tryToFire();

        // Handle delayed special shot if pending
        if (specialShotPending && System.currentTimeMillis() > normalShotTime + SPECIAL_SHOT_DELAY) {
            ProjectileType specialType = ProjectileType.FAST_FOLLOWING_SHOT;
            int specialDamage = 2; // Or whatever damage is appropriate
            context.addEntity(new ProjectileEntity(context, specialType, specialDamage, getX() + (width/2), getY() + height));
            specialShotPending = false; // Reset the flag
        }

        // if we have gone off the bottom of the screen, destroy self
        if (y > 600) {
            this.destroy();
        }
    }

    @Override
    public void draw(Graphics g) {
        // Draw the fire effect first, so it's behind the entity
        Sprite fireSprite = fireFrames[fireFrameNumber];
        int fireWidth = (int) (fireSprite.getWidth() * fireSpriteScale);
        int fireHeight = (int) (fireSprite.getHeight() * fireSpriteScale);
        double fireX = this.x + (this.width / 2.0) - (fireWidth / 2.0);
        double fireY = this.y - fireHeight + 20; // Position it at the top-rear
        g.drawImage(fireSprite.getImage(), (int) fireX, (int) fireY, fireWidth, fireHeight, null);

        // Now draw the entity itself using the parent class's rotation logic
        super.draw(g);
    }

    @Override
    public void onDestroy() {
        // No special cleanup needed for the integrated fire effect
    }

    @Override
    public void collidedWith(Entity other) {
        if (other instanceof ProjectileEntity) {
            ProjectileEntity shot = (ProjectileEntity) other;
            if (shot.getType().targetType == ProjectileType.TargetType.ENEMY) {
                handleDamage(shot.getDamage());
            }
        } else if (other instanceof LaserBeamEntity) {
            LaserBeamEntity laser = (LaserBeamEntity) other;
            handleDamage(laser.getDamage());
        }
    }

    private void handleDamage(int damage) {
        if (health.isAlive()) {
            if (!health.decreaseHealth(damage)) {
                // Create, scale, and position the explosion to be centered on the shooter
                AnimatedExplosionEntity explosion = new AnimatedExplosionEntity(context, 0, 0);
                explosion.setScale(0.1);
                int centeredX = this.getX() + (this.getWidth() / 2) - (explosion.getWidth() / 2);
                int centeredY = (this.getY() + this.getHeight()) - (explosion.getHeight() / 2);
                explosion.setX(centeredX);
                explosion.setY(centeredY);
                context.addEntity(explosion);

                this.destroy();
            }
        }
    }
}
