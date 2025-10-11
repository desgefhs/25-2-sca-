package org.newdawn.spaceinvaders.entity.Projectile;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;

import org.newdawn.spaceinvaders.entity.Entity;

/**
 * 보스전의 특정 기믹으로 사용되는 전역 레이저 엔티티
 * 생성 직후 플레이어가 모든 아이템을 수집했는지 확인하고, 그렇지 않으면 플레이어를 즉시 사망시킴
 * 그 후 일정 시간 동안 화면에 표시되다가 사라짐
 */
public class LaserEntity extends Entity {

    private final GameContext context;
    /** 레이저 표시 시간 (2초) */
    private long fireDuration = 2000;
    /** 기믹 판정이 실행되었는지 여부 */
    private boolean fired = false;

    /**
     * LaserEntity 객체를 생성
     *
     * @param context 게임 컨텍스트
     * @param x       x 좌표
     * @param width   레이저의 너비
     */
    public LaserEntity(GameContext context, int x, int width) {
        super("sprites/texture_laser.PNG", x, 0);
        this.context = context;
        this.width = width;
        this.height = Game.GAME_HEIGHT;
    }

    /**
     * 레이저의 기믹을 실행하고, 지속 시간이 다 되면 자신을 제거
     *
     * @param delta 경과 시간 (밀리초)
     */
    @Override
    public void move(long delta) {
        // 기믹 판정은 생성 직후 한 번만 실행
        if (!fired) {
            // 플레이어가 모든 아이템을 수집하지 못했다면 즉시 사망 처리
            if (!context.hasCollectedAllItems()) {
                context.notifyDeath();
            }
            fired = true;
        }

        // 표시 시간 카운트다운 후 엔티티 제거
        fireDuration -= delta;
        if (fireDuration <= 0) {
            context.removeEntity(this);
        }
    }

    /**
     * 레이저 텍스처를 타일처럼 반복해서 그려 화면 전체를 채움
     *
     * @param g 그래픽 컨텍스트
     */
    @Override
    public void draw(java.awt.Graphics g) {
        int tileWidth = 20; // 20px 너비의 타일로 레이저를 그림

        int numTiles = (int) Math.ceil((double) Game.GAME_WIDTH / tileWidth);

        for (int i = 0; i < numTiles; i++) {
            g.drawImage(sprite.getImage(), getX() + i * tileWidth, getY(), tileWidth, height, null);
        }
    }

    @Override
    public void collidedWith(Entity other) {
        // 충돌 로직 없음
    }
}
