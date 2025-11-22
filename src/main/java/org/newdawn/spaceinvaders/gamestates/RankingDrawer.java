package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.ranking.Ranking;

import java.awt.*;
import java.util.List;

public class RankingDrawer {

    private static final String FONT_NAME = "Dialog";
    private static final Font RANKING_TITLE_FONT = new Font(FONT_NAME, Font.BOLD, 24);
    private static final Font RANKING_LIST_FONT = new Font(FONT_NAME, Font.BOLD, 18);
    private static final Font RETURN_PROMPT_FONT = new Font(FONT_NAME, Font.BOLD, 14);

    private static final Color BACKGROUND_COLOR = Color.black;
    private static final Color FONT_COLOR = Color.white;

    private static final int TITLE_Y = 100;
    private static final int LIST_START_Y = 150;
    private static final int LIST_ITEM_GAP = 30;
    private static final int PROMPT_Y = 500;

    public void draw(Graphics2D g, List<Ranking> highScores) {
        drawBackground(g);
        drawTitle(g);
        drawHighScores(g, highScores);
        drawReturnPrompt(g);
    }

    private void drawBackground(Graphics2D g) {
        g.setColor(BACKGROUND_COLOR);
        g.fillRect(0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);
    }

    private void drawTitle(Graphics2D g) {
        g.setColor(FONT_COLOR);
        g.setFont(RANKING_TITLE_FONT);
        String title = "Ranking";
        int x = (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth(title)) / 2;
        g.drawString(title, x, TITLE_Y);
    }

    private void drawHighScores(Graphics2D g, List<Ranking> highScores) {
        if (highScores == null) {
            return;
        }

        g.setColor(FONT_COLOR);
        g.setFont(RANKING_LIST_FONT);
        int y = LIST_START_Y;
        int rank = 1;
        for (Ranking ranking : highScores) {
            String scoreText = String.format("%d. %s - %d", rank, ranking.getName(), ranking.getScore());
            int x = (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth(scoreText)) / 2;
            g.drawString(scoreText, x, y);
            y += LIST_ITEM_GAP;
            rank++;
        }
    }

    private void drawReturnPrompt(Graphics2D g) {
        g.setColor(FONT_COLOR);
        g.setFont(RETURN_PROMPT_FONT);
        String prompt = "Press Enter to return to Main Menu";
        int x = (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth(prompt)) / 2;
        g.drawString(prompt, x, PROMPT_Y);
    }
}
