package org.newdawn.spaceinvaders.entity;

/**
 * 엔티티 생성을 위해 필요한 모든 정보를 담는 데이터 클래스
 * 포메이션(Formation) 클래스에서 이 정보를 사용하여 적들을 배치
 */
public class SpawnInfo {
    /** 생성할 엔티티의 유형 */
    public final EntityType entityType;
    /** 생성될 x 좌표 */
    public final int x;
    /** 생성될 y 좌표 */
    public final int y;
    /** 적용할 이동 패턴 */
    public final MovementPattern movementPattern;
    /** 이 엔티티가 강화될 확률 (0.0 ~ 1.0) */
    public final double upgradeChance;
    /** 이 엔티티를 강제로 강화할지 여부 */
    public final boolean forceUpgrade;

    /**
     * 모든 정보를 포함하는 주 생성자
     *
     * @param entityType      엔티티 유형
     * @param x               x 좌표
     * @param y               y 좌표
     * @param movementPattern 이동 패턴
     * @param upgradeChance   강화 확률
     * @param forceUpgrade    강제 강화 여부
     */
    public SpawnInfo(EntityType entityType, int x, int y, MovementPattern movementPattern, double upgradeChance, boolean forceUpgrade) {
        this.entityType = entityType;
        this.x = x;
        this.y = y;
        this.movementPattern = movementPattern;
        this.upgradeChance = upgradeChance;
        this.forceUpgrade = forceUpgrade;
    }

    /**
     * 강제 강화를 false로 기본 설정하는 생성자
     */
    public SpawnInfo(EntityType entityType, int x, int y, MovementPattern movementPattern, double upgradeChance) {
        this(entityType, x, y, movementPattern, upgradeChance, false);
    }

    /**
     * 특정 패턴이나 확률 없이 강제 강화 여부
     */
    public SpawnInfo(EntityType entityType, int x, int y, boolean forceUpgrade) {
        this(entityType, x, y, MovementPattern.STRAIGHT_DOWN, 0.0, forceUpgrade);
    }

    /**
     * 가장 기본적인 정보(타입, 위치)만으로 생성하는 생성자
     * 이동 패턴은 STRAIGHT_DOWN, 강화 관련 값은 기본값으로 설정
     */
    public SpawnInfo(EntityType entityType, int x, int y) {
        this(entityType, x, y, MovementPattern.STRAIGHT_DOWN, 0.0, false);
    }
}
