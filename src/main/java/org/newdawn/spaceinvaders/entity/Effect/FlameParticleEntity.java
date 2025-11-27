package org.newdawn.spaceinvaders.entity.Effect;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.Enemy.AlienEntity;
import org.newdawn.spaceinvaders.entity.Entity;
import org.newdawn.spaceinvaders.graphics.Sprite;
import org.newdawn.spaceinvaders.graphics.SpriteStore;

import java.awt.*;

public class FlameParticleEntity extends Entity {
    private float lifeTime = 400; // ms, total lifetime
    private final int damage = 1;

    private final Sprite[] frames = new Sprite[4];
    private int currentFrame = 0;
    private final long frameDuration = 100; // 100ms per frame
    private long frameTimer = 0;

    public FlameParticleEntity(GameContext context, int x, int y, double dx, double dy) {
        // First, call super() with the path to the first frame to properly initialize the Entity
        super("sprites/fire/FlameParticle1_I.jpg", x, y);
        
        this.context = context;
        this.dx = dx;
        this.dy = dy;

        // Now, load all frames for the animation
        // The first frame is already loaded by the super constructor and is in this.sprite
        frames[0] = this.sprite;
        frames[1] = SpriteStore.get().getSprite("sprites/fire/FlameParticle2_I.jpg");
        frames[2] = SpriteStore.get().getSprite("sprites/fire/FlameParticle3_I.jpg");
        frames[3] = SpriteStore.get().getSprite("sprites/fire/FlameParticle4_I.jpg");
    }

    @Override
    public void move(long delta) {
        super.move(delta);

        // Update lifetime
        lifeTime -= delta;
        if (lifeTime <= 0) {
            context.removeEntity(this);
            return;
        }

        // Update animation frame
        frameTimer += delta;
        if (frameTimer > frameDuration) {
            frameTimer = 0;
            currentFrame = (currentFrame + 1) % 4; // Loop through 4 frames
        }
    }

    @Override
    public void draw(Graphics g) {
        // Draw the current animation frame
        if (frames[currentFrame] != null) {
            g.drawImage(frames[currentFrame].getImage(), getX(), getY(), getWidth(), getHeight(), null);
        }
    }

    public int getDamage() {
        return damage;
    }

    @Override
    public void collidedWith(Entity other) {
        if (other instanceof AlienEntity) {
            context.removeEntity(this);
        }
    }
}
