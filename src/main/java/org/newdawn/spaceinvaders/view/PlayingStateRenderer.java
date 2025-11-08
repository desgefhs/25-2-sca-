package org.newdawn.spaceinvaders.view;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.Entity;

import java.awt.*;

public class PlayingStateRenderer {

    private final GameContext gameContext;
    private final BuffUI buffUI;

    public PlayingStateRenderer(GameContext gameContext) {
        this.gameContext = gameContext;
        this.buffUI = new BuffUI();
    }

    public void render(Graphics2D g) {
        // Draw Background
        g.setColor(Color.black);
        g.fillRect(0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);
        gameContext.getBackground().draw(g);

        // --- Start of Clipped Drawing ---
        Shape originalClip = g.getClip();
        try {
            g.setClip(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

            // Draw Entities
            for (Entity entity : gameContext.getEntityManager().getEntities()) {
                entity.draw(g);
            }

            // Draw Hitboxes if enabled
            if (gameContext.getShowHitboxes()) {
                g.setColor(Color.RED);
                for (Entity entity : gameContext.getEntityManager().getEntities()) {
                    g.drawRect(entity.getX(), entity.getY(), entity.getWidth(), entity.getHeight());
                }
            }
        } finally {
            // Restore original clip to draw UI elements outside the game area
            g.setClip(originalClip);
        }
        // --- End of Clipped Drawing ---

        // Draw UI
        g.setColor(Color.white);
        g.setFont(new Font("Dialog", Font.BOLD, 14));
        g.drawString(String.format("점수: %03d", gameContext.getPlayerManager().getScore()), 680, 30);
        g.drawString(String.format("Wave: %d", gameContext.getWaveManager().getWave()), 520, 30);

        // Draw Play Time
        if (gameContext.getPlayerManager().getGameStartTime() > 0) {
            long elapsedMillis = System.currentTimeMillis() - gameContext.getPlayerManager().getGameStartTime();
            long elapsedSeconds = elapsedMillis / 1000;
            long minutes = elapsedSeconds / 60;
            long seconds = elapsedSeconds % 60;
            g.drawString(String.format("Time: %02d:%02d", minutes, seconds), 520, 55);
        }

        // Draw Buff UI
        if (gameContext.getShip() != null) {
            buffUI.draw(g, gameContext.getShip().getBuffManager());
        }

        // Draw Message if any
        if (gameContext.getMessage() != null && !gameContext.getMessage().isEmpty()) {
            g.setColor(Color.white);
            g.setFont(new Font("Dialog", Font.BOLD, 20));
            g.drawString(gameContext.getMessage(), (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth(gameContext.getMessage())) / 2, 250);
        }
    }
}