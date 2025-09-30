package org.newdawn.spaceinvaders.entity;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;

public class ThreeWayShotEntity extends Entity {
    private GameContext context;
    private boolean used = false;
    private int damage = 1; // Default damage

    public ThreeWayShotEntity(GameContext context, int x, int y, double dx, double dy) {
        super("sprites/ThreeWayShooter_shot.gif", x, y);
        this.context = context;
        this.dx = dx;
        this.dy = dy;
        setScale(1.5);
    }

    @Override
    public void move(long delta) {
        super.move(delta);
        // Remove if it goes off screen
        if (y > Game.GAME_HEIGHT + 100 || x < -100 || x > Game.GAME_WIDTH + 100) {
            context.removeEntity(this);
        }
    }

    @Override
    public void collidedWith(Entity other) {
        if (used) {
            return;
        }
        // Hits the ship, remove itself.
        // The ship will handle taking damage.
        if (other instanceof ShipEntity) {
            context.removeEntity(this);
            used = true;
        }
    }

    public int getDamage() {
        return damage;
    }
}
