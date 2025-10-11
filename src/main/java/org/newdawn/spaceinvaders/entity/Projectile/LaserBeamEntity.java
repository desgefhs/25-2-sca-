package org.newdawn.spaceinvaders.entity.Projectile;

import org.newdawn.spaceinvaders.entity.Entity;

import org.newdawn.spaceinvaders.core.GameContext;
import java.awt.Graphics2D;

/**
 * 지정된 시간 동안 지속되는 레이저 빔을 나타내는 엔티티
 * 발사한 소유자(owner)를 따라다니며, 닿는 모든 적에게 지속적으로 피해를 줌
 */
public class LaserBeamEntity extends Entity {
    /** 레이저 지속 시간 (ms) */
    private final int duration;
    /** 레이저 데미지 */
    private final int damage;
    /** 레이저 생성 시간 */
    private long startTime;
    /** 레이저를 발사한 엔티티 */
    private Entity owner;

    /**
     * LaserBeamEntity 객체를 생성
     *
     * @param context  게임 컨텍스트
     * @param owner    레이저를 발사한 엔티티
     * @param duration 지속 시간 (ms)
     * @param damage   데미지
     */
    public LaserBeamEntity(GameContext context, Entity owner, int duration, int damage) {
        super("sprites/texture_laser.PNG", owner.getX(), owner.getY());
        this.context = context;
        this.owner = owner;
        this.duration = duration;
        this.damage = damage;
        this.startTime = System.currentTimeMillis();
        this.width = 20;
        this.height = 400;
    }

    /**
     * 레이저의 위치를 소유자에게 맞추고, 지속 시간이 다 되면 자신을 제거
     *
     * @param delta 경과 시간 (밀리초)
     */
    @Override
    public void move(long delta) {
        if (System.currentTimeMillis() - startTime > duration) {
            context.removeEntity(this);
            return;
        }

        // 소유자의 위치에 따라 레이저 위치 업데이트
        this.x = owner.getX() + owner.getWidth() / 2 - 10;
        this.y = owner.getY() - 400;
    }

    @Override
    public void draw(java.awt.Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(sprite.getImage(), (int)x, (int)y, 20, 400, null);
    }

    public void collidedWith(Entity other) {

    }

    /**
     * 레이저의 데미지를 반환
     * @return 데미지
     */
    public int getDamage() {
        return damage;
    }
}
