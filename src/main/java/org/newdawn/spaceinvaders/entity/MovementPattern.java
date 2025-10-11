package org.newdawn.spaceinvaders.entity;

/**
 * 엔티티의 이동 패턴을 정의하는 열거형입니다.
 */
public enum MovementPattern {
    /** 직선으로 아래로 이동 */
    STRAIGHT_DOWN,
    /** 직선으로 위로 이동 */
    STRAIGHT_UP,
    /** 사인 곡선을 그리며 좌우로 이동 */
    SINUSOIDAL,
    /** 움직이지 않음 (정지) */
    STATIC,
    /** 수평으로 중앙까지 이동 후 정지 */
    HORIZ_TO_CENTER_AND_STOP
}
