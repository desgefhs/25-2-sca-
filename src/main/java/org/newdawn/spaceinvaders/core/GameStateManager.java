package org.newdawn.spaceinvaders.core;

import org.newdawn.spaceinvaders.gamestates.MainMenuState;
import org.newdawn.spaceinvaders.gamestates.PlayingState;



public class GameStateManager {

    private GameState currentState;
    private PlayingState playingState;
    private MainMenuState mainMenuState;

    public void setState(GameState state) {
        if (currentState != null) {
            currentState.onExit();
        }
        currentState = state;
        if (currentState != null) {
            if (state instanceof PlayingState) {
                this.playingState = (PlayingState) state;
            }
            if (state instanceof MainMenuState) {
                this.mainMenuState = (MainMenuState) state;
            }
            currentState.init();
            currentState.onEnter();
        }
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public PlayingState getPlayingState() {
        return playingState;
    }

    public MainMenuState getMainMenuState() {
        return mainMenuState;
    }

    public void update(long delta) {
        if (currentState != null) {
            currentState.update(delta);
        }
    }

    public void handleInput(InputHandler input) {
        if (currentState != null) {
            currentState.handleInput(input);
        }
    }

    public void render(java.awt.Graphics2D g) {
        if (currentState != null) {
            currentState.render(g);
        }
    }
}
