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

/**
 * 플레이어에게 접근하여 경고 후 폭발하는 특수한 적 엔티티.
 * 체력을 가지고 있지 않으며, 특정 상태 머신에 따라 동작합니다.
 */
public class BombEntity extends Entity implements Enemy {

    /** 폭탄의 현재 상태를 정의하는 열거형. */
    private enum State { APPROACHING, WARNING, EXPLODING }
    private State currentState = State.APPROACHING;

    /** 게임 컨텍스트. */
    private final GameContext context;
    /** 폭탄의 이동 속도. */
    private final double moveSpeed = 75; // 속도 절반으로 줄임

    // 경고 및 폭발 관련 상수
    private static final long WARNING_DURATION = 1000L; // 경고 지속 시간 1초
    private static final int WARNING_RANGE = 100; // 함선과의 거리가 이 범위 안에 들어오면 경고 시작
    private static final int EXPLOSION_RADIUS = 100; // 폭발 반경
    private static final int EXPLOSION_DAMAGE = 1; // 폭발 데미지

    /** 현재 상태의 타이머. */
    private long stateTimer = 0;

    // 경고 애니메이션 관련 필드
    private final Sprite[] warningFrames = new Sprite[13];
    private int currentWarningFrame = 0;
    private static final long WARNING_FRAME_DURATION = WARNING_DURATION / 13;

    /**
     * BombEntity 생성자.
     * @param context 게임 컨텍스트
     * @param x 초기 x 좌표
     * @param y 초기 y 좌표
     */
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

    /**
     * 폭탄의 상태 머신을 업데이트합니다.
     * 접근, 경고, 폭발 상태에 따라 다른 로직을 수행합니다.
     * @param delta 마지막 업데이트 이후 경과 시간 (밀리초)
     */
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
                context.addEntity(new AnimatedExplosionEntity(context, (int)x, (int)y));
                this.destroy(); // 자신 파괴
                break;
        }
    }

    /**
     * 폭탄과 경고 애니메이션을 그립니다.
     * @param g 그래픽 컨텍스트
     */
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

    /**
     * 다른 엔티티와의 충돌을 처리합니다.
     * 폭탄 엔티티는 발사체에 의해 파괴되지 않으며, 함선과 직접 충돌 시에도 데미지를 주지 않습니다.
     * @param other 충돌한 다른 엔티티
     */
    @Override
    public void collidedWith(Entity other) {
        // 이 엔티티는 발사체에 의해 파괴될 수 없습니다.
        if (other instanceof ProjectileEntity) {
            return;
        }

        // 함선과 직접 충돌 시 피해를 주지 않습니다. (로직은 move()의 EXPLODING 상태에서 처리)
        if (other instanceof ShipEntity) {
        }
    }

    /**
     * 이 엔티티는 업그레이드할 수 없습니다.
     */
    @Override
    public void upgrade() {
        // 이 엔티티는 업그레이드할 수 없습니다.
    }
}