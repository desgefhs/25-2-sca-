package org.newdawn.spaceinvaders.entity.Effect;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.Entity;
import org.newdawn.spaceinvaders.graphics.Sprite;
import org.newdawn.spaceinvaders.graphics.SpriteStore;

/**
 * 폭발 애니메이션을 표시하는 엔티티
 */
public class AnimatedExplosionEntity extends Entity {

    private final int totalFrames = 15;
    private final Sprite[] frames = new Sprite[totalFrames];
    /** 각 프레임의 지속 시간 (40ms) */
    private final long frameDuration = 40;
    /** 마지막 프레임 변경 이후 경과 시간 */
    private long lastFrameChange;
    private int frameNumber;
    private GameContext context;

    /**
     * AnimatedExplosionEntity 객체를 생성
     * 생성 시 모든 애니메이션 프레임을 미리 로드
     *
     * @param context 게임 컨텍스트
     * @param x       x 좌표
     * @param y       y 좌표
     */
    public AnimatedExplosionEntity(GameContext context, int x, int y) {
        // 첫 번째 프레임으로 시작
        super(String.format("sprites/explosion/k2_%04d.png", 1), x, y);
        this.context = context;

        // 모든 프레임 미리 로드
        for (int i = 0; i < totalFrames; i++) {
            String frameRef = String.format("sprites/explosion/k2_%04d.png", i + 1);
            frames[i] = SpriteStore.get().getSprite(frameRef);
        }
    }

    /**
     * 프레임 지속 시간에 따라 프레임을 업데이트
     * 애니메이션이 완료되면 이 엔티티를 게임에서 제거.
     *
     * @param delta 경과 시간 (밀리초)
     */
    @Override
    public void move(long delta) {
        lastFrameChange += delta;

        // 프레임을 변경할 시간이 되면
        if (lastFrameChange > frameDuration) {
            lastFrameChange = 0;
            frameNumber++;

            // 애니메이션이 완료되면 엔티티 제거
            if (frameNumber >= totalFrames) {
                context.removeEntity(this);
                return;
            }

            // 현재 스프라이트를 다음 프레임으로 업데이트
            sprite = frames[frameNumber];
        }
    }

    @Override
    public void collidedWith(Entity other) {
        // 이 엔티티는 충돌 로직이 없음
    }
}
