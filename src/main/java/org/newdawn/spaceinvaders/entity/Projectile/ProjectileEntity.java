package org.newdawn.spaceinvaders.entity.Projectile;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.Entity;
import org.newdawn.spaceinvaders.entity.ShipEntity;

/**
 * 게임 내 모든 발사체(총알, 미사일 등)의 기본 엔티티.
 * 발사체는 타입, 피해량, 이동 속도, 그리고 추적 기능과 같은 속성을 가집니다.
 */
public class ProjectileEntity extends Entity {

    /** 게임 컨텍스트. */
    private final GameContext context;
    /** 발사체의 타입 정의. */
    private final ProjectileType type;
    /** 발사체가 주는 피해량. */
    private final int damage;

    /** 추적 미사일의 남은 추적 시간 (밀리초). */
    private long homingTimer;
    /** 발사체가 이미 사용(충돌)되었는지 여부. */
    private boolean used = false;

    /**
     * ProjectileEntity 생성자.
     * @param context 게임 컨텍스트
     * @param type 발사체의 타입 정의
     * @param damage 발사체가 주는 피해량
     * @param x 초기 x 좌표
     * @param y 초기 y 좌표
     * @param dx 초기 수평 속도
     * @param dy 초기 수직 속도
     */
    public ProjectileEntity(GameContext context, ProjectileType type, int damage, int x, int y, double dx, double dy) {
        super(type.spritePath, x, y);
        this.context = context;
        this.type = type;
        this.damage = damage;
        this.dx = dx;
        this.dy = dy;
        this.homingTimer = type.homingDuration;
        if (type == ProjectileType.FEATHER_SHOT) { // 특정 발사체는 크기 조절
            setScale(1.0);
        } else {
            setScale(1.5);
        }
    }

    /**
     * 초기 속도가 필요 없는 추적 발사체를 위한 생성자.
     * @param context 게임 컨텍스트
     * @param type 발사체의 타입 정의
     * @param damage 발사체가 주는 피해량
     * @param x 초기 x 좌표
     * @param y 초기 y 좌표
     */
    public ProjectileEntity(GameContext context, ProjectileType type, int damage, int x, int y) {
        this(context, type, damage, x, y, 0, 0);
    }

    /**
     * 발사체를 이동시키고, 추적 기능이 있다면 플레이어를 향해 방향을 조절합니다.
     * 화면 밖으로 나가면 자신을 제거합니다.
     * @param delta 마지막 업데이트 이후 경과 시간 (밀리초)
     */
    @Override
    public void move(long delta) {
        // 추적 로직
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
                    dx = (diffX / length) * type.moveSpeed;
                    dy = (diffY / length) * type.moveSpeed;
                }
            }
        }

        super.move(delta);

        // 스크린 밖으로 나가면 제거
        if (y < -100 || y > Game.GAME_HEIGHT + 100 || x < -100 || x > Game.GAME_WIDTH + 100) {
            context.removeEntity(this);
        }
    }

    /**
     * 다른 엔티티와의 충돌을 처리합니다.
     * 발사체의 타겟 타입에 따라 충돌 로직이 달라집니다.
     * @param other 충돌한 다른 엔티티
     */
    @Override
    public void collidedWith(Entity other) {
        if (used) { // 이미 충돌 처리된 발사체는 다시 처리하지 않음
            return;
        }

        // 플레이어 발사체는 플레이어가 아닌 체력을 가진 모든 엔티티와 충돌
        if (type.targetType == ProjectileType.TargetType.ENEMY) {
            if (!(other instanceof ShipEntity) && other.getHealth() != null) {
                context.removeEntity(this);
                used = true;
            }
        }

        // 적 발사체는 플레이어 함선과만 충돌
        if (type.targetType == ProjectileType.TargetType.PLAYER && other instanceof ShipEntity) {
            context.removeEntity(this);
            used = true;
        }
    }

    /**
     * 발사체가 주는 데미지를 반환합니다.
     * @return 데미지 값
     */
    public int getDamage() {
        return damage;
    }

    /**
     * 발사체의 타입을 반환합니다.
     * @return {@link ProjectileType}
     */
    public ProjectileType getType() {
        return type;
    }
}
