package org.newdawn.spaceinvaders.entity.Projectile;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.Entity;

import java.awt.Graphics;

/**
 * 화면 전체를 가로지르며 일정 시간 동안 지속되는 글로벌 레이저 엔티티.
 * 주로 보스 공격 패턴의 일환으로 사용되며, 겹치는 모든 엔티티에 피해를 줍니다.
 */
public class LaserEntity extends Entity {

    private final GameContext context;
    /** 레이저 발사 지속 시간 (밀리초). */
    private long fireDuration = 2000; // 2초 발사
    /** 레이저가 발사되었는지 여부. */
    private boolean fired = false; // 현재 사용되지 않음

    /**
     * LaserEntity 생성자.
     * @param context 게임 컨텍스트
     * @param x 레이저의 시작 x 좌표 (화면 전체를 커버하므로 중요하지 않을 수 있음)
     * @param width 레이저의 너비 (화면 전체를 커버하므로 Game.GAME_WIDTH와 같을 수 있음)
     */
    public LaserEntity(GameContext context, int x, int width) {
        super("sprites/texture_laser.PNG", x, 0); // y는 0으로 고정하여 화면 상단에서 시작
        this.context = context;
        this.width = width;
        this.height = Game.GAME_HEIGHT; // 화면 전체 높이
    }

    /**
     * 레이저의 지속 시간을 카운트다운하고, 시간이 다 되면 자신을 파괴합니다.
     * @param delta 마지막 업데이트 이후 경과 시간 (밀리초)
     */
    @Override
    public void move(long delta) {
        // 발사 지속 시간을 카운트다운하여 엔티티를 제거합니다.
        fireDuration -= delta;
        if (fireDuration <= 0) {
            this.destroy();
        }
    }

    /**
     * 레이저를 화면에 그립니다. 레이저 스프라이트를 타일처럼 반복하여 화면 너비를 채웁니다.
     * @param g 그래픽 컨텍스트
     */
    @Override
    public void draw(java.awt.Graphics g) {
        int tileWidth = 20; // 20px 너비의 타일로 레이저를 그립니다.

        int numTiles = (int) Math.ceil((double) Game.GAME_WIDTH / tileWidth);

        for (int i = 0; i < numTiles; i++) {
            g.drawImage(sprite.getImage(), getX() + i * tileWidth, getY(), tileWidth, height, null);
        }
    }

    /**
     * 레이저의 효과는 전역적이며 {@link #move(long)} 메소드에서 처리되므로,
     * 여기서는 충돌 로직이 필요하지 않습니다.
     * @param other 충돌한 다른 엔티티
     */
    @Override
    public void collidedWith(Entity other) {
        // 레이저의 효과는 전역적이며 move()에서 처리되므로, 여기서는 충돌 로직이 필요하지 않습니다.
    }
}
