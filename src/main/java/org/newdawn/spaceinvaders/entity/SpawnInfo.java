package org.newdawn.spaceinvaders.entity;

public class SpawnInfo {
    public final EntityType entityType;
    public final int x;
    public final int y;
    public final MovementPattern movementPattern;
    public final double upgradeChance;
    public final boolean forceUpgrade;

    // 메인 생성자
    public SpawnInfo(EntityType entityType, int x, int y, MovementPattern movementPattern, double upgradeChance, boolean forceUpgrade) {
        this.entityType = entityType;
        this.x = x;
        this.y = y;
        this.movementPattern = movementPattern;
        this.upgradeChance = upgradeChance;
        this.forceUpgrade = forceUpgrade;
    }

    // forceUpgrade가 없는 생성자 (기본값 false)
    public SpawnInfo(EntityType entityType, int x, int y, MovementPattern movementPattern, double upgradeChance) {
        this(entityType, x, y, movementPattern, upgradeChance, false);
    }

    // 특정 패턴/확률 없이 강제 업그레이드하는 생성자
    public SpawnInfo(EntityType entityType, int x, int y, boolean forceUpgrade) {
        this(entityType, x, y, MovementPattern.STRAIGHT_DOWN, 0.0, forceUpgrade);
    }

    // 간단한 생성자
    public SpawnInfo(EntityType entityType, int x, int y) {
        this(entityType, x, y, MovementPattern.STRAIGHT_DOWN, 0.0, false);
    }
}
