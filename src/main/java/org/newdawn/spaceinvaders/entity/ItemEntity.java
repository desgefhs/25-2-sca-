package org.newdawn.spaceinvaders.entity;

import org.newdawn.spaceinvaders.core.GameContext;


public class ItemEntity extends Entity {

    private final GameContext context;

    public ItemEntity(GameContext context, int x, int y) {
        super("sprites/spr_shield.png", x, y);
        setScale(0.1);
        this.context = context;
        this.dy = 100; // 아래로 이동
    }

    @Override
    public void collidedWith(Entity other) {
        if (other instanceof ShipEntity) {
            // 생명 주기 관리자가 이벤트를 발행하고 엔티티를 제거하는 것을 처리합니다.
            this.destroy();
        }
    }
}