package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameState;
import org.newdawn.spaceinvaders.core.InputHandler;
import org.newdawn.spaceinvaders.userinput.PlayingInputHandler;
import org.newdawn.spaceinvaders.view.PlayingStateRenderer;

import java.awt.Graphics2D;

public class PlayingState implements GameState {

    private final GameContext gameContext;
    private final PlayingStateRenderer renderer;
    private final PlayingInputHandler inputHandler;

    public PlayingState(GameContext gameContext) {
        this.gameContext = gameContext;
        this.renderer = new PlayingStateRenderer(gameContext);
        this.inputHandler = new PlayingInputHandler(gameContext);
    }

    @Override
    public void init() {
        // The init logic is now part of onEnter to ensure it's called every time we enter the state
    }

    @Override
    public void handleInput(InputHandler input) {
        // All input handling is now delegated to the dedicated handler class.
        inputHandler.handle(input);
    }

    @Override
    public void update(long delta) {
        // The responsibility for the game loop's update order now belongs to GameManager.
        gameContext.updatePlayingLogic(delta);
    }

    @Override
    public void render(Graphics2D g) {
        renderer.render(g);
    }

    @Override
    public void onEnter() {
        // Initialize the wave manager's timers every time we enter the playing state
        gameContext.getWaveManager().init();
    }

    @Override
    public void onExit() {}
}