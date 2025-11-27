package org.newdawn.spaceinvaders.entity.Enemy;

import org.newdawn.spaceinvaders.core.GameContext;

import org.newdawn.spaceinvaders.entity.*;
import org.newdawn.spaceinvaders.entity.Effect.AnimatedExplosionEntity;
import org.newdawn.spaceinvaders.entity.Projectile.LaserBeamEntity;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileEntity;
import org.newdawn.spaceinvaders.entity.Projectile.ProjectileType;

public class MeteorEntity extends Entity {

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
    private final int scoreValue;

    public int getScoreValue() {
        return scoreValue;
    }

    public MeteorEntity(GameContext context, MeteorType type, int x, int y) {
        super(type.spritePath, x, y);
        this.context = context;
        this.health = new HealthComponent(this, type.maxHealth);
        this.scoreValue = type.maxHealth * 5; // 점수는 체력에 비례합니다.
        this.dy = (Math.random() * 50) + 50; // 50에서 100 사이의 무작위 하강 속도
    }

    @Override
    public void collidedWith(Entity other) {
        // 플레이어 발사체와의 충돌 확인
        if (other instanceof ProjectileEntity) {
            ProjectileEntity shot = (ProjectileEntity) other;

            // 플레이어의 발사체인지 확인
            if (shot.getType().targetType == ProjectileType.TargetType.ENEMY) {
                // 충돌 시 발사체 제거
                context.removeEntity(shot);

                // 발사체 손상과 관계없이 체력을 1씩 고정적으로 감소
                if (!health.decreaseHealth(1)) {
                    // 이 운석은 파괴됩니다.
                    this.destroy();

                    // 크기가 조절되고 중앙에 위치한 폭발 효과 생성
                    AnimatedExplosionEntity explosion = new AnimatedExplosionEntity(context, 0, 0);
                    explosion.setScale(0.1);
                    int centeredX = this.getX() + (this.getWidth() / 2) - (explosion.getWidth() / 2);
                    int centeredY = this.getY() + (this.getHeight() / 2) - (explosion.getHeight() / 2);
                    explosion.setX(centeredX);
                    explosion.setY(centeredY);
                    context.addEntity(explosion);
                }
            }
        }

        // 선택 사항: 플레이어 함선과의 충돌 처리
        if (other instanceof ShipEntity) {
            // 운석의 남은 체력만큼 플레이어에게 피해를 줍니다.
            ShipEntity ship = (ShipEntity) other;
            if (!ship.getHealth().decreaseHealth(this.health.getCurrentHealth())) {
                ship.destroy();
            }

            // 충돌 시 운석 파괴
            this.destroy();
        } else if (other instanceof LaserBeamEntity) {
            // 운석은 현재 레이저에 의해 즉시 파괴됩니다.
            this.destroy();

            // 크기가 조절되고 중앙에 위치한 폭발 효과 생성
            AnimatedExplosionEntity explosion = new AnimatedExplosionEntity(context, 0, 0);
            explosion.setScale(0.1);
            int centeredX = this.getX() + (this.getWidth() / 2) - (explosion.getWidth() / 2);
            int centeredY = this.getY() + (this.getHeight() / 2) - (explosion.getHeight() / 2);
            explosion.setX(centeredX);
            explosion.setY(centeredY);
            context.addEntity(explosion);
        }
    }
}