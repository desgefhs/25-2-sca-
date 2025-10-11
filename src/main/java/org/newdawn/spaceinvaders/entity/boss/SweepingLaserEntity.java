package org.newdawn.spaceinvaders.entity.boss;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.Entity;
import org.newdawn.spaceinvaders.entity.ShipEntity;

/**
 * 화면을 가로지르며 스윕하는 레이저 빔을 나타내는 엔티티
 * 이 레이저는 플레이어에게 피해를 줍니다.
 */
public class SweepingLaserEntity extends Entity {

    /** 레이저의 데미지 */
    private final int damage = 2;

    /**
     * SweepingLaserEntity 객체를 생성
     *
     * @param context 게임 컨텍스트
     * @param x       x 좌표
     * @param y       y 좌표
     * @param dx      수평 이동 속도
     * @param dy      수직 이동 속도
     */
    public SweepingLaserEntity(GameContext context, int x, int y, double dx, double dy) {
        super("sprites/texture_laser.PNG", x, y);
        this.context = context;
        this.dx = dx;
        this.dy = dy;

        // 레이저가 화면을 가득 채우도록 크기 조절
        if (dx != 0) { // 수평 스윕
            this.height = Game.GAME_HEIGHT;
        } else { // 수직 스윕
            this.width = Game.GAME_WIDTH;
        }
    }

    @Override
    public void move(long delta) {
        super.move(delta);

        // 화면 밖으로 나가면 엔티티 제거
        if (x < -width || x > Game.GAME_WIDTH || y < -height || y > Game.GAME_HEIGHT) {
            context.removeEntity(this);
        }
    }

    /**
     * 다른 엔티티와 충돌했을 때 호출
     *
     * @param other 충돌한 다른 엔티티
     */
    @Override
    public void collidedWith(Entity other) {
        if (other instanceof ShipEntity) {
            ShipEntity ship = (ShipEntity) other;
            ship.getHealth().decreaseHealth(damage);
        }
    }
}