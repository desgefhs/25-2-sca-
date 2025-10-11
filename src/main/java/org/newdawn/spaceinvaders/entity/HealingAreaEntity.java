package org.newdawn.spaceinvaders.entity;

import org.newdawn.spaceinvaders.core.GameContext;

/**
 * 플레이어를 치유하는 영역을 나타내는 엔티티
 * 플레이어와 충돌 시 플레이어의 체력을 회복시키고 사라짐
 */
public class HealingAreaEntity extends Entity {

    /** 게임 컨텍스트 */
    private final GameContext context;

    /**
     * HealingAreaEntity 객체를 생성
     *
     * @param context 게임 컨텍스트
     * @param x       x 좌표
     * @param y       y 좌표
     */
    public HealingAreaEntity(GameContext context, int x, int y) {
        super("sprites/HealingArea.png", x, y);
        this.context = context;
        this.dy = 100; // 아래쪽으로 이동
    }

    /**
     * 다른 엔티티와 충돌했을 때 호출
     * 충돌한 엔티티가 플레이어(ShipEntity)이면, 플레이어를 치유하고 자신을 제거
     *
     * @param other 충돌한 다른 엔티티
     */
    @Override
    public void collidedWith(Entity other) {
        if (other instanceof ShipEntity) {
            // 플레이어 치유 (최대 체력의 50%)
            context.getShip().heal(context.getShip().getMaxHealth() / 2);
            // 힐링 영역 제거
            context.removeEntity(this);
        }
    }
}
