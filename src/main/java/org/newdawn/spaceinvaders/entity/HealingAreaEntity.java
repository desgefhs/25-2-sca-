package org.newdawn.spaceinvaders.entity;

import org.newdawn.spaceinvaders.core.GameContext;

/**
 * 플레이어 함선이 충돌하면 치유 효과를 제공하는 엔티티.
 * 플레이어에게 닿으면 사라집니다.
 */
public class HealingAreaEntity extends Entity {

    /**
     * HealingAreaEntity 생성자.
     * @param context 게임 컨텍스트
     * @param x 초기 x 좌표
     * @param y 초기 y 좌표
     */
    public HealingAreaEntity(GameContext context, int x, int y) {
        super("sprites/HealingArea.png", x, y);
        this.context = context;
        this.dy = 100; // 아래로 이동
    }

    /**
     * 다른 엔티티와의 충돌을 처리합니다.
     * 플레이어 함선과 충돌하면 함선을 치유하고 자신은 제거됩니다.
     * @param other 충돌한 다른 엔티티
     */
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