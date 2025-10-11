package org.newdawn.spaceinvaders.entity.Enemy;

/**
 * 게임에 등장하는 모든 '적' 유형의 엔티티가 구현해야 하는 인터페이스
 * 적의 공통적인 동작을 정의
 */
public interface Enemy {
    /**
     * 적을 강화 상태로 만듬
     * 구현하는 클래스는 이 메서드가 호출되었을 때 공격 패턴, 체력, 외형 등을 변경할 수 있음
     */
    void upgrade();
}
