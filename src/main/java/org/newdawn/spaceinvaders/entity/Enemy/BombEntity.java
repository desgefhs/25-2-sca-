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
 * 플레이어에게 접근하여 자폭하는 적 엔티티.
 * 일정 범위 내로 접근하면 경고 애니메이션을 표시한 후 폭발하여 주변에 피해를 줌
 */
public class BombEntity extends Entity implements Enemy {

    /** 폭탄의 상태 (접근 중, 경고, 폭발) */
    private enum State { APPROACHING, WARNING, EXPLODING }
    private State currentState = State.APPROACHING;
    private GameContext context;
    /** 이동 속도 */
    private double moveSpeed = 75;

    // 경고 및 폭발 관련 상수
    /** 경고 지속 시간 (1초) */
    private static final long WARNING_DURATION = 1000L;
    /** 경고 상태로 전환되는 범위 */
    private static final int WARNING_RANGE = 100;
    /** 폭발 반경 */
    private static final int EXPLOSION_RADIUS = 100;
    /** 폭발 데미지 */
    private static final int EXPLOSION_DAMAGE = 1;

    /** 상태 지속 시간 타이머 */
    private long stateTimer = 0;

    // 경고 애니메이션

    private Sprite[] warningFrames = new Sprite[13];
    private int currentWarningFrame = 0;
    /** 경고 애니메이션 각 프레임의 지속 시간 */
    private static final long WARNING_FRAME_DURATION = WARNING_DURATION / 13;

    /**
     * BombEntity 객체를 생성
     *
     * @param context 게임 컨텍스트
     * @param x       x 좌표
     * @param y       y 좌표
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

    @Override
    public void move(long delta) {
        super.move(delta);

        ShipEntity ship = context.getShip();
        if (ship == null) return;

        double distanceToShip = Math.sqrt(Math.pow(ship.getX() - x, 2) + Math.pow(ship.getY() - y, 2));

        switch (currentState) {
            case APPROACHING:
                // 플레이어와의 거리가 경고 범위 내로 들어오면 경고 상태로 전환
                if (distanceToShip <= WARNING_RANGE) {
                    currentState = State.WARNING;
                    stateTimer = WARNING_DURATION;
                }
                break;

            case WARNING:
                stateTimer -= delta;
                // 경과 시간에 따라 표시할 애니메이션 프레임 업데이트
                currentWarningFrame = (int) (((WARNING_DURATION - stateTimer) / (float) WARNING_DURATION) * 12);

                // 경고 시간이 끝나면 폭발 상태로 전환
                if (stateTimer <= 0) {
                    currentState = State.EXPLODING;
                }
                break;

            case EXPLODING:
                // 폭발 시점에 플레이어가 폭발 반경 내에 있는지 다시 확인
                if (distanceToShip <= EXPLOSION_RADIUS) {
                    if (!ship.getHealth().decreaseHealth(EXPLOSION_DAMAGE)) {
                        context.notifyDeath();
                    }
                }
                // 자신을 제거하고 처치 알림
                context.notifyAlienKilled();
                context.removeEntity(this);
                break;
        }
    }

    @Override
    public void draw(Graphics g) {
        super.draw(g);
        // 경고 상태일 때 경고 애니메이션 그리기
        if (currentState == State.WARNING) {
            Sprite frame = warningFrames[currentWarningFrame];
            int diameter = EXPLOSION_RADIUS * 2;
            // 폭발 반경에 맞춰 경고 스프라이트 크기 조절하여 그리기
            g.drawImage(frame.getImage(), (int) (x + (width/2) - (diameter/2)), (int) (y + (height/2) - (diameter/2)), diameter, diameter, null);
        }
    }

    /**
     * 이 엔티티는 발사체에 의해 파괴되지 않고, 플레이어와 직접 충돌해도 데미지를 주지 않음
     *
     * @param other 충돌한 다른 엔티티
     */
    @Override
    public void collidedWith(Entity other) {
        // 발사체에 맞지 않음
        if (other instanceof ProjectileEntity) {
            return;
        }

        // 플레이어와 직접 충돌 시 데미지 없음
        if (other instanceof ShipEntity) {
            return;
        }
    }

    @Override
    public void upgrade() {
        // 이 엔티티는 강화될 수 없음
    }
}
