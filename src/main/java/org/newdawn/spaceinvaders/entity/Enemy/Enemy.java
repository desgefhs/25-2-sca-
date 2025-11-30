package org.newdawn.spaceinvaders.entity.Enemy;

/**
 * 게임 내에 등장하는 모든 적 엔티티가 구현해야 하는 공통 인터페이스입니다.
 * 적 엔티티의 핵심적인 행동(예: 업그레이드)을 정의합니다.
 */
public interface Enemy {
    /**
     * 적 엔티티를 업그레이드합니다.
     * 적의 종류에 따라 체력, 공격 패턴, 이동 속도 등이 변경될 수 있습니다.
     */
    void upgrade();
}
