package org.newdawn.spaceinvaders.entity.Enemy;

import org.newdawn.spaceinvaders.core.GameContext;

import org.newdawn.spaceinvaders.entity.*;
import org.newdawn.spaceinvaders.entity.Effect.AnimatedExplosionEntity;
import org.newdawn.spaceinvaders.entity.Projectile.LaserBeamEntity;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileEntity;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileType;

public class MeteorEntity extends Entity {

    public enum MeteorType {
        SMALL("sprites/meteors2.gif", 1),
        MEDIUM("sprites/meteors3.gif", 2),
        LARGE("sprites/meteors4.gif", 3);

        public final String spritePath;
        public final int maxHealth;

        MeteorType(String spritePath, int maxHealth) {
            this.spritePath = spritePath;
            this.maxHealth = maxHealth;
        }
    }

    private final GameContext context;
    private final int scoreValue;

    public int getScoreValue() {
        return scoreValue;
    }

    public MeteorEntity(GameContext context, MeteorType type, int x, int y) {
        super(type.spritePath, x, y);
        this.context = context;
        this.health = new HealthComponent(this, type.maxHealth);
        this.scoreValue = type.maxHealth * 5; // Score is proportional to health
        this.dy = (Math.random() * 50) + 50; // Random downward speed between 50 and 100
    }

    @Override
    public void collidedWith(Entity other) {
        // Check for collision with player projectiles
        if (other instanceof ProjectileEntity) {
            ProjectileEntity shot = (ProjectileEntity) other;

            // Ensure it's a player's shot
            if (shot.getType().targetType == ProjectileType.TargetType.ENEMY) {
                // Remove the projectile on impact
                context.removeEntity(shot);

                // Decrease health by a fixed amount of 1, regardless of shot damage
                if (!health.decreaseHealth(1)) {
                    // This meteor is destroyed
                    this.destroy();

                    // Create a scaled and centered explosion effect
                    AnimatedExplosionEntity explosion = new AnimatedExplosionEntity(context, 0, 0);
                    explosion.setScale(0.1);
                    int centeredX = this.getX() + (this.getWidth() / 2) - (explosion.getWidth() / 2);
                    int centeredY = this.getY() + (this.getHeight() / 2) - (explosion.getHeight() / 2);
                    explosion.setX(centeredX);
                    explosion.setY(centeredY);
                    context.addEntity(explosion);
                }
            }
        }

        // Optional: Handle collision with the player's ship
        if (other instanceof ShipEntity) {
            // Damage the player equal to the meteor's remaining health
            ShipEntity ship = (ShipEntity) other;
            if (!ship.getHealth().decreaseHealth(this.health.getCurrentHealth())) {
                ship.destroy();
            }

            // Destroy the meteor on impact
            this.destroy();
        } else if (other instanceof LaserBeamEntity) {
            // Meteors are instantly destroyed by lasers for now.
            this.destroy();

            // Create a scaled and centered explosion effect
            AnimatedExplosionEntity explosion = new AnimatedExplosionEntity(context, 0, 0);
            explosion.setScale(0.1);
            int centeredX = this.getX() + (this.getWidth() / 2) - (explosion.getWidth() / 2);
            int centeredY = this.getY() + (this.getHeight() / 2) - (explosion.getHeight() / 2);
            explosion.setX(centeredX);
            explosion.setY(centeredY);
            context.addEntity(explosion);
        }
    }
}
