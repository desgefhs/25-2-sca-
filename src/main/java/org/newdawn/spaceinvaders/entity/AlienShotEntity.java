package org.newdawn.spaceinvaders.entity;


import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;

public class AlienShotEntity extends Entity {
    private double moveSpeed = 150; // Homing missiles are usually slower
    private GameContext context;
    private boolean used = false;
    private int damage;
    private long homingDuration = 500; // Homing is active for 1.5 seconds

    public AlienShotEntity(GameContext context, String sprite, int x, int y, int damage) {
        super(sprite, x, y); // Use the sprite passed from the alien
        this.context = context;
        this.damage = damage;
        setScale(1.5);
        // dy is no longer fixed, it will be calculated in move()
    }

    public int getDamage() {
        return damage;
    }

    public void move(long delta) {
        // Homing logic is only active for a certain duration
        if (homingDuration > 0) {
            homingDuration -= delta;

            ShipEntity ship = context.getShip();
            if (ship != null) {
                double targetX = ship.getX();
                double targetY = ship.getY();

                double diffX = targetX - x;
                double diffY = targetY - y;

                double length = Math.sqrt(diffX * diffX + diffY * diffY);
                if (length > 0) {
                    diffX /= length;
                    diffY /= length;
                }

                dx = diffX * moveSpeed;
                dy = diffY * moveSpeed;
            }
        }

        super.move(delta);

        // if we shot off the screen, remove ourselves
        if (y > Game.GAME_HEIGHT + 100) {
            context.removeEntity(this);
        }
    }

    public void collidedWith(Entity other) {
        if (used) {
            return;
        }
        if (other instanceof ShipEntity) {
            context.removeEntity(this);
            used = true;
        }
    }
}
