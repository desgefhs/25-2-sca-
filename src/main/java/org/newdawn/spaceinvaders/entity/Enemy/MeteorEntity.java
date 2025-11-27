package org.newdawn.spaceinvaders.entity.Enemy;

import org.newdawn.spaceinvaders.core.GameContext;

import org.newdawn.spaceinvaders.entity.*;
import org.newdawn.spaceinvaders.entity.Effect.AnimatedExplosionEntity;
import org.newdawn.spaceinvaders.entity.Projectile.LaserBeamEntity;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileEntity;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileType;

/**
 * 게임 내에 등장하는 운석 엔티티.
 * 플레이어의 공격으로 파괴되거나, 함선과 충돌하여 피해를 줄 수 있습니다.
 * 크기에 따라 다른 체력과 점수 값을 가집니다.
 */
public class MeteorEntity extends Entity {

    /**
     * 운석의 종류를 정의하는 열거형.
     * 종류별로 스프라이트 경로와 최대 체력을 정의합니다.
     */
    public enum MeteorType {
        SMALL("sprites/meteors2.gif", 1),
        MEDIUM("sprites/meteors3.gif", 2),
        LARGE("sprites/meteors4.gif", 3);

        public final String spritePath;
        public final int maxHealth;

        MeteorType(String spritePath, int maxHealth) {
            this.spritePath = spritePath;
            this.maxHealth = maxHealth;
        }
    }

    private final GameContext context;
    /** 운석 파괴 시 획득할 점수 값. */
    private final int scoreValue;

    /**
     * 운석 파괴 시 획득할 점수 값을 반환합니다.
     * @return 점수 값
     */
    public int getScoreValue() {
        return scoreValue;
    }

    /**
     * MeteorEntity 생성자.
     * @param context 게임 컨텍스트
     * @param type 운석의 종류
     * @param x 초기 x 좌표
     * @param y 초기 y 좌표
     */
    public MeteorEntity(GameContext context, MeteorType type, int x, int y) {
        super(type.spritePath, x, y);
        this.context = context;
        this.health = new HealthComponent(this, type.maxHealth);
        this.scoreValue = type.maxHealth * 5; // 점수는 체력에 비례합니다.
        this.dy = (Math.random() * 50) + 50; // 50에서 100 사이의 무작위 하강 속도
    }

    /**
     * 다른 엔티티와의 충돌을 처리합니다.
     * 플레이어 발사체, 함선, 레이저 빔과의 충돌 로직을 포함합니다.
     * @param other 충돌한 다른 엔티티
     */
    @Override
    public void collidedWith(Entity other) {
        // 플레이어 발사체와의 충돌 처리
        if (other instanceof ProjectileEntity) {
            ProjectileEntity shot = (ProjectileEntity) other;

            // 플레이어의 발사체인지 확인 (타겟 타입이 ENEMY인 경우)
            if (shot.getType().targetType == ProjectileType.TargetType.ENEMY) {
                context.removeEntity(shot); // 충돌 시 발사체 제거

                // 발사체 손상과 관계없이 체력을 1씩 고정적으로 감소
                if (!health.decreaseHealth(1)) {
                    this.destroy(); // 운석 파괴
                    createExplosion(); // 폭발 효과 생성
                }
            }
        }
        // 플레이어 함선과의 충돌 처리
        else if (other instanceof ShipEntity) {
            ShipEntity ship = (ShipEntity) other;
            // 운석의 남은 체력만큼 플레이어에게 피해를 줍니다.
            if (!ship.getHealth().decreaseHealth(this.health.getCurrentHealth())) {
                ship.destroy();
            }
            this.destroy(); // 충돌 시 운석 파괴
        }
        // 레이저 빔과의 충돌 처리
        else if (other instanceof LaserBeamEntity) {
            this.destroy(); // 운석은 레이저에 의해 즉시 파괴됩니다.
            createExplosion(); // 폭발 효과 생성
        }
    }

    /**
     * 운석 파괴 시 애니메이션 폭발 효과를 생성합니다.
     */
    private void createExplosion() {
        AnimatedExplosionEntity explosion = new AnimatedExplosionEntity(context, 0, 0);
        explosion.setScale(0.1);
        int centeredX = this.getX() + (this.getWidth() / 2) - (explosion.getWidth() / 2);
        int centeredY = this.getY() + (this.getHeight() / 2) - (explosion.getHeight() / 2);
        explosion.setX(centeredX);
        explosion.setY(centeredY);
        context.addEntity(explosion);
    }

    /**
     * 이 엔티티는 업그레이드할 수 없습니다.
     */
    @Override
    public void upgrade() {
        // 이 엔티티는 업그레이드할 수 없습니다.
    }
}