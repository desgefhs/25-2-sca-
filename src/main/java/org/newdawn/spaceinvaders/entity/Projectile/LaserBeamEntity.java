package org.newdawn.spaceinvaders.entity.Projectile;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.Entity;

import java.awt.Graphics2D;

/**
 * 플레이어 함선에서 발사되는 레이저 빔 엔티티.
 * 일정 시간 동안 지속되며, 적에게 지속적인 피해를 줄 수 있습니다.
 */
public class LaserBeamEntity extends Entity {
    /** 레이저 빔의 지속 시간 (밀리초). */
    private final int duration;
    /** 레이저 빔이 주는 데미지. */
    private final int damage;
    /** 레이저 빔이 생성된 시간 (타임스탬프). */
    private final long startTime;
    /** 이 레이저 빔을 발사한 엔티티 (주로 플레이어 함선). */
    private final Entity owner;

    /**
     * LaserBeamEntity 생성자.
     * @param context 게임 컨텍스트
     * @param owner 이 레이저 빔을 소유한 엔티티
     * @param duration 레이저 빔의 지속 시간 (밀리초)
     * @param damage 레이저 빔이 주는 데미지
     */
    public LaserBeamEntity(GameContext context, Entity owner, int duration, int damage) {
        super("sprites/texture_laser.PNG", owner.getX(), owner.getY()); // 초기 위치는 발사자의 위치
        this.context = context;
        this.owner = owner;
        this.duration = duration;
        this.damage = damage;
        this.startTime = System.currentTimeMillis();
        this.width = 20; // 레이저 빔의 너비
        this.height = 400; // 레이저 빔의 높이
    }

    /**
     * 레이저 빔의 지속 시간을 업데이트하고, 지속 시간이 지나면 자신을 제거합니다.
     * @param delta 마지막 업데이트 이후 경과 시간 (밀리초)
     */
    @Override
    public void move(long delta) {
        // 지속 시간이 지나면 자신을 제거합니다.
        if (System.currentTimeMillis() - startTime > duration) {
            context.removeEntity(this);
            return;
        }

        // 레이저 빔의 위치를 소유자 엔티티에 맞춰 업데이트합니다.
        this.x = owner.getX() + owner.getWidth() / 2 - 10;
        this.y = owner.getY() - 400;
    }

    /**
     * 레이저 빔을 화면에 그립니다.
     * @param g 그래픽 컨텍스트
     */
    @Override
    public void draw(java.awt.Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        // 지정된 크기로 레이저 빔 이미지를 그림
        g2d.drawImage(sprite.getImage(), (int)x, (int)y, 20, 400, null);
    }

    /**
     * 다른 엔티티와의 충돌을 처리합니다.
     * 레이저는 충돌 시 자체적으로 어떤 행동도 취하지 않습니다.
     * 피해를 받는 책임은 충돌된 엔티티(예: 외계인)에 있습니다.
     * @param other 충돌한 다른 엔티티
     */
    public void collidedWith(Entity other) {
        // 이 레이저 빔은 충돌 시 자체적으로 아무런 행동도 취하지 않습니다.
    }

    /**
     * 레이저 빔이 주는 데미지를 반환합니다.
     * @return 데미지 값
     */
    public int getDamage() {
        return damage;
    }
}
