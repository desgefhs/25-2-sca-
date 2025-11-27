package org.newdawn.spaceinvaders.entity;

import org.newdawn.spaceinvaders.core.GameContext;

public class HealingAreaEntity extends Entity {

    private final GameContext context;

    public HealingAreaEntity(GameContext context, int x, int y) {
        super("sprites/HealingArea.png", x, y);
        this.context = context;
        this.dy = 100; // 아래로 이동
    }

    @Override
    public void collidedWith(Entity other) {
        if (other instanceof ShipEntity) {
            // 플레이어 치유
            context.getShip().heal(context.getShip().getMaxHealth() / 2);
            // 게임에서 치유 영역 제거
            context.removeEntity(this);
        }
    }
}