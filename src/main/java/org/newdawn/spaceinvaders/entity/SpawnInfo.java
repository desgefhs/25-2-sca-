package org.newdawn.spaceinvaders.entity;

public class SpawnInfo {
    public final EntityType entityType;
    public final int x;
    public final int y;
    public final MovementPattern movementPattern;
    public final double upgradeChance;
    public final boolean forceUpgrade;

    // Main constructor
    public SpawnInfo(EntityType entityType, int x, int y, MovementPattern movementPattern, double upgradeChance, boolean forceUpgrade) {
        this.entityType = entityType;
        this.x = x;
        this.y = y;
        this.movementPattern = movementPattern;
        this.upgradeChance = upgradeChance;
        this.forceUpgrade = forceUpgrade;
    }

    // Constructor without forceUpgrade (defaults to false)
    public SpawnInfo(EntityType entityType, int x, int y, MovementPattern movementPattern, double upgradeChance) {
        this(entityType, x, y, movementPattern, upgradeChance, false);
    }

    // Constructor for forced upgrade without specific pattern/chance
    public SpawnInfo(EntityType entityType, int x, int y, boolean forceUpgrade) {
        this(entityType, x, y, MovementPattern.STRAIGHT_DOWN, 0.0, forceUpgrade);
    }

    // Simple constructor
    public SpawnInfo(EntityType entityType, int x, int y) {
        this(entityType, x, y, MovementPattern.STRAIGHT_DOWN, 0.0, false);
    }
}
