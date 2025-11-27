package org.newdawn.spaceinvaders.entity.Enemy;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;

import org.newdawn.spaceinvaders.entity.Effect.AnimatedExplosionEntity;
import org.newdawn.spaceinvaders.entity.Entity;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileEntity;
import org.newdawn.spaceinvaders.entity.ShipEntity;
import org.newdawn.spaceinvaders.graphics.Sprite;
import org.newdawn.spaceinvaders.graphics.SpriteStore;

import java.awt.Graphics;

public class BombEntity extends Entity implements Enemy {

    private enum State { APPROACHING, WARNING, EXPLODING }
    private State currentState = State.APPROACHING;

    private final GameContext context;
    private final double moveSpeed = 75; // 속도 절반으로 줄임

    // 경고 및 폭발 통계
    private static final long WARNING_DURATION = 1000L; // 1초
    private static final int WARNING_RANGE = 100; // 화면 높이(600)의 1/5
    private static final int EXPLOSION_RADIUS = 100;
    private static final int EXPLOSION_DAMAGE = 1;

    private long stateTimer = 0;

    // 경고 애니메이션
    private final Sprite[] warningFrames = new Sprite[13];
    private int currentWarningFrame = 0;
    private static final long WARNING_FRAME_DURATION = WARNING_DURATION / 13;

    public BombEntity(GameContext context, int x, int y) {
        super("sprites/enemy/bomb.gif", x, y);
        this.context = context;
        this.dy = moveSpeed;

        // 경고 애니메이션 프레임 미리 로드
        for (int i = 0; i < 13; i++) {
            String frame = (i < 10) ? "0" + i : String.valueOf(i);
            warningFrames[i] = SpriteStore.get().getSprite("sprites/radar/" + frame + ".png");
        }
    }

    @Override
    public void move(long delta) {
        super.move(delta);

        ShipEntity ship = context.getShip();
        if (ship == null) return;

        double distanceToShip = Math.sqrt(Math.pow(ship.getX() - x, 2) + Math.pow(ship.getY() - y, 2));

        switch (currentState) {
            case APPROACHING:
                if (distanceToShip <= WARNING_RANGE) {
                    currentState = State.WARNING;
                    stateTimer = WARNING_DURATION;
                }
                break;

            case WARNING:
                stateTimer -= delta;
                // 표시할 애니메이션 프레임 업데이트
                currentWarningFrame = (int) (((WARNING_DURATION - stateTimer) / (float) WARNING_DURATION) * 12);

                if (stateTimer <= 0) {
                    currentState = State.EXPLODING;
                }
                break;

            case EXPLODING:
                // 함선이 움직였을 경우를 대비해 거리 다시 확인
                if (distanceToShip <= EXPLOSION_RADIUS) {
                    if (!ship.getHealth().decreaseHealth(EXPLOSION_DAMAGE)) {
                        ship.destroy();
                    }
                }
                // 시각적 폭발 생성
                // 자신 파괴
                this.destroy();
                break;
        }
    }

    @Override
    public void draw(Graphics g) {
        super.draw(g);
        if (currentState == State.WARNING) {
            Sprite frame = warningFrames[currentWarningFrame];
            int diameter = EXPLOSION_RADIUS * 2;
            // 폭발 직경에 맞게 조정된 경고 스프라이트 그리기
            g.drawImage(frame.getImage(), (int) (x + (width/2) - (diameter/2)), (int) (y + (height/2) - (diameter/2)), diameter, diameter, null);
        }
    }

    @Override
    public void collidedWith(Entity other) {
        // 이 엔티티는 발사체에 의해 파괴될 수 없습니다.
        if (other instanceof ProjectileEntity) {
            return;
        }

        // 함선과 직접 충돌 시 피해를 주지 않습니다.
        if (other instanceof ShipEntity) {
        }
    }

    @Override
    public void upgrade() {
        // 이 엔티티는 업그레이드할 수 없습니다.
    }
}