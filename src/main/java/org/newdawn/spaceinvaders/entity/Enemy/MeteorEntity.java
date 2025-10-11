package org.newdawn.spaceinvaders.entity.Enemy;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.*;
import org.newdawn.spaceinvaders.entity.Effect.AnimatedExplosionEntity;
import org.newdawn.spaceinvaders.entity.Projectile.LaserBeamEntity;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileEntity;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileType;

/**
 * 게임의 장애물 역할을 하는 운석 엔티티
 * 크기(SMALL, MEDIUM, LARGE)에 따라 다른 체력과 점수를 가짐
 */
public class MeteorEntity extends Entity {

    /**
     * 운석의 유형을 정의
     */
    public enum MeteorType {
        /** 작은 운석 */
        SMALL("sprites/meteors2.gif", 1),
        /** 중간 운석 */
        MEDIUM("sprites/meteors3.gif", 2),
        /** 큰 운석 */
        LARGE("sprites/meteors4.gif", 3);

        /** 스프라이트 경로 */
        public final String spritePath;
        /** 최대 체력 */
        public final int maxHealth;

        MeteorType(String spritePath, int maxHealth) {
            this.spritePath = spritePath;
            this.maxHealth = maxHealth;
        }
    }

    private final GameContext context;
    /** 파괴 시 획득하는 점수 */
    private final int scoreValue;

    /**
     * MeteorEntity 객체를 생성.
     *
     * @param context 게임 컨텍스트
     * @param type    운석의 유형
     * @param x       x 좌표
     * @param y       y 좌표
     */
    public MeteorEntity(GameContext context, MeteorType type, int x, int y) {
        super(type.spritePath, x, y);
        this.context = context;
        this.health = new HealthComponent(this, type.maxHealth);
        this.scoreValue = type.maxHealth * 5; // 점수는 체력에 비례
        this.dy = (Math.random() * 50) + 50; // 50에서 100 사이의 무작위 하강 속도
    }

    @Override
    public void collidedWith(Entity other) {
        // 플레이어 발사체와 충돌 처리
        if (other instanceof ProjectileEntity) {
            ProjectileEntity shot = (ProjectileEntity) other;
            if (shot.getType().targetType == ProjectileType.TargetType.ENEMY) {
                context.removeEntity(shot); // 충돌 시 발사체 제거
                // 발사체 데미지와 무관하게 체력 1 감소
                if (!health.decreaseHealth(1)) {
                    destroyAndExplode();
                }
            }
        }
        // 플레이어 레이저 빔과 충돌 처리
        else if (other instanceof LaserBeamEntity) {
            destroyAndExplode(); // 레이저에 닿으면 즉시 파괴
        }
        // 플레이어 우주선과 충돌 처리
        else if (other instanceof ShipEntity) {
            // 운석의 남은 체력만큼 플레이어에게 데미지
            ShipEntity ship = (ShipEntity) other;
            ship.getHealth().decreaseHealth(this.health.getCurrentHealth());
            context.removeEntity(this); // 충돌 시 운석 파괴
        }
    }

    /**
     * 운석을 파괴하고 폭발 효과를 생성
     */
    private void destroyAndExplode() {
        context.removeEntity(this);
        context.notifyMeteorDestroyed(this.scoreValue);

        // 중앙에 폭발 효과 생성
        AnimatedExplosionEntity explosion = new AnimatedExplosionEntity(context, 0, 0);
        explosion.setScale(0.1);
        int centeredX = this.getX() + (this.getWidth() / 2) - (explosion.getWidth() / 2);
        int centeredY = this.getY() + (this.getHeight() / 2) - (explosion.getHeight() / 2);
        explosion.setX(centeredX);
        explosion.setY(centeredY);
        context.addEntity(explosion);
    }
}
