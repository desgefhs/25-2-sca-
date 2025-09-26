package org.newdawn.spaceinvaders.entity;

import org.newdawn.spaceinvaders.GameContext;

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
        if (other instanceof ShipEntity) {
            context.notifyDeath();
            return;
        }

        if (other instanceof ShotEntity) {
            if (!health.isAlive()) {
                return;
            }
            if (!health.decreaseHealth(((ShotEntity) other).getDamage())) {
                context.removeEntity(this);
            }
        }
    }

    @Override
    public HealthComponent getHealth() {
        return health;
    }
}
