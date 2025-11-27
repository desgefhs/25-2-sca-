package org.newdawn.spaceinvaders.entity;

import org.newdawn.spaceinvaders.core.GameContext;

/**
 * 플레이어가 수집할 수 있는 아이템 엔티티.
 * 플레이어 함선과 충돌 시 파괴되며, 이에 대한 이벤트가 발생합니다.
 */
public class ItemEntity extends Entity {

    private final GameContext context;

    /**
     * ItemEntity 생성자.
     * @param context 게임 컨텍스트
     * @param x 초기 x 좌표
     * @param y 초기 y 좌표
     */
    public ItemEntity(GameContext context, int x, int y) {
        super("sprites/spr_shield.png", x, y); // 현재는 쉴드 스프라이트를 사용
        setScale(0.1);
        this.context = context;
        this.dy = 100; // 아래로 이동
    }

    /**
     * 다른 엔티티와의 충돌을 처리합니다.
     * 플레이어 함선과 충돌하면 자신을 파괴 상태로 표시합니다.
     * @param other 충돌한 다른 엔티티
     */
    @Override
    public void collidedWith(Entity other) {
        if (other instanceof ShipEntity) {
            // 생명 주기 관리자가 이벤트를 발행하고 엔티티를 제거하는 것을 처리합니다.
            this.destroy();
        }
    }
}