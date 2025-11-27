package org.newdawn.spaceinvaders.entity.Enemy;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.Entity;
import org.newdawn.spaceinvaders.entity.ShipEntity;

/**
 * 플레이어에게 피해를 주는 쓸어버리는 레이저 엔티티.
 * 생성 시 지정된 방향으로 움직이며, 화면 밖으로 나가면 스스로 제거됩니다.
 */
public class SweepingLaserEntity extends Entity {

    private final int damage = 2;

    /**
     * SweepingLaserEntity 생성자.
     * @param context 게임 컨텍스트
     * @param x 초기 x 좌표
     * @param y 초기 y 좌표
     * @param dx 수평 이동 속도
     * @param dy 수직 이동 속도
     */
    public SweepingLaserEntity(GameContext context, int x, int y, double dx, double dy) {
        super("sprites/texture_laser.PNG", x, y);
        this.context = context;
        this.dx = dx;
        this.dy = dy;

        // 레이저가 화면을 가로지르도록 크기 설정
        if (dx != 0) { // 수평 스윕
            this.height = Game.GAME_HEIGHT;
        } else { // 수직 스윕
            this.width = Game.GAME_WIDTH;
        }
    }

    /**
     * 레이저를 이동시키고, 화면 밖으로 나가면 자신을 제거합니다.
     * @param delta 마지막 업데이트 이후 경과 시간
     */
    @Override
    public void move(long delta) {
        super.move(delta);

        // 화면 밖으로 나가면 엔티티를 제거합니다.
        if (x < -width || x > Game.GAME_WIDTH || y < -height || y > Game.GAME_HEIGHT) {
            context.removeEntity(this);
        }
    }

    /**
     * 다른 엔티티와의 충돌을 처리합니다.
     * 플레이어 함선과 충돌하면 함선에 피해를 줍니다.
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
