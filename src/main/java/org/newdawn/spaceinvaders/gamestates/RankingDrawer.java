package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.ranking.Ranking;

import java.awt.*;
import java.util.List;

/**
 * 랭킹 화면의 UI 요소를 그리는 책임을 가진 클래스.
 * 랭킹 데이터는 받아와서 시각적으로 표현하는 역할만 수행합니다.
 */
public class RankingDrawer {

    private static final String FONT_NAME = "Dialog";
    /** 랭킹 제목에 사용될 폰트. */
    private static final Font RANKING_TITLE_FONT = new Font(FONT_NAME, Font.BOLD, 24);
    /** 랭킹 목록에 사용될 폰트. */
    private static final Font RANKING_LIST_FONT = new Font(FONT_NAME, Font.BOLD, 18);
    /** 복귀 안내 메시지에 사용될 폰트. */
    private static final Font RETURN_PROMPT_FONT = new Font(FONT_NAME, Font.BOLD, 14);

    /** 배경색. */
    private static final Color BACKGROUND_COLOR = Color.black;
    /** 폰트 색상. */
    private static final Color FONT_COLOR = Color.white;

    /** 제목의 Y축 위치. */
    private static final int TITLE_Y = 100;
    /** 랭킹 목록 시작 Y축 위치. */
    private static final int LIST_START_Y = 150;
    /** 랭킹 목록 항목 간의 간격. */
    private static final int LIST_ITEM_GAP = 30;
    /** 복귀 안내 메시지의 Y축 위치. */
    private static final int PROMPT_Y = 500;

    /**
     * 랭킹 화면의 모든 요소를 그립니다.
     *
     * @param g 화면에 그릴 Graphics2D 객체
     * @param highScores 표시할 랭킹 목록
     */
    public void draw(Graphics2D g, List<Ranking> highScores) {
        drawBackground(g);
        drawTitle(g);
        drawHighScores(g, highScores);
        drawReturnPrompt(g);
    }

    /**
     * 배경을 그립니다.
     *
     * @param g 화면에 그릴 Graphics2D 객체
     */
    private void drawBackground(Graphics2D g) {
        g.setColor(BACKGROUND_COLOR);
        g.fillRect(0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);
    }

    /**
     * 랭킹 화면의 제목을 그립니다.
     *
     * @param g 화면에 그릴 Graphics2D 객체
     */
    private void drawTitle(Graphics2D g) {
        g.setColor(FONT_COLOR);
        g.setFont(RANKING_TITLE_FONT);
        String title = "Ranking";
        int x = (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth(title)) / 2;
        g.drawString(title, x, TITLE_Y);
    }

    /**
     * 랭킹 목록을 그립니다.
     *
     * @param g 화면에 그릴 Graphics2D 객체
     * @param highScores 표시할 랭킹 목록
     */
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

    /**
     * 메인 메뉴로 돌아가는 안내 메시지를 그립니다.
     *
     * @param g 화면에 그릴 Graphics2D 객체
     */
    private void drawReturnPrompt(Graphics2D g) {
        g.setColor(FONT_COLOR);
        g.setFont(RETURN_PROMPT_FONT);
        String prompt = "Press Enter to return to Main Menu";
        int x = (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth(prompt)) / 2;
        g.drawString(prompt, x, PROMPT_Y);
    }
}
