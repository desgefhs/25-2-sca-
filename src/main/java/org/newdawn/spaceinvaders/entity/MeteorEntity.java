package org.newdawn.spaceinvaders.entity;

import org.newdawn.spaceinvaders.core.GameContext;

public class MeteorEntity extends Entity {
    private double moveSpeed;
    private GameContext context;
    private HealthComponent health;

    public MeteorEntity(GameContext context, String sprite, int x, int y, double moveSpeed) {
        super(sprite, x, y);
        this.context = context;
        this.moveSpeed = moveSpeed;
        this.health = new HealthComponent(2);
        dy = moveSpeed;
        setScale(1.5);
    }

    @Override
    public void move(long delta) {
        super.move(delta);
        if (y > 600) {
            context.removeEntity(this);
        }
    }

    @Override
    public void collidedWith(Entity other) {
        // Collision with the ship is now handled by ShipEntity to respect invincibility frames.

        // If it collides with a shot, do nothing to the meteor.
        // The shot will destroy itself because the meteor has a health component.
        if (other instanceof ShotEntity) {
            return;
        }
    }

    @Override
    public HealthComponent getHealth() {
        return health;
    }
}
