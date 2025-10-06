package org.newdawn.spaceinvaders.entity;

//총알의 속성, 설정값들 모아둠
public enum ProjectileType {
    // 타입(이미지 경로, 이동속도, 유도시간, 타겟 종류 )
    PLAYER_SHOT("sprites/shot/shot.gif", 300, 0, TargetType.ENEMY),
    NORMAL_SHOT("sprites/shot/nomalshot.gif", 250, 0, TargetType.PLAYER),
    FAST_NORMAL_SHOT("sprites/shot/fast_nomalshot.gif", 400, 0, TargetType.PLAYER),
    FOLLOWING_SHOT("sprites/shot/followingshot.gif", 150, 500, TargetType.PLAYER),
    FAST_FOLLOWING_SHOT("sprites/shot/fast_followingshot.gif", 250, 500, TargetType.PLAYER),
    LASER_SHOT("sprites/texture_laser.PNG", 600, 0, TargetType.ENEMY),
    HYDRA_CURTAIN("sprites/explosion.gif", 250, 0, TargetType.PLAYER),
    FEATHER_SHOT("sprites/projectiles/feather.png", 220, 0, TargetType.PLAYER);

    public enum TargetType {
        PLAYER, ENEMY
    }

    public final String spritePath;
    public final double moveSpeed;
    public final long homingDuration;
    public final TargetType targetType;

    ProjectileType(String spritePath, double moveSpeed, long homingDuration, TargetType targetType) {
        this.spritePath = spritePath;
        this.moveSpeed = moveSpeed;
        this.homingDuration = homingDuration;
        this.targetType = targetType;

    }
}