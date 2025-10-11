package org.newdawn.spaceinvaders.player;

/**
 * 게임에 존재하는 버프의 유형을 정의
 */
public enum BuffType {
    /** 무적: 모든 피해를 받지 않음 */
    INVINCIBILITY,
    /** 속도 증가: 이동 속도 증가 */
    SPEED_BOOST,
    /** 치유: 체력을 즉시 회복 */
    HEAL,
    /** 공격력 증가: 공격력 및 발사 속도 증가 */
    DAMAGE_BOOST
}
