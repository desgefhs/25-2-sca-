package org.newdawn.spaceinvaders.entity.Projectile;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.Entity;
import org.newdawn.spaceinvaders.entity.ShipEntity;

/**
 * 게임에 사용되는 모든 발사체를 나타내는 일반 클래스
 * 발사체의 유형(ProjectileType)에 따라 외형, 이동, 유도 기능 등이 결정
 */
public class ProjectileEntity extends Entity {

    private final GameContext context;
    /** 발사체 유형 */
    private final ProjectileType type;
    /** 발사체 데미지 */
    private final int damage;

    /** 유도 기능 남은 시간 */
    private long homingTimer;
    /** 충돌 처리 여부 (중복 충돌 방지용) */
    private boolean used = false;

    /**
     * ProjectileEntity 객체를 생성
     *
     * @param context 게임 컨텍스트
     * @param type    발사체 유형
     * @param damage  데미지
     * @param x       초기 x 좌표
     * @param y       초기 y 좌표
     * @param dx      초기 수평 속도
     * @param dy      초기 수직 속도
     */
    public ProjectileEntity(GameContext context, ProjectileType type, int damage, int x, int y, double dx, double dy) {
        super(type.spritePath, x, y);
        this.context = context;
        this.type = type;
        this.damage = damage;
        this.dx = dx;
        this.dy = dy;
        this.homingTimer = type.homingDuration;
        setScale(type == ProjectileType.FEATHER_SHOT ? 1.0 : 1.5);
    }

    /**
     * 초기 속도가 필요 없는 유도 발사체를 위한 생성자
     */
    public ProjectileEntity(GameContext context, ProjectileType type, int damage, int x, int y) {
        this(context, type, damage, x, y, 0, 0);
    }

    @Override
    public void move(long delta) {
        // 유도 로직
        if (homingTimer > 0) {
            homingTimer -= delta;
            ShipEntity ship = context.getShip();
            if (ship != null) {
                double targetX = ship.getX();
                double targetY = ship.getY();
                double diffX = targetX - x;
                double diffY = targetY - y;
                double length = Math.sqrt(diffX * diffX + diffY * diffY);
                if (length > 0) {
                    // 플레이어를 향해 방향 벡터를 계산하여 속도 업데이트
                    dx = (diffX / length) * type.moveSpeed;
                    dy = (diffY / length) * type.moveSpeed;
                }
            }
        }

        super.move(delta);

        // 화면 밖으로 나가면 제거
        if (y < -100 || y > Game.GAME_HEIGHT + 100 || x < -100 || x > Game.GAME_WIDTH + 100) {
            context.removeEntity(this);
        }
    }

    /**
     * 다른 엔티티와 충돌했을 때 호출
     * 발사체의 타겟 유형에 따라 충돌을 처리하고, 한 번 충돌하면 사라짐
     *
     * @param other 충돌한 다른 엔티티
     */
    @Override
    public void collidedWith(Entity other) {
        if (used) {
            return; // 이미 사용된 발사체는 충돌 처리 안 함
        }

        // 플레이어의 발사체는 적과 충돌
        if (type.targetType == ProjectileType.TargetType.ENEMY) {
            if (!(other instanceof ShipEntity) && other.getHealth() != null) {
                context.removeEntity(this);
                used = true;
            }
        }

        // 적의 발사체는 플레이어와 충돌
        if (type.targetType == ProjectileType.TargetType.PLAYER && other instanceof ShipEntity) {
            context.removeEntity(this);
            used = true;
        }
    }

    public int getDamage() {
        return damage;
    }

    public ProjectileType getType() {
        return type;
    }
}
