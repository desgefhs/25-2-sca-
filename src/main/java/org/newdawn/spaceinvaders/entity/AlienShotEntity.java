package org.newdawn.spaceinvaders.entity;

import org.newdawn.spaceinvaders.GameContext;

public class AlienShotEntity extends Entity {
    private double moveSpeed = 150;
    private GameContext context;
    private boolean used = false;
    private int damage;

    public AlienShotEntity(GameContext context, String sprite, int x, int y, int damage) {
        super("sprites/shot.gif", x, y);
        this.context = context;
        this.damage = damage;
        dy = moveSpeed;
    }

    public int getDamage() {
        return damage;
    }

    public void move(long delta) {
        super.move(delta);
        if (y > 600) {
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
