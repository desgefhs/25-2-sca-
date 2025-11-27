package org.newdawn.spaceinvaders.entity.Enemy;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.Entity;
import org.newdawn.spaceinvaders.entity.ShipEntity;
import org.newdawn.spaceinvaders.graphics.Sprite;
import org.newdawn.spaceinvaders.graphics.SpriteStore;

import java.awt.Graphics;

/**
 * 보스의 '촉수 공격'과 같이 특정 위치에서 발생하는 시한부 공격 엔티티.
 * 경고 단계와 실제 공격 단계로 나뉘며, 플레이어 함선과 충돌 시 피해를 줍니다.
 */
public class TentacleAttackEntity extends Entity {

    /** 공격의 현재 상태를 정의하는 열거형. */
    private enum State {
        WARNING,    // 공격 전 경고 단계
        ATTACKING   // 실제 공격 단계
    }

    private State state = State.WARNING;
    /** 이 엔티티가 생성된 시간 (타임스탬프). */
    private final long startTime;
    /** 경고 단계의 지속 시간 (밀리초). */
    private final long warningDuration = 1000; // 1초 경고
    /** 공격 단계의 지속 시간 (밀리초). */
    private final long attackDuration = 500;   // 0.5초 공격
    /** 공격 시 주는 데미지. */
    private final int damage = 1;

    /** 경고 단계에서 사용될 스프라이트. */
    private final Sprite warningSprite;
    /** 공격 단계에서 사용될 스프라이트. */
    private final Sprite attackSprite;

    /**
     * TentacleAttackEntity 생성자.
     * @param context 게임 컨텍스트
     * @param x 초기 x 좌표
     * @param y 초기 y 좌표
     */
    public TentacleAttackEntity(GameContext context, int x, int y) {
        super("sprites/bosses/fireheart_target.png", x, y); // 초기 스프라이트는 경고 이미지입니다.
        this.context = context;
        this.startTime = System.currentTimeMillis();

        // 스프라이트 미리 로드
        this.warningSprite = this.sprite; // 슈퍼 생성자에서 설정된 초기 스프라이트
        this.attackSprite = SpriteStore.get().getSprite("sprites/bosses/fireheart_tentacle.png");
    }

    /**
     * 공격의 상태를 업데이트하고, 단계에 따라 스프라이트를 변경하며, 공격 종료 시 자신을 제거합니다.
     * @param delta 마지막 업데이트 이후 경과 시간 (밀리초)
     */
    @Override
    public void move(long delta) {
        long now = System.currentTimeMillis();
        long timeSinceStart = now - startTime;

        if (state == State.WARNING && timeSinceStart > warningDuration) {
            state = State.ATTACKING;
            this.sprite = attackSprite; // 스프라이트 변경
            // 촉수 스프라이트 크기가 다를 수 있으므로 위치 조정
            this.x = this.x + (warningSprite.getWidth() / 2) - (attackSprite.getWidth() / 2);
            this.y = this.y + (warningSprite.getHeight() / 2) - (attackSprite.getHeight() / 2);
        } else if (state == State.ATTACKING && timeSinceStart > warningDuration + attackDuration) {
            context.removeEntity(this); // 공격 종료 및 엔티티 제거
        }
    }

    /**
     * 다른 엔티티와의 충돌을 처리합니다.
     * 공격 단계에서 플레이어 함선과 충돌 시 함선에 피해를 줍니다.
     * @param other 충돌한 다른 엔티티
     */
    @Override
    public void collidedWith(Entity other) {
        if (state == State.ATTACKING && other instanceof ShipEntity) {
            ShipEntity ship = (ShipEntity) other;
            ship.getHealth().decreaseHealth(damage);
            // 촉수 공격은 지속 시간 동안 유지되므로 충돌 시 제거되지 않습니다.
        }
    }
}
