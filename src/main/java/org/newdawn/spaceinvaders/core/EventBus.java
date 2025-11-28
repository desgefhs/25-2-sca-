package org.newdawn.spaceinvaders.core;

import org.newdawn.spaceinvaders.core.events.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple synchronous event bus for decoupling game components.
 */
public class EventBus {

    private final List<EventListener> listeners = new ArrayList<>();

    /**
     * Registers a listener to receive events.
     * @param listener The listener to register.
     */
    public void register(EventListener listener) {
        listeners.add(listener);
    }


    /**
     * Publishes an event to all registered listeners.
     * @param event The event to publish.
     */
    public void publish(Event event) {
        for (EventListener listener : listeners) {
            listener.onEvent(event);
        }
    }
}
