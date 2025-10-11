package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameManager;
import org.newdawn.spaceinvaders.core.InputHandler;

import java.awt.*;

public class GameOverState implements GameState {
    private final GameManager gameManager;
    private final boolean gameWon;

    public GameOverState(GameManager gameManager, boolean gameWon) {
        this.gameManager = gameManager;
        this.gameWon = gameWon;
    }

    @Override
    public void init() {}

    @Override
    public void handleInput(InputHandler input) {
        if (input.isLeftPressedAndConsume()) gameManager.gameOverMenu.moveLeft();
        if (input.isRightPressedAndConsume()) gameManager.gameOverMenu.moveRight();

        if (input.isEnterPressedAndConsume()) {
            gameManager.getSoundManager().playSound("buttonselect");
            String selected = gameManager.gameOverMenu.getSelectedItem();
            if ("다시하기".equals(selected)) {
                gameManager.startGameplay();
            } else if ("메인 메뉴로".equals(selected)) {
                gameManager.setCurrentState(Type.MAIN_MENU);
            }
        }
    }

    @Override
    public void update(long delta) {}

    @Override
    public void render(Graphics2D g) {
        // Render the playing state underneath
        PlayingState playingState = gameManager.getGsm().getPlayingState();
        if (playingState != null) {
            playingState.render(g);
        }

        // Draw the message and GameOverMenu
        if (gameManager.message != null && !gameManager.message.isEmpty()) {
            g.setColor(Color.white);
            g.setFont(new Font("Dialog", Font.BOLD, 20));
            g.drawString(gameManager.message, (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth(gameManager.message)) / 2, 250);
        }

        g.setFont(new Font("Dialog", Font.BOLD, 24));
        int totalWidth = 0;
        int spacing = 40;
        for (int i = 0; i < gameManager.gameOverMenu.getItemCount(); i++) {
            totalWidth += g.getFontMetrics().stringWidth(gameManager.gameOverMenu.getItem(i));
        }
        totalWidth += (gameManager.gameOverMenu.getItemCount() - 1) * spacing;
        int currentX = (Game.SCREEN_WIDTH - totalWidth) / 2;

        for (int i = 0; i < gameManager.gameOverMenu.getItemCount(); i++) {
            if (i == gameManager.gameOverMenu.getSelectedIndex()) {
                g.setColor(Color.GREEN);
            } else {
                g.setColor(Color.WHITE);
            }
            g.drawString(gameManager.gameOverMenu.getItem(i), currentX, 350);
            currentX += g.getFontMetrics().stringWidth(gameManager.gameOverMenu.getItem(i)) + spacing;
        }
    }

    @Override
    public void onEnter() {
        if (gameWon) {
            gameManager.message = "Well done! You Win!";
        } else {
            gameManager.saveGameResults();
            long finalCredit = gameManager.currentPlayer.getCredit();
            gameManager.message = String.format("이번 라운드 점수: %d / 최종 크레딧: %d", gameManager.score, finalCredit);
        }
    }

    @Override
    public void onExit() {
        gameManager.message = "";
    }
}
