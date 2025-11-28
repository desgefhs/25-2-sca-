package org.newdawn.spaceinvaders.core;

import java.util.ArrayList;
import java.util.List;

/**
 * 게임 컴포넌트 간의 결합도를 낮추기 위한 간단한 동기 방식의 이벤트 버스.
 * 옵저버 패턴(Observer Pattern)을 구현합니다.
 */
public class EventBus {

    /** 이벤트를 수신할 리스너들의 목록. */
    private final List<EventListener> listeners = new ArrayList<>();

    /**
     * 리스너를 등록하여 이벤트를 수신할 수 있도록 합니다.
     * @param listener 등록할 리스너
     */
    public void register(EventListener listener) {
        listeners.add(listener);
    }


    /**
     * 등록된 모든 리스너에게 이벤트를 발행(전송)합니다.
     * @param event 발행할 이벤트
     */
    public void publish(Event event) {
        // 리스너 목록을 복사하여 순회 중 수정 문제를 방지할 수 있지만,
        // 현재 구조에서는 동기식으로 즉시 처리되므로 필수적이지는 않음.
        for (EventListener listener : new ArrayList<>(listeners)) {
            listener.onEvent(event);
        }
    }
}
