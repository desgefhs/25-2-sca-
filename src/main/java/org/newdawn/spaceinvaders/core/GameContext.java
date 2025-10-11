package org.newdawn.spaceinvaders.core;

import org.newdawn.spaceinvaders.entity.Entity;
import org.newdawn.spaceinvaders.entity.ShipEntity;
import org.newdawn.spaceinvaders.sound.SoundManager;

// GameManager가 이 인터페이스를 구현하여 게임의 상태와 로직을 관리
public interface GameContext {

    SoundManager getSoundManager();

    /**
     * 새로운 엔티티를 게임에 추가
     *
     * @param entity 추가할 엔티티
     */
    void addEntity(Entity entity);

    /**
     * 특정 엔티티를 게임에서 제거
     *
     * @param entity 제거할 엔티티
     */
    void removeEntity(Entity entity);

    /**
     * 플레이어의 죽음을 알림
     */
    void notifyDeath();

    /**
     * 플레이어의 승리를 알림
     */
    void notifyWin();

    /**
     * 에일리언이 화면 밖으로 탈출했음을 알림
     *
     * @param entity 탈출한 에일리언 엔티티
     */
    void notifyAlienEscaped(Entity entity);

    /**
     * 에일리언이 죽었음을 알림
     */
    void notifyAlienKilled();

    /**
     * 운석이 파괴되었음을 알리고 점수를 추가
     *
     * @param scoreValue 획득한 점수
     */
    void notifyMeteorDestroyed(int scoreValue);

    /**
     * 게임에 존재하는 모든 엔티티 목록을 가져옴
     *
     * @return 엔티티 목록
     */
    java.util.List<Entity> getEntities();

    /**
     * 플레이어의 우주선 엔티티를 가져옴
     *
     * @return 우주선 엔티티
     */
    ShipEntity getShip();

    /**
     * 아이템을 획득했음을 알림
     */
    void notifyItemCollected();

    /**
     * 모든 아이템을 획득했는지 확인
     *
     * @return 모든 아이템 획득 시 true, 그렇지 않으면 false
     */
    boolean hasCollectedAllItems();

    /**
     * 아이템 획득 상태를 초기화
     */
    void resetItemCollection();

    /**
     * 지정된 시간 동안 플레이어를 기절
     *
     * @param duration 기절 지속 시간 (밀리초)
     */
    void stunPlayer(long duration);


    /**
     * 플레이어가 공격할 수 있는지 확인
     *
     * @return 공격 가능 시 true, 그렇지 않으면 false
     */
    boolean canPlayerAttack();
}
