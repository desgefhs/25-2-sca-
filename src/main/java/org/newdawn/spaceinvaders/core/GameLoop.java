package org.newdawn.spaceinvaders.core;

import org.newdawn.spaceinvaders.core.GameStateManager;
import org.newdawn.spaceinvaders.view.GameWindow;

import java.awt.Graphics2D;

public class GameLoop {

    private final GameStateManager gsm;
    private final InputHandler inputHandler;
    private final GameWindow gameWindow;
    private final GameManager gameManager;

    private boolean gameRunning = true;
    private long lastLoopTime;

    public GameLoop(GameStateManager gsm, InputHandler inputHandler, GameWindow gameWindow, GameManager gameManager) {
        this.gsm = gsm;
        this.inputHandler = inputHandler;
        this.gameWindow = gameWindow;
        this.gameManager = gameManager;
    }

    public void run() {
        lastLoopTime = SystemTimer.getTime();
        while (gameRunning) {
            long delta = SystemTimer.getTime() - lastLoopTime;
            lastLoopTime = SystemTimer.getTime();

            gsm.handleInput(inputHandler);
            gsm.update(delta);

            if (gameManager.getMessageEndTime() > 0 && System.currentTimeMillis() > gameManager.getMessageEndTime()) {
                gameManager.setMessage("");
                gameManager.setMessageEndTime(0);
            }

            if (gameManager.nextState != null) {
                gameManager.setCurrentState(gameManager.nextState);
                gameManager.nextState = null;
            }

            Graphics2D g = gameWindow.getGameCanvas().getGraphics2D();
            if (g != null) {
                gsm.render(g);
                g.dispose();
                gameWindow.getGameCanvas().showStrategy();
            }

            SystemTimer.sleep(lastLoopTime + 10 - SystemTimer.getTime());
        }
    }
}
