package org.newdawn.spaceinvaders.entity;

public class SpawnInfo {
    public final EntityType entityType;
    public final int x;
    public final int y;
    public final MovementPattern movementPattern;
    public final double upgradeChance;

    public SpawnInfo(EntityType entityType, int x, int y, MovementPattern movementPattern, double upgradeChance) {
        this.entityType = entityType;
        this.x = x;
        this.y = y;
        this.movementPattern = movementPattern;
        this.upgradeChance = upgradeChance;
    }

    public SpawnInfo(EntityType entityType, int x, int y) {
        this(entityType, x, y, MovementPattern.STRAIGHT_DOWN, 0.0);
    }
}
