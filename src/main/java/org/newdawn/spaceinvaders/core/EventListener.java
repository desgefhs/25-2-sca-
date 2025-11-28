package org.newdawn.spaceinvaders.core;

import org.newdawn.spaceinvaders.core.events.Event;

/**
 * 이벤트 버스({@link EventBus})의 이벤트를 수신(listen)하려는 클래스를 위한 인터페이스.
 */
public interface EventListener {
    /**
     * 이벤트가 이벤트 버스로 발행(publish)될 때 호출됩니다.
     * 리스너는 이 메소드 내에서 이벤트의 타입을 확인하고 적절한 로직을 수행해야 합니다.
     *
     * @param event 발행된 이벤트 객체
     */
    void onEvent(Event event);
}
