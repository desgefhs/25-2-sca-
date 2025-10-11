package org.newdawn.spaceinvaders.entity.boss;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.Entity;
import org.newdawn.spaceinvaders.entity.ShipEntity;
import org.newdawn.spaceinvaders.graphics.Sprite;
import org.newdawn.spaceinvaders.graphics.SpriteStore;

/**
 * 보스의 촉수 공격을 나타내는 엔티티
 * 경고(WARNING) 상태와 공격(ATTACKING) 상태를 가지며,
 * 경고 표시 후 잠시 동안 공격 판정이 생김
 */
public class TentacleAttackEntity extends Entity {

    /** 공격 상태 (경고, 공격) */
    private enum State {
        WARNING,
        ATTACKING
    }

    private State state = State.WARNING;
    /** 상태 시작 시간 */
    private long startTime;
    /** 경고 지속 시간 (1초) */
    private final long warningDuration = 1000;
    /** 공격 지속 시간 (0.5초) */
    private final long attackDuration = 500;
    /** 공격 데미지 */
    private final int damage = 1;

    private Sprite warningSprite;
    private Sprite attackSprite;

    /**
     * TentacleAttackEntity 객체를 생성
     *
     * @param context 게임 컨텍스트
     * @param x       x 좌표
     * @param y       y 좌표
     */
    public TentacleAttackEntity(GameContext context, int x, int y) {
        super("sprites/bosses/fireheart_target.png", x, y); // 초기 스프라이트는 경고 표시
        this.context = context;
        this.startTime = System.currentTimeMillis();

        // 스프라이트 미리 로드
        this.warningSprite = this.sprite;
        this.attackSprite = SpriteStore.get().getSprite("sprites/bosses/fireheart_tentacle.png");
    }

    @Override
    public void move(long delta) {
        long now = System.currentTimeMillis();
        long timeSinceStart = now - startTime;

        // 경고 시간이 지나면 공격 상태로 전환
        if (state == State.WARNING && timeSinceStart > warningDuration) {
            state = State.ATTACKING;
            this.sprite = attackSprite; // 스프라이트 변경
            // 경고와 공격 스프라이트 크기 차이에 따른 위치 보정
            this.x = this.x + (warningSprite.getWidth() / 2) - (attackSprite.getWidth() / 2);
            this.y = this.y + (warningSprite.getHeight() / 2) - (attackSprite.getHeight() / 2);
        } 
        // 공격 시간이 지나면 엔티티 제거
        else if (state == State.ATTACKING && timeSinceStart > warningDuration + attackDuration) {
            context.removeEntity(this);
        }
    }

    /**
     * 다른 엔티티와 충돌했을 때 호출
     * 공격 상태에서 플레이어와 충돌 시 데미지를 줌
     *
     * @param other 충돌한 다른 엔티티
     */
    @Override
    public void collidedWith(Entity other) {
        if (state == State.ATTACKING && other instanceof ShipEntity) {
            ShipEntity ship = (ShipEntity) other;
            ship.getHealth().decreaseHealth(damage);
            // 촉수 공격은 지속 시간 동안 유지되므로 충돌 시 제거되지 않음
        }
    }
}