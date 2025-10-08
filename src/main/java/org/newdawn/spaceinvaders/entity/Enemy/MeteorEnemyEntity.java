package org.newdawn.spaceinvaders.entity.Enemy;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.*;
import org.newdawn.spaceinvaders.entity.Effect.AnimatedExplosionEntity;
import org.newdawn.spaceinvaders.entity.Projectile.LaserBeamEntity;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileEntity;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileType;

public class MeteorEnemyEntity extends Entity {
    private static final long FIRING_INTERVAL = 400L; // 0.4 seconds
    private long lastFire = 0;
    private GameContext context;
    private double moveSpeed = 75;

    private enum FiringState { FIRING, COOLDOWN }
    private FiringState currentState = FiringState.FIRING;
    private long stateTimer = 2000L; // Start in FIRING state
    private static final long FIRING_DURATION = 2000L;
    private static final long COOLDOWN_DURATION = 1000L;

    public MeteorEnemyEntity(GameContext context, int x, int y) {
        super("sprites/enemy/meteorEnemy.gif", x, y);
        this.context = context;
        this.health = new HealthComponent(this, 3); // Set initial health to 3
        this.dy = moveSpeed; // Move downwards
        setScale(1.5);
    }

    private void tryToFire() {
        if (System.currentTimeMillis() - lastFire < FIRING_INTERVAL) {
            return;
        }
        lastFire = System.currentTimeMillis();

        ProjectileType type = ProjectileType.FAST_NORMAL_SHOT;
        ProjectileEntity shot = new ProjectileEntity(context, type, 1, getX() + (width / 2) - 15, getY() + height, 0, type.moveSpeed);
        context.addEntity(shot);
    }

    @Override
    public void move(long delta) {
        super.move(delta);

        // Update firing state
        stateTimer -= delta;
        if (stateTimer <= 0) {
            if (currentState == FiringState.FIRING) {
                currentState = FiringState.COOLDOWN;
                stateTimer = COOLDOWN_DURATION;
            } else {
                currentState = FiringState.FIRING;
                stateTimer = FIRING_DURATION;
            }
        }

        // Try to fire only when in the FIRING state
        if (currentState == FiringState.FIRING) {
            tryToFire();
        }

        // If it goes off the bottom of the screen, remove it
        if (y > 600) {
            context.removeEntity(this);
            context.notifyAlienEscaped(this);
        }
    }

    @Override
    public void collidedWith(Entity other) {
        // If it collides with a player's shot, take damage
        if (other instanceof ProjectileEntity) {
            ProjectileEntity shot = (ProjectileEntity) other;
            if (shot.getType().targetType == ProjectileType.TargetType.ENEMY) {
                if (health.isAlive()) {
                    // Decrease health and check if it's destroyed
                    if (!health.decreaseHealth(shot.getDamage())) {
                        // Create an explosion on death
                        AnimatedExplosionEntity explosion = new AnimatedExplosionEntity(context, 0, 0);
                        explosion.setScale(0.1);
                        int centeredX = this.getX() + (this.getWidth() / 2) - (explosion.getWidth() / 2);
                        int centeredY = (this.getY() + this.getHeight() / 2) - (explosion.getHeight() / 2);
                        explosion.setX(centeredX);
                        explosion.setY(centeredY);
                        context.addEntity(explosion);

                        // Remove this entity and notify that an alien was killed
                        context.removeEntity(this);
                        context.notifyAlienKilled();
                    }
                }
                // Remove the player's projectile upon impact
                context.removeEntity(shot);
            }
        } else if (other instanceof LaserBeamEntity) {
            LaserBeamEntity laser = (LaserBeamEntity) other;
            if (health.isAlive()) {
                if (!health.decreaseHealth(laser.getDamage())) {
                    AnimatedExplosionEntity explosion = new AnimatedExplosionEntity(context, 0, 0);
                    explosion.setScale(0.1);
                    int centeredX = this.getX() + (this.getWidth() / 2) - (explosion.getWidth() / 2);
                    int centeredY = (this.getY() + this.getHeight() / 2) - (explosion.getHeight() / 2);
                    explosion.setX(centeredX);
                    explosion.setY(centeredY);
                    context.addEntity(explosion);

                    context.removeEntity(this);
                    context.notifyAlienKilled();
                }
            }
        }
    }

    @Override
    public void draw(java.awt.Graphics g) {
        // Override the default draw method to prevent automatic rotation.
        // This assumes the sprite already faces the correct direction (downwards).
        g.drawImage(sprite.getImage(), getX(), getY(), width, height, null);
    }
}
