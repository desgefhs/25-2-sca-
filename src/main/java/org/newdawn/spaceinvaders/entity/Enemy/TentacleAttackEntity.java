package org.newdawn.spaceinvaders.entity.Enemy;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.Entity;
import org.newdawn.spaceinvaders.entity.ShipEntity;
import org.newdawn.spaceinvaders.graphics.Sprite;
import org.newdawn.spaceinvaders.graphics.SpriteStore;

import java.awt.Graphics;

public class TentacleAttackEntity extends Entity {

    private enum State {
        WARNING,
        ATTACKING
    }

    private State state = State.WARNING;
    private final long startTime;
    private final long warningDuration = 1000; // 1초 경고
    private final long attackDuration = 500;   // 0.5초 공격
    private final int damage = 1;

    private final Sprite warningSprite;
    private final Sprite attackSprite;

    public TentacleAttackEntity(GameContext context, int x, int y) {
        super("sprites/bosses/fireheart_target.png", x, y); // 초기 스프라이트는 경고입니다.
        this.context = context;
        this.startTime = System.currentTimeMillis();

        // 스프라이트 미리 로드
        this.warningSprite = this.sprite; // 슈퍼 생성자에서
        this.attackSprite = SpriteStore.get().getSprite("sprites/bosses/fireheart_tentacle.png");
    }

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
            context.removeEntity(this); // 공격 종료
        }
    }

    @Override
    public void collidedWith(Entity other) {
        if (state == State.ATTACKING && other instanceof ShipEntity) {
            ShipEntity ship = (ShipEntity) other;
            ship.getHealth().decreaseHealth(damage);
            // 촉수 공격은 지속 시간 동안 유지되므로 충돌 시 제거되지 않습니다.
        }
    }
}
