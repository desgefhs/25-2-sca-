package org.newdawn.spaceinvaders.entity.Effect;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.Entity;

import java.awt.Graphics;

/**
 * 단일 이미지로 된 폭발 효과를 일정 시간 동안 표시하는 엔티티
 */
public class ExplosionEntity extends Entity {
    /** 이펙트 지속 시간 (0.5초) */
    private final long lifeTime = 500;
    /** 엔티티 생성 시간 */
    private long createdAt;
    private GameContext context;

    /**
     * ExplosionEntity 객체를 생성
     *
     * @param context 게임 컨텍스트
     * @param sprite  표시할 스프라이트 경로
     * @param x       x 좌표
     * @param y       y 좌표
     */
    public ExplosionEntity(GameContext context, String sprite, int x, int y) {
        super(sprite, x, y);
        this.context = context;
        this.createdAt = System.currentTimeMillis();
    }

    /**
     * 스프라이트를 1.5배 크기로 확대하여 그림
     */
    @Override
    public void draw(Graphics g) {
        int newWidth = (int) (sprite.getWidth() * 1.5);
        int newHeight = (int) (sprite.getHeight() * 1.5);
        g.drawImage(sprite.getImage(), (int) x, (int) y, newWidth, newHeight, null);
    }

    /**
     * 엔티티의 생존 시간을 확인하고, 지속 시간이 다 되면 게임에서 제거
     *
     * @param delta 경과 시간 (밀리초)
     */
    @Override
    public void move(long delta) {
        super.move(delta);
        if (System.currentTimeMillis() - createdAt > lifeTime) {
            context.removeEntity(this);
        }
    }

    @Override
    public void collidedWith(Entity other) {
        // 이 엔티티는 충돌 로직이 없음
    }
}
