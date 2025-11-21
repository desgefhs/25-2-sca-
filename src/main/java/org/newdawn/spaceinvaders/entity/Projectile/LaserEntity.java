package org.newdawn.spaceinvaders.entity.Projectile;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;


import org.newdawn.spaceinvaders.entity.Entity;

public class LaserEntity extends Entity {

    private final GameContext context;
    private long fireDuration = 2000; // Fire for 2 seconds
    private boolean fired = false;

    public LaserEntity(GameContext context, int x, int width) {
        super("sprites/texture_laser.PNG", x, 0);
        this.context = context;
        this.width = width;
        this.height = Game.GAME_HEIGHT;
    }

    @Override
    public void move(long delta) {
        // Countdown the firing duration to remove the entity
        fireDuration -= delta;
        if (fireDuration <= 0) {
            this.destroy();
        }
    }

    @Override
    public void draw(java.awt.Graphics g) {
        int tileWidth = 20; // Draw the laser in 20px wide tiles

        int numTiles = (int) Math.ceil((double) Game.GAME_WIDTH / tileWidth);

        for (int i = 0; i < numTiles; i++) {
            g.drawImage(sprite.getImage(), getX() + i * tileWidth, getY(), tileWidth, height, null);
        }
    }

    @Override
    public void collidedWith(Entity other) {
        // The laser's effect is global and handled in move(), so no collision logic is needed here.
    }
}
