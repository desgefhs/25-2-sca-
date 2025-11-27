package org.newdawn.spaceinvaders.entity.Projectile;

/**
 * 게임 내에서 사용되는 다양한 발사체(Projectile)의 타입을 정의하는 열거형.
 * 각 발사체 타입은 고유한 스프라이트, 이동 속도, 추적 시간, 타겟 종류 등의 속성을 가집니다.
 */
public enum ProjectileType {
    /** 플레이어가 발사하는 기본 총알. */
    PLAYER_SHOT("sprites/shot/shot.gif", 300, 0, TargetType.ENEMY),
    /** 적이 발사하는 일반 총알. */
    NORMAL_SHOT("sprites/shot/nomalshot.gif", 250, 0, TargetType.PLAYER),
    /** 적이 발사하는 빠른 일반 총알. */
    FAST_NORMAL_SHOT("sprites/shot/fast_nomalshot.gif", 400, 0, TargetType.PLAYER),
    /** 적이 발사하는 플레이어 추적 총알. */
    FOLLOWING_SHOT("sprites/shot/followingshot.gif", 150, 500, TargetType.PLAYER),
    /** 적이 발사하는 빠른 플레이어 추적 총알. */
    FAST_FOLLOWING_SHOT("sprites/shot/fast_followingshot.gif", 250, 500, TargetType.PLAYER),
    /** 레이저 무기에서 발사되는 레이저. */
    LASER_SHOT("sprites/texture_laser.PNG", 600, 0, TargetType.ENEMY),
    /** 히드라 보스의 커튼 공격 발사체. */
    HYDRA_CURTAIN("sprites/explosion.gif", 250, 0, TargetType.PLAYER), // 임시 스프라이트
    /** 그리핀 보스의 깃털 발사체. */
    FEATHER_SHOT("sprites/bosses/feather.png", 220, 0, TargetType.PLAYER);

    /**
     * 발사체가 타겟으로 삼을 수 있는 엔티티의 종류를 정의하는 열거형.
     */
    public enum TargetType {
        PLAYER, ENEMY
    }

    /** 발사체의 스프라이트 이미지 경로. */
    public final String spritePath;
    /** 발사체의 이동 속도 (픽셀/초). */
    public final double moveSpeed;
    /** 발사체의 추적(homing) 지속 시간 (밀리초). 0이면 추적 기능 없음. */
    public final long homingDuration;
    /** 발사체가 타겟으로 삼는 엔티티의 종류. */
    public final TargetType targetType;

    /**
     * ProjectileType 생성자.
     * @param spritePath 발사체 스프라이트의 리소스 경로
     * @param moveSpeed 발사체의 이동 속도
     * @param homingDuration 발사체의 추적 지속 시간
     * @param targetType 발사체가 타겟으로 삼는 엔티티의 종류
     */
    ProjectileType(String spritePath, double moveSpeed, long homingDuration, TargetType targetType) {
        this.spritePath = spritePath;
        this.moveSpeed = moveSpeed;
        this.homingDuration = homingDuration;
        this.targetType = targetType;
    }
}