package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.InputHandler;

import java.awt.Graphics2D;

/**
 * An interface for a single game state.
 */
public interface GameState {

    enum Type {
        MAIN_MENU,
        PLAYING,
        PAUSED,
        GAME_OVER,
        GAME_WON,
        RANKING,
        SHOP,
        PET_MENU,
        EXIT_CONFIRMATION,
        WAVE_CLEARED // This is a transient state
    }

    /**
     * Initialise the game state
     */
    void init();

    /**
     * Handle user input for this state.
     *
     * @param input The input handler providing the current key states.
     */
    void handleInput(InputHandler input);

    /**
     * Update the game logic for this state.
     *
     * @param delta The time that has passed since the last update.
     */
    void update(long delta);

    /**
     * Render the visual representation of this state.
     *
     * @param g The graphics context on which to draw.
     */
    void render(Graphics2D g);

    /**
     * Called when this state is entered.
     */
    void onEnter();

    /**
     * Called when this state is exited.
     */
    void onExit();
}
