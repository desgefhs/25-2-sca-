package org.newdawn.spaceinvaders.entity;

/**
 * 포메이션에서 생성될 수 있는 엔티티의 유형을 정의
 */
public enum EntityType {
    /** 일반 에일리언 */
    ALIEN,
    /** 보스 에일리언 */
    BOSS,
    /** 폭탄을 떨어뜨리는 에일리언 */
    BOMB,
    METEOR_ENEMY,
    /** 버스트 샷을 발사하는 에일리언 */
    BURST_SHOOTER,
    /** 3-Way 샷을 발사하는 에일리언 */
    THREE_WAY_SHOOTER,
    /** 운석 */
    METEOR,
}
