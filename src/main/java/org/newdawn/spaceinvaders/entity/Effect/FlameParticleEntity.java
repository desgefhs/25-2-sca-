package org.newdawn.spaceinvaders.entity.Effect;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.Enemy.AlienEntity;
import org.newdawn.spaceinvaders.entity.Entity;
import org.newdawn.spaceinvaders.graphics.Sprite;
import org.newdawn.spaceinvaders.graphics.SpriteStore;

import java.awt.*;

public class FlameParticleEntity extends Entity {
    private float lifeTime = 400; // ms, 총 수명
    private final int damage = 1;

    private final Sprite[] frames = new Sprite[4];
    private int currentFrame = 0;
    private final long frameDuration = 100; // 프레임당 100ms
    private long frameTimer = 0;

    public FlameParticleEntity(GameContext context, int x, int y, double dx, double dy) {
        // 먼저, 첫 번째 프레임의 경로로 super()를 호출하여 엔티티를 올바르게 초기화합니다.
        super("sprites/fire/FlameParticle1_I.jpg", x, y);
        
        this.context = context;
        this.dx = dx;
        this.dy = dy;

        // 이제 애니메이션의 모든 프레임을 로드합니다.
        // 첫 번째 프레임은 이미 슈퍼 생성자에 의해 로드되었으며 this.sprite에 있습니다.
        frames[0] = this.sprite;
        frames[1] = SpriteStore.get().getSprite("sprites/fire/FlameParticle2_I.jpg");
        frames[2] = SpriteStore.get().getSprite("sprites/fire/FlameParticle3_I.jpg");
        frames[3] = SpriteStore.get().getSprite("sprites/fire/FlameParticle4_I.jpg");
    }

    @Override
    public void move(long delta) {
        super.move(delta);

        // 수명 업데이트
        lifeTime -= delta;
        if (lifeTime <= 0) {
            context.removeEntity(this);
            return;
        }

        // 애니메이션 프레임 업데이트
        frameTimer += delta;
        if (frameTimer > frameDuration) {
            frameTimer = 0;
            currentFrame = (currentFrame + 1) % 4; // 4개의 프레임을 순환합니다.
        }
    }

    @Override
    public void draw(Graphics g) {
        // 현재 애니메이션 프레임 그리기
        if (frames[currentFrame] != null) {
            g.drawImage(frames[currentFrame].getImage(), getX(), getY(), getWidth(), getHeight(), null);
        }
    }

    public int getDamage() {
        return damage;
    }

    @Override
    public void collidedWith(Entity other) {
        if (other instanceof AlienEntity) {
            context.removeEntity(this);
        }
    }
}