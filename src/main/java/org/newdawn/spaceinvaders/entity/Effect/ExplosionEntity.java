package org.newdawn.spaceinvaders.entity.Effect;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.Entity;

import java.awt.Graphics;

/**
 * 게임 내에서 짧은 시간 동안 표시되는 비애니메이션 폭발 효과 엔티티.
 * 생성 후 일정 시간이 지나면 스스로 게임에서 제거됩니다.
 */
public class ExplosionEntity extends Entity {
    /** 폭발 효과의 지속 시간 (밀리초). */
    private final long lifeTime = 500; // 0.5초
    /** 엔티티가 생성된 시간 (타임스탬프). */
    private final long createdAt;

    /**
     * ExplosionEntity 생성자.
     * @param context 게임 컨텍스트
     * @param sprite 폭발 스프라이트 리소스 경로
     * @param x 폭발의 x 좌표
     * @param y 폭발의 y 좌표
     */
    public ExplosionEntity(GameContext context, String sprite, int x, int y) {
        super(sprite, x, y);
        this.context = context;
        this.createdAt = System.currentTimeMillis();
    }

    /**
     * 폭발 스프라이트를 기존 크기보다 약간 크게 그립니다.
     * @param g 그래픽 컨텍스트
     */
    @Override
    public void draw(Graphics g) {
        int newWidth = (int) (sprite.getWidth() * 1.5);
        int newHeight = (int) (sprite.getHeight() * 1.5);
        g.drawImage(sprite.getImage(), (int) x, (int) y, newWidth, newHeight, null);
    }

    /**
     * 폭발 효과의 타이머를 업데이트하고, 지속 시간이 지나면 자신을 제거합니다.
     * @param delta 마지막 업데이트 이후 경과 시간 (밀리초)
     */
    @Override
    public void move(long delta) {
        super.move(delta); // Entity의 기본 이동 로직 (현재는 사용되지 않음)
        if (System.currentTimeMillis() - createdAt > lifeTime) {
            context.removeEntity(this);
        }
    }

    /**
     * 폭발 이펙트는 어떤 엔티티와도 충돌하지 않습니다. (추상 메소드 구현)
     * @param other 충돌한 다른 엔티티
     */
    @Override
    public void collidedWith(Entity other) {
        // 폭발 이펙트는 다른 엔티티와 충돌하지 않음
    }
}
