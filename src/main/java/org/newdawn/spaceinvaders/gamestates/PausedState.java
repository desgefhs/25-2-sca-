package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameState;
import org.newdawn.spaceinvaders.core.InputHandler;
import org.newdawn.spaceinvaders.core.GameStateManager;

import java.awt.*;

public class PausedState implements GameState {
    private final GameContext gameContext;

    public PausedState(GameContext gameContext) {
        this.gameContext = gameContext;
    }

    @Override
    public void init() {}

    @Override
    public void handleInput(InputHandler input) {
        if (input.isUpPressedAndConsume()) gameContext.getPauseMenu().moveUp();
        if (input.isDownPressedAndConsume()) gameContext.getPauseMenu().moveDown();
        if (input.isEnterPressedAndConsume()) {
            gameContext.getSoundManager().playSound("buttonselect");
            String selected = gameContext.getPauseMenu().getSelectedItem();
            if ("재개하기".equals(selected)) gameContext.setCurrentState(Type.PLAYING);
            else if ("메인메뉴로 나가기".equals(selected)) {
                gameContext.saveGameResults();
                gameContext.setCurrentState(Type.MAIN_MENU);
            }
            else if ("종료하기".equals(selected)) System.exit(0);
        }
    }

    @Override
    public void update(long delta) {}

    @Override
    public void render(Graphics2D g) {
        // Render the playing state underneath
        PlayingState playingState = gameContext.getGsm().getPlayingState();
        if (playingState != null) {
            playingState.render(g);
        }

        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);

        // 일시정지 메뉴
        g.setFont(new Font("Dialog", Font.BOLD, 24));
        int itemHeight = 40;
        int startY = (Game.SCREEN_HEIGHT - (gameContext.getPauseMenu().getItemCount() * itemHeight)) / 2;

        for (int i = 0; i < gameContext.getPauseMenu().getItemCount(); i++) {
            if (i == gameContext.getPauseMenu().getSelectedIndex()) {
                g.setColor(Color.GREEN);
            } else {
                g.setColor(Color.WHITE);
            }
            String itemText = gameContext.getPauseMenu().getItem(i);
            int textWidth = g.getFontMetrics().stringWidth(itemText);
            g.drawString(itemText, (Game.SCREEN_WIDTH - textWidth) / 2, startY + (i * itemHeight));
        }
    }

    @Override
    public void onEnter() {}

    @Override
    public void onExit() {}
}