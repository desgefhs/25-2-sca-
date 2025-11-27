package org.newdawn.spaceinvaders.entity.Effect;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.Entity;
import org.newdawn.spaceinvaders.graphics.Sprite;
import org.newdawn.spaceinvaders.graphics.SpriteStore;

public class AnimatedExplosionEntity extends Entity {

    private final int totalFrames = 15;
    private final Sprite[] frames = new Sprite[totalFrames];
    private final long frameDuration = 40; // 각 프레임은 40ms 동안 지속됩니다.

    private long lastFrameChange;
    private int frameNumber;
    private final GameContext context;

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

    @Override
    public void collidedWith(Entity other) {
        // 폭발은 어떤 것과도 충돌하지 않습니다.
    }
}