package org.newdawn.spaceinvaders.entity.Projectile;

/**
 * 다양한 발사체의 속성을 정의
 * 각 발사체는 고유한 스프라이트, 속도, 유도 시간, 타겟 유형을 가짐
 */
public enum ProjectileType {
    /** 플레이어 기본 발사체 */
    PLAYER_SHOT("sprites/shot/shot.gif", 300, 0, TargetType.ENEMY),
    /** 적 기본 발사체 */
    NORMAL_SHOT("sprites/shot/nomalshot.gif", 250, 0, TargetType.PLAYER),
    /** 빠른 적 기본 발사체 */
    FAST_NORMAL_SHOT("sprites/shot/fast_nomalshot.gif", 400, 0, TargetType.PLAYER),
    /** 느린 유도 발사체 */
    FOLLOWING_SHOT("sprites/shot/followingshot.gif", 150, 500, TargetType.PLAYER),
    /** 빠른 유도 발사체 */
    FAST_FOLLOWING_SHOT("sprites/shot/fast_followingshot.gif", 250, 500, TargetType.PLAYER),
    /** 플레이어 레이저 발사체 */
    LASER_SHOT("sprites/texture_laser.PNG", 600, 0, TargetType.ENEMY),
    /** 히드라 보스의 커튼 패턴 발사체 */
    HYDRA_CURTAIN("sprites/explosion.gif", 250, 0, TargetType.PLAYER),
    /** 그리핀 보스의 깃털 발사체 */
    FEATHER_SHOT("sprites/bosses/feather.png", 220, 0, TargetType.PLAYER);

    /**
     * 발사체의 타겟 유형을 정의
     */
    public enum TargetType {
        /** 플레이어를 타겟으로 함 */
        PLAYER,
        /** 적을 타겟으로 함 */
        ENEMY
    }

    public final String spritePath;
    /** 이동 속도 (pixels/sec) */
    public final double moveSpeed;
    /** 유도 지속 시간 (ms). 0이면 유도 기능 없음. */
    public final long homingDuration;
    /** 타겟 유형 (플레이어 또는 적) */
    public final TargetType targetType;

    ProjectileType(String spritePath, double moveSpeed, long homingDuration, TargetType targetType) {
        this.spritePath = spritePath;
        this.moveSpeed = moveSpeed;
        this.homingDuration = homingDuration;
        this.targetType = targetType;

    }
}