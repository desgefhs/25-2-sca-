package org.newdawn.spaceinvaders.entity;

/**
 * 게임에 등장하는 다양한 적 또는 오브젝트의 타입을 정의하는 열거형.
 */
public enum EntityType {
    /** 일반 외계인. */
    ALIEN,
    /** 보스. */
    BOSS,
    /** 폭탄. */
    BOMB,
    /** 메테오 적. */
    METEOR_ENEMY,
    /** 점사 공격을 하는 적. */
    BURST_SHOOTER,
    /** 세 방향으로 공격하는 적. */
    THREE_WAY_SHOOTER,
    /** 플레이어가 파괴할 수 있는 메테오. */
    METEOR,
}
