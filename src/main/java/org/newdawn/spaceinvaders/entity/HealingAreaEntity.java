package org.newdawn.spaceinvaders.entity;

import org.newdawn.spaceinvaders.core.GameContext;

public class HealingAreaEntity extends Entity {

    private final GameContext context;

    public HealingAreaEntity(GameContext context, int x, int y) {
        super("sprites/HealingArea.png", x, y);
        this.context = context;
        this.dy = 100; // Move downwards
    }

    @Override
    public void collidedWith(Entity other) {
        if (other instanceof ShipEntity) {
            // Heal the player
            context.getShip().heal(context.getShip().getMaxHealth() / 2);
            // Remove the healing area from the game
            context.removeEntity(this);
        }
    }
}
