package org.newdawn.spaceinvaders.entity;

import org.newdawn.spaceinvaders.core.GameContext;

/**
 * 플레이어가 수집할 수 있는 아이템을 나타내는 엔티티
 * 플레이어와 충돌 시 수집되었음을 알리고 게임에서 사라
 */
public class ItemEntity extends Entity {

    /** 게임 컨텍스트 */
    private final GameContext context;

    /**
     * ItemEntity 객체를 생성
     *
     * @param context 게임 컨텍스트
     * @param x       x 좌표
     * @param y       y 좌표
     */
    public ItemEntity(GameContext context, int x, int y) {
        super("sprites/spr_shield.png", x, y);
        setScale(0.1);
        this.context = context;
        this.dy = 100; // 아래쪽으로 이동
    }

    /**
     * 다른 엔티티와 충돌했을 때 호출
     * 충돌한 엔티티가 플레이어(ShipEntity)이면, 아이템이 수집되었음을 알리고 자신을 제거
     *
     * @param other 충돌한 다른 엔티티
     */
    @Override
    public void collidedWith(Entity other) {
        if (other instanceof ShipEntity) {
            // 게임 매니저에게 아이템이 수집되었음을 알림
            context.notifyItemCollected();
            // 아이템을 게임에서 제거
            context.removeEntity(this);
        }
    }
}
