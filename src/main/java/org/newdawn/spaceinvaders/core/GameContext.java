package org.newdawn.spaceinvaders.core;

import org.newdawn.spaceinvaders.entity.Entity;

/**
 * 엔티티들이 게임의 핵심 로직과 소통하기 위한 메소드 규칙을 정의하는 인터페이스.
 */
public interface GameContext {

    /**
     * 게임에 새로운 엔티티를 추가하도록 요청.
     * @param entity 추가할 엔티티
     */
    void addEntity(Entity entity);

    /**
     * 게임에서 특정 엔티티를 제거하도록 요청.
     * @param entity 제거할 엔티티
     */
    void removeEntity(Entity entity);

    /**
     * 플레이어가 사망했음을 알립니다.
     */
    void notifyDeath();

    /**
     * 플레이어가 승리했음을 알립니다.
     */
    void notifyWin();

    /**
     * 외계인이 화면 밖으로 탈출했음을 알립니다.
     * @param entity 탈출한 엔티티
     */
    void notifyAlienEscaped(Entity entity);

    /**
     * 외계인이 죽었음을 알립니다.
     */
    void notifyAlienKilled();

    /**
     * 외계인들이 화면 끝에 도달하여 로직 업데이트가 필요함을 알립니다.
     */
    void updateLogic();

    /**
     * 게임에 존재하는 모든 엔티티 목록을 가져옵니다.
     * @return 엔티티 목록
     */
    java.util.List<Entity> getEntities();
}
