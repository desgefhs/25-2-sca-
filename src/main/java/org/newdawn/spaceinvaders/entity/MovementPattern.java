package org.newdawn.spaceinvaders.entity;

public enum MovementPattern {
    STATIC, // 생성 위치에 고정
    STRAIGHT_DOWN, // 직선으로 아래로 이동
    SINUSOIDAL, // 사인파 형태로 좌우로 흔들리며 아래로 이동
    PLAYER_TRACKING // 플레이어의 x좌표를 따라 아래로 이동
}
