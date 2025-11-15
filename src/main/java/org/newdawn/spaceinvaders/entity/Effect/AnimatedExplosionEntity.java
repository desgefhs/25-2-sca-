package org.newdawn.spaceinvaders.entity.Effect;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.Entity;
import org.newdawn.spaceinvaders.graphics.Sprite;
import org.newdawn.spaceinvaders.graphics.SpriteStore;

public class AnimatedExplosionEntity extends Entity {

    private final int totalFrames = 15;
    private final Sprite[] frames = new Sprite[totalFrames];
    private final long frameDuration = 40; // Each frame lasts 40ms

    private long lastFrameChange;
    private int frameNumber;
    private final GameContext context;

    public AnimatedExplosionEntity(GameContext context, int x, int y) {
        // Start with the first frame
        super(String.format("sprites/explosion/k2_%04d.png", 1), x, y);
        this.context = context;

        // Pre-load all frames
        for (int i = 0; i < totalFrames; i++) {
            String frameRef = String.format("sprites/explosion/k2_%04d.png", i + 1);
            frames[i] = SpriteStore.get().getSprite(frameRef);
        }
    }

    @Override
    public void move(long delta) {
        lastFrameChange += delta;

        // If it's time to change the frame
        if (lastFrameChange > frameDuration) {
            lastFrameChange = 0;
            frameNumber++;

            // If the animation is complete, remove the entity
            if (frameNumber >= totalFrames) {
                context.removeEntity(this);
                return;
            }

            // Update the current sprite to the next frame
            sprite = frames[frameNumber];
        }
    }

    @Override
    public void collidedWith(Entity other) {
        // An explosion does not collide with anything
    }
}
