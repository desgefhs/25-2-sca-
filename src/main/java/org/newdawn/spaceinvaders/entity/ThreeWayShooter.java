package org.newdawn.spaceinvaders.entity;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.graphics.Sprite;
import org.newdawn.spaceinvaders.graphics.SpriteStore;

import java.awt.Graphics;

public class ThreeWayShooter extends Entity {
    private double moveSpeed = 100; // Movement speed of the shooter itself
    private GameContext context;

    private long lastFire = 0;
    private long firingInterval = 2000; // Fires every 2 seconds

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

    public ThreeWayShooter(GameContext context, int x, int y) {
        super("sprites/enemy/ThreeWayShooter.gif", x, y);
        this.context = context;
        this.health = new HealthComponent(this,5); // Example health
        dy = moveSpeed;

        // Pre-load all fire frames
        fireFrames[0] = SpriteStore.get().getSprite("sprites/fire effect/18 Ion.png");
        fireFrames[1] = SpriteStore.get().getSprite("sprites/fire effect/19 Ion.png");
        fireFrames[2] = SpriteStore.get().getSprite("sprites/fire effect/20 Ion.png");
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

        // if we have gone off the bottom of the screen, remove ourselves
        if (y > 600) {
            context.removeEntity(this);
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

        // Now draw the entity itself
        super.draw(g);
    }

    @Override
    public void onDestroy() {
        // No special cleanup needed for the integrated fire effect
    }

    @Override
    public void collidedWith(Entity other) {
        // if it's a shot from the player, take damage
        if (other instanceof ProjectileEntity) {
            ProjectileEntity shot = (ProjectileEntity) other;
            if (shot.getType().targetType == ProjectileType.TargetType.ENEMY) {
                if (health.isAlive()) {
                    if (!health.decreaseHealth(shot.getDamage())) {
                        // Create, scale, and position the explosion to be centered on the shooter
                        AnimatedExplosionEntity explosion = new AnimatedExplosionEntity(context, 0, 0);
                        explosion.setScale(0.1);
                        int centeredX = this.getX() + (this.getWidth() / 2) - (explosion.getWidth() / 2);
                        int centeredY = (this.getY() + this.getHeight()) - (explosion.getHeight() / 2);
                        explosion.setX(centeredX);
                        explosion.setY(centeredY);
                        context.addEntity(explosion);

                        // Remove the shooter from the game
                        context.removeEntity(this);
                    }
                }
            }
        }
    }
}
