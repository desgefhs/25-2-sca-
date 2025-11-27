package org.newdawn.spaceinvaders.entity.Enemy;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.Entity;
import org.newdawn.spaceinvaders.entity.ShipEntity;

public class SweepingLaserEntity extends Entity {

    private final int damage = 2;

    public SweepingLaserEntity(GameContext context, int x, int y, double dx, double dy) {
        super("sprites/texture_laser.PNG", x, y);
        this.context = context;
        this.dx = dx;
        this.dy = dy;

        // 레이저가 화면을 가로지르도록 만듭니다.
        if (dx != 0) { // 수평 스윕
            this.height = Game.GAME_HEIGHT;
        } else { // 수직 스윕
            this.width = Game.GAME_WIDTH;
        }
    }

    @Override
    public void move(long delta) {
        super.move(delta);

        // 화면 밖으로 나가면 엔티티를 제거합니다.
        if (x < -width || x > Game.GAME_WIDTH || y < -height || y > Game.GAME_HEIGHT) {
            context.removeEntity(this);
        }
    }

    @Override
    public void collidedWith(Entity other) {
        if (other instanceof ShipEntity) {
            ShipEntity ship = (ShipEntity) other;
            ship.getHealth().decreaseHealth(damage);
        }
    }
}
