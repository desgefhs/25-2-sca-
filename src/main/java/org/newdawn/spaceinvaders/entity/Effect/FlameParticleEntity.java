package org.newdawn.spaceinvaders.entity.Effect;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.Enemy.AlienEntity;
import org.newdawn.spaceinvaders.entity.Entity;
import org.newdawn.spaceinvaders.graphics.Sprite;
import org.newdawn.spaceinvaders.graphics.SpriteStore;

import java.awt.*;

/**
 * 게임 내에서 짧은 시간 동안 표시되는 애니메이션 불꽃 입자 엔티티.
 * 적에게 피해를 줄 수 있으며, 적과 충돌 시 사라집니다.
 */
public class FlameParticleEntity extends Entity {
    /** 파티클의 총 수명 (밀리초). */
    private float lifeTime = 400; // ms
    /** 파티클이 주는 데미지. */
    private final int damage = 1;

    /** 애니메이션 프레임 스프라이트 배열. */
    private final Sprite[] frames = new Sprite[4];
    /** 현재 표시 중인 프레임 인덱스. */
    private int currentFrame = 0;
    /** 각 프레임이 지속되는 시간 (밀리초). */
    private final long frameDuration = 100;
    /** 마지막 프레임 변경 이후 경과 시간. */
    private long frameTimer = 0;

    /**
     * FlameParticleEntity 생성자.
     * @param context 게임 컨텍스트
     * @param x 초기 x 좌표
     * @param y 초기 y 좌표
     * @param dx 초기 수평 속도
     * @param dy 초기 수직 속도
     */
    public FlameParticleEntity(GameContext context, int x, int y, double dx, double dy) {
        super("sprites/fire/FlameParticle1_I.jpg", x, y); // 첫 번째 프레임으로 엔티티 초기화
        
        this.context = context;
        this.dx = dx;
        this.dy = dy;

        // 모든 애니메이션 프레임을 로드합니다.
        frames[0] = this.sprite;
        frames[1] = SpriteStore.get().getSprite("sprites/fire/FlameParticle2_I.jpg");
        frames[2] = SpriteStore.get().getSprite("sprites/fire/FlameParticle3_I.jpg");
        frames[3] = SpriteStore.get().getSprite("sprites/fire/FlameParticle4_I.jpg");
    }

    /**
     * 파티클의 수명과 애니메이션 프레임을 업데이트합니다.
     * 수명이 다하거나 애니메이션이 완료되면 자신을 제거합니다.
     * @param delta 마지막 업데이트 이후 경과 시간 (밀리초)
     */
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
            currentFrame = (currentFrame + 1) % 4; // 4개의 프레임을 순환
        }
    }

    /**
     * 현재 애니메이션 프레임을 화면에 그립니다.
     * @param g 그래픽 컨텍스트
     */
    @Override
    public void draw(Graphics g) {
        if (frames[currentFrame] != null) {
            g.drawImage(frames[currentFrame].getImage(), getX(), getY(), getWidth(), getHeight(), null);
        }
    }

    /**
     * 파티클이 주는 데미지 값을 반환합니다.
     * @return 데미지 값
     */
    public int getDamage() {
        return damage;
    }

    /**
     * 다른 엔티티와의 충돌을 처리합니다.
     * 외계인과 충돌하면 자신을 제거합니다.
     * @param other 충돌한 다른 엔티티
     */
    @Override
    public void collidedWith(Entity other) {
        if (other instanceof AlienEntity) {
            context.removeEntity(this);
        }
    }
}