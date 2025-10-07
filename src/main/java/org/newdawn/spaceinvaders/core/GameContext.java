package org.newdawn.spaceinvaders.core;

import org.newdawn.spaceinvaders.entity.Entity;
import org.newdawn.spaceinvaders.entity.ShipEntity;

// gamemanager 인터페이스.

public interface GameContext {

    // 새로운 엔티티를 추가

    void addEntity(Entity entity);

    // 특정 엔티티 제거
    void removeEntity(Entity entity);

    // 플레이어 사망
    void notifyDeath();

    // 플레이어 승리
    void notifyWin();

    // 엔티티가 화면 밖으로 나감
    void notifyAlienEscaped(Entity entity);

    // 적 엔티티 처리함
    void notifyAlienKilled();

    //게임에 존재하는 모든 엔티티를 가져옴
    java.util.List<Entity> getEntities();

    ShipEntity getShip();

    void notifyItemCollected();

    boolean hasCollectedAllItems();

    void resetItemCollection();

    void stunPlayer(long duration);


    boolean canPlayerAttack();
}
