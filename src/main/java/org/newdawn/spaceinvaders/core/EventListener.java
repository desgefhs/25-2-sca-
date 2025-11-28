package org.newdawn.spaceinvaders.core;

import org.newdawn.spaceinvaders.core.events.Event;

/**
 * An interface for classes that want to listen to events on the EventBus.
 */
public interface EventListener {
    /**
     * Called when an event is published to the EventBus.
     * @param event The event that was published.
     */
    void onEvent(Event event);
}
