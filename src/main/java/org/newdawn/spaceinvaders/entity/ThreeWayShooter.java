package org.newdawn.spaceinvaders.entity;

import org.newdawn.spaceinvaders.core.GameContext;

public class ThreeWayShooter extends Entity {
    private double moveSpeed = 100; // Movement speed of the shooter itself
    private GameContext context;

    private long lastFire = 0;
    private long firingInterval = 2000; // Fires every 2 seconds

    private final EngineFireEntity fireEffect;

    public ThreeWayShooter(GameContext context, int x, int y) {
        super("sprites/ThreeWayShooter.gif", x, y);
        this.context = context;
        this.health = new HealthComponent(5); // Example health
        dy = moveSpeed;

        this.fireEffect = new EngineFireEntity(context, this);
        this.context.addEntity(this.fireEffect);
    }

    private void tryToFire() {
        if (System.currentTimeMillis() - lastFire < firingInterval) {
            return;
        }
        lastFire = System.currentTimeMillis();

        double shotMoveSpeed = 250;
        double angle = Math.toRadians(30);

        // Center shot (0 degrees)
        context.addEntity(new ThreeWayShotEntity(context, getX() + (width/2), getY() + height, 0, shotMoveSpeed));

        // Left shot (-30 degrees)
        double dxLeft = -Math.sin(angle) * shotMoveSpeed;
        double dyLeft = Math.cos(angle) * shotMoveSpeed;
        context.addEntity(new ThreeWayShotEntity(context, getX() + (width/2), getY() + height, dxLeft, dyLeft));

        // Right shot (+30 degrees)
        double dxRight = Math.sin(angle) * shotMoveSpeed;
        double dyRight = Math.cos(angle) * shotMoveSpeed;
        context.addEntity(new ThreeWayShotEntity(context, getX() + (width/2), getY() + height, dxRight, dyRight));
    }

    @Override
    public void move(long delta) {
        super.move(delta);

        tryToFire();

        // if we have gone off the bottom of the screen, remove ourselves
        if (y > 600) {
            context.removeEntity(this.fireEffect);
            context.removeEntity(this);
        }
    }

    @Override
    public void collidedWith(Entity other) {
        // if it's a shot from the player, take damage
        if (other instanceof ShotEntity) {
            if (!health.isAlive()) {
                return;
            }

            if (!health.decreaseHealth(((ShotEntity) other).getDamage())) {
                // Create, scale, and position the explosion to be centered on the shooter
                AnimatedExplosionEntity explosion = new AnimatedExplosionEntity(context, 0, 0);
                explosion.setScale(0.1);
                int centeredX = this.getX() + (this.getWidth() / 2) - (explosion.getWidth() / 2);
                int centeredY = (this.getY() + this.getHeight()) - (explosion.getHeight() / 2);
                explosion.setX(centeredX);
                explosion.setY(centeredY);
                context.addEntity(explosion);

                // Remove the fire effect as well
                context.removeEntity(this.fireEffect);
                // Remove the shooter from the game
                context.removeEntity(this);
                // This new alien type doesn't count towards the main alien count for wave progression
                // but we could add a new notification if we wanted (e.g. notifySpecialAlienKilled)
            }
        }
    }
}