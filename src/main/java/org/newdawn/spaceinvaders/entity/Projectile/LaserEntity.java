package org.newdawn.spaceinvaders.entity.Projectile;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;

import org.newdawn.spaceinvaders.entity.Entity;

public class LaserEntity extends Entity {

    private final GameContext context;
    private long fireDuration = 5000; // Fire for 5 seconds
    private boolean fired = false;

    public LaserEntity(GameContext context, int x, int width) {
        super("sprites/texture_laser.PNG", x, 0);
        this.context = context;
        this.width = width;
        this.height = Game.GAME_HEIGHT;
    }

    @Override
    public void move(long delta) {
        // Perform the death check only once, right after the laser is created.
        if (!fired) {
            if (!context.hasCollectedAllItems()) {
                context.notifyDeath();
            }
            fired = true;
        }

        // Countdown the firing duration to remove the entity
        fireDuration -= delta;
        if (fireDuration <= 0) {
            context.removeEntity(this);
        }
    }

    @Override
    public void draw(java.awt.Graphics g) {
        // Always draw the sprite stretched and at full opacity
        g.drawImage(sprite.getImage(), getX(), getY(), width, height, null);
    }

    @Override
    public void collidedWith(Entity other) {
        // The laser's effect is global and handled in move(), so no collision logic is needed here.
    }
}
