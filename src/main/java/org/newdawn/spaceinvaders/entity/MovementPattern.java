package org.newdawn.spaceinvaders.entity;

/**
 * 엔티티가 사용할 수 있는 다양한 이동 패턴을 정의하는 열거형.
 */
public enum MovementPattern {
    /** 엔티티가 수직으로 아래로만 이동합니다. */
    STRAIGHT_DOWN,
    /** 엔티티가 수직으로 위로만 이동합니다. */
    STRAIGHT_UP,
    /** 엔티티가 사인 곡선 형태로 이동합니다. */
    SINUSOIDAL,
    /** 엔티티가 움직이지 않고 고정되어 있습니다. */
    STATIC,
    /** 엔티티가 수평으로 중앙으로 이동한 후 멈춥니다. */
    HORIZ_TO_CENTER_AND_STOP
}
