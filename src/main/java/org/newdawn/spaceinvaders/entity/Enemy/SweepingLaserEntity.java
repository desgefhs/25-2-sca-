package org.newdawn.spaceinvaders.entity.Enemy;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.Entity;
import org.newdawn.spaceinvaders.entity.ShipEntity;

public class SweepingLaserEntity extends Entity {

    private final int damage = 2;

    public SweepingLaserEntity(GameContext context, int x, int y, double dx, double dy) {
        super("sprites/texture_laser.PNG", x, y);
        this.context = context;
        this.dx = dx;
        this.dy = dy;

        // Make the laser span the screen
        if (dx != 0) { // Horizontal sweep
            this.height = Game.GAME_HEIGHT;
        } else { // Vertical sweep
            this.width = Game.GAME_WIDTH;
        }
    }

    @Override
    public void move(long delta) {
        super.move(delta);

        // Remove the entity when it's off-screen
        if (x < -width || x > Game.GAME_WIDTH || y < -height || y > Game.GAME_HEIGHT) {
            context.removeEntity(this);
        }
    }

    @Override
    public void collidedWith(Entity other) {
        if (other instanceof ShipEntity) {
            ShipEntity ship = (ShipEntity) other;
            ship.getHealth().decreaseHealth(damage);
        }
    }
}