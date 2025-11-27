package org.newdawn.spaceinvaders.entity;

import org.newdawn.spaceinvaders.core.GameContext;


public class ItemEntity extends Entity {

    private final GameContext context;

    public ItemEntity(GameContext context, int x, int y) {
        super("sprites/spr_shield.png", x, y);
        setScale(0.1);
        this.context = context;
        this.dy = 100; // Move downwards
    }

    @Override
    public void collidedWith(Entity other) {
        if (other instanceof ShipEntity) {
            // The lifecycle manager will handle publishing the event and removing the entity
            this.destroy();
        }
    }
}
