package org.newdawn.spaceinvaders.entity;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.graphics.Sprite;
import org.newdawn.spaceinvaders.graphics.SpriteStore;

public class EngineFireEntity extends Entity {

    private final Entity parent;
    private final GameContext context;

    private final Sprite[] frames = new Sprite[3];
    private final long frameDuration = 100; // ms
    private long lastFrameChange;
    private int frameNumber;

    public EngineFireEntity(GameContext context, Entity parent) {
        // Start with the first frame, at a dummy position
        super("sprites/fire effect/18 Ion.png", 0, 0);
        this.context = context;
        this.parent = parent;

        // Pre-load all frames
        frames[0] = sprite;
        frames[1] = SpriteStore.get().getSprite("sprites/fire effect/19 Ion.png");
        frames[2] = SpriteStore.get().getSprite("sprites/fire effect/20 Ion.png");
        setScale(0.8);
    }

    @Override
    public void move(long delta) {
        // Follow the parent
        if (parent != null) {
            // Center the effect above the parent (its rear)
            this.x = parent.getX() + (parent.getWidth() / 2) - (this.getWidth() / 2);
            this.y = parent.getY() - this.getHeight() + 10;
        }

        // Animate
        lastFrameChange += delta;
        if (lastFrameChange > frameDuration) {
            lastFrameChange = 0;
            frameNumber = (frameNumber + 1) % frames.length;
            sprite = frames[frameNumber];
        }
    }

    @Override
    public void collidedWith(Entity other) {
        // This effect does not collide with anything
    }
}
