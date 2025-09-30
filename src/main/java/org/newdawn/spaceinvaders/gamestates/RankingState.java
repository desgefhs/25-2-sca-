package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameManager;
import org.newdawn.spaceinvaders.core.InputHandler;

import java.awt.*;

public class RankingState implements GameState {
    private final GameManager gameManager;

    public RankingState(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void init() {}

    @Override
    public void handleInput(InputHandler input) {
        if (input.isFirePressedAndConsume()) {
            gameManager.setCurrentState(Type.MAIN_MENU);
        }
    }

    @Override
    public void update(long delta) {}

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);

        g.setColor(Color.white);
        g.setFont(new Font("Dialog", Font.BOLD, 24));
        g.drawString("Ranking", (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth("Ranking")) / 2, 100);

        g.setFont(new Font("Dialog", Font.BOLD, 18));
        int y = 150;
        java.util.List<String> highScores = gameManager.getDatabaseManager().getHighScores();
        for (String score : highScores) {
            g.drawString(score, (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth(score)) / 2, y);
            y += 30;
        }

        g.setFont(new Font("Dialog", Font.BOLD, 14));
        g.drawString("Press Fire to return to Main Menu", (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth("Press Fire to return to Main Menu")) / 2, 500);
    }

    @Override
    public void onEnter() {}

    @Override
    public void onExit() {}
}
