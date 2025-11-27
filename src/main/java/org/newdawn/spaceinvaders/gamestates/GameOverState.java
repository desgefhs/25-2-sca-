package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameState;
import org.newdawn.spaceinvaders.core.InputHandler;
import org.newdawn.spaceinvaders.userinput.GameOverInputHandler;

import java.awt.*;

public class GameOverState implements GameState {
    private final GameContext gameContext;
    private final boolean gameWon;
    private final GameOverInputHandler inputHandler;

    public GameOverState(GameContext gameContext, boolean gameWon) {
        this.gameContext = gameContext;
        this.gameWon = gameWon;
        this.inputHandler = new GameOverInputHandler(gameContext);
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

        // Draw the message and GameOverMenu
        if (gameContext.getMessage() != null && !gameContext.getMessage().isEmpty()) {
            g.setColor(Color.white);
            g.setFont(new Font("Dialog", Font.BOLD, 20));
            g.drawString(gameContext.getMessage(), (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth(gameContext.getMessage())) / 2, 250);
        }

        g.setFont(new Font("Dialog", Font.BOLD, 24));
        int totalWidth = 0;
        int spacing = 40;
        for (int i = 0; i < gameContext.getGameContainer().getUiManager().getGameOverMenu().getItemCount(); i++) {
            totalWidth += g.getFontMetrics().stringWidth(gameContext.getGameContainer().getUiManager().getGameOverMenu().getItem(i));
        }
        totalWidth += (gameContext.getGameContainer().getUiManager().getGameOverMenu().getItemCount() - 1) * spacing;
        int currentX = (Game.SCREEN_WIDTH - totalWidth) / 2;

        for (int i = 0; i < gameContext.getGameContainer().getUiManager().getGameOverMenu().getItemCount(); i++) {
            if (i == gameContext.getGameContainer().getUiManager().getGameOverMenu().getSelectedIndex()) {
                g.setColor(Color.GREEN);
            } else {
                g.setColor(Color.WHITE);
            }
            g.drawString(gameContext.getGameContainer().getUiManager().getGameOverMenu().getItem(i), currentX, 350);
            currentX += g.getFontMetrics().stringWidth(gameContext.getGameContainer().getUiManager().getGameOverMenu().getItem(i)) + spacing;
        }
    }

    @Override
    public void onEnter() {
        if (gameWon) {
            gameContext.setMessage("Well done! You Win!");
        } else {
            gameContext.getGameContainer().getPlayerManager().saveGameResults();
            long finalCredit = gameContext.getGameContainer().getPlayerManager().getCurrentPlayer().getCredit();
            gameContext.setMessage(String.format("이번 라운드 점수: %d / 최종 크레딧: %d", gameContext.getGameContainer().getPlayerManager().getScore(), finalCredit));
        }
    }

    @Override
    public void onExit() {
        gameContext.setMessage("");
    }
}