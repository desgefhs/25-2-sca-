package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameState;
import org.newdawn.spaceinvaders.core.InputHandler;
import org.newdawn.spaceinvaders.userinput.PausedInputHandler;

import java.awt.*;

public class PausedState implements GameState {
    private final GameContext gameContext;
    private final PausedInputHandler inputHandler;

    public PausedState(GameContext gameContext) {
        this.gameContext = gameContext;
        this.inputHandler = new PausedInputHandler(gameContext);
    }

    @Override
    public void init() {
        // 이 상태에서는 사용하지 않음
    }

    @Override
    public void handleInput(InputHandler input) {
        inputHandler.handle(input);
    }

    @Override
    public void update(long delta) {
        // 이 상태에서는 사용하지 않음
    }

    @Override
    public void render(Graphics2D g) {
        // Render the playing state underneath
        PlayingState playingState = gameContext.getGameContainer().getGsm().getPlayingState();
        if (playingState != null) {
            playingState.render(g);
        }

        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);

        // 일시정지 메뉴
        g.setFont(new Font("Dialog", Font.BOLD, 24));
        int itemHeight = 40;
        int startY = (Game.SCREEN_HEIGHT - (gameContext.getGameContainer().getUiManager().getPauseMenu().getItemCount() * itemHeight)) / 2;

        for (int i = 0; i < gameContext.getGameContainer().getUiManager().getPauseMenu().getItemCount(); i++) {
            if (i == gameContext.getGameContainer().getUiManager().getPauseMenu().getSelectedIndex()) {
                g.setColor(Color.GREEN);
            } else {
                g.setColor(Color.WHITE);
            }
            String itemText = gameContext.getGameContainer().getUiManager().getPauseMenu().getItem(i);
            int textWidth = g.getFontMetrics().stringWidth(itemText);
            g.drawString(itemText, (Game.SCREEN_WIDTH - textWidth) / 2, startY + (i * itemHeight));
        }
    }

    @Override
    public void onEnter() {
        // 이 상태에서는 사용하지 않음
    }

    @Override
    public void onExit() {
        // 이 상태에서는 사용하지 않음
    }
}
