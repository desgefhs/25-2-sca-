package org.newdawn.spaceinvaders.entity.Effect;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.Entity;
import org.newdawn.spaceinvaders.graphics.Sprite;
import org.newdawn.spaceinvaders.graphics.SpriteStore;

/**
 * 애니메이션 효과가 있는 폭발을 표시하는 엔티티.
 * 정해진 프레임 수만큼 애니메이션을 재생한 후 스스로 게임에서 제거됩니다.
 */
public class AnimatedExplosionEntity extends Entity {

    /** 총 프레임 수. */
    private final int totalFrames = 15;
    /** 각 프레임의 스프라이트를 저장하는 배열. */
    private final Sprite[] frames = new Sprite[totalFrames];
    /** 각 프레임이 지속되는 시간 (밀리초). */
    private final long frameDuration = 40;

    /** 마지막 프레임 변경 이후 경과 시간. */
    private long lastFrameChange;
    /** 현재 표시 중인 프레임 번호. */
    private int frameNumber;

    /**
     * AnimatedExplosionEntity 생성자.
     * @param context 게임 컨텍스트
     * @param x 폭발의 x 좌표
     * @param y 폭발의 y 좌표
     */
    public AnimatedExplosionEntity(GameContext context, int x, int y) {
        // 첫 번째 프레임으로 시작
        super(String.format("sprites/explosion/k2_%04d.png", 1), x, y);
        this.context = context;

        // 모든 프레임을 미리 로드합니다.
        for (int i = 0; i < totalFrames; i++) {
            String frameRef = String.format("sprites/explosion/k2_%04d.png", i + 1);
            frames[i] = SpriteStore.get().getSprite(frameRef);
        }
    }

    /**
     * 애니메이션의 프레임을 업데이트하고, 애니메이션이 완료되면 자신을 제거합니다.
     * @param delta 마지막 업데이트 이후 경과 시간 (밀리초)
     */
    @Override
    public void move(long delta) {
        lastFrameChange += delta;

        // 프레임을 변경할 시간이 되면
        if (lastFrameChange > frameDuration) {
            lastFrameChange = 0;
            frameNumber++;

            // 애니메이션이 완료되면 엔티티를 제거합니다.
            if (frameNumber >= totalFrames) {
                context.removeEntity(this);
                return;
            }

            // 현재 스프라이트를 다음 프레임으로 업데이트합니다.
            sprite = frames[frameNumber];
        }
    }

    /**
     * 폭발 엔티티는 어떤 엔티티와도 충돌하지 않습니다. (추상 메소드 구현)
     * @param other 충돌한 다른 엔티티
     */
    @Override
    public void collidedWith(Entity other) {
        // 폭발은 어떤 것과도 충돌하지 않습니다.
    }
}