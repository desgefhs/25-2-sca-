package org.newdawn.spaceinvaders.core.events;

import org.newdawn.spaceinvaders.core.Event;
import org.newdawn.spaceinvaders.entity.Entity;

/**
 * 외계인이 화면 밖으로 탈출했을 때 발생하는 이벤트를 나타내는 클래스.
 */
public class AlienEscapedEvent implements Event {
    /** 탈출한 외계인 엔티티. */
    private final Entity alien;

    /**
     * AlienEscapedEvent 생성자.
     * @param alien 탈출한 외계인 엔티티
     */
    public AlienEscapedEvent(Entity alien) {
        this.alien = alien;
    }

    /**
     * 탈출한 외계인 엔티티를 반환합니다.
     * @return 탈출한 외계인
     */
    public Entity getAlien() {
        return alien;
    }
}
