package org.newdawn.spaceinvaders.view;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.Entity;

import java.awt.*;

/**
 * 게임 플레이 상태(PlayingState)의 모든 시각적 요소를 그리는 책임을 가진 클래스.
 * 배경, 엔티티, UI 컴포넌트(점수, 버프 등)를 렌더링합니다.
 */
public class PlayingStateRenderer {

    /** 게임의 전반적인 컨텍스트. */
    private final GameContext gameContext;
    /** 버프 UI를 그리는 객체. */
    private final BuffUI buffUI;

    /**
     * PlayingStateRenderer 생성자.
     * @param gameContext 게임 컨텍스트
     */
    public PlayingStateRenderer(GameContext gameContext) {
        this.gameContext = gameContext;
        this.buffUI = new BuffUI();
    }

    /**
     * 게임 플레이 화면의 모든 요소를 렌더링합니다.
     * @param g 그래픽 컨텍스트
     */
    public void render(Graphics2D g) {
        // 배경 그리기
        drawBackground(g);

        // --- 게임 영역 내 요소 그리기 (클리핑 적용) ---
        Shape originalClip = g.getClip();
        try {
            g.setClip(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

            // 엔티티 그리기
            drawEntities(g);

            // 히트박스 그리기 (활성화 시)
            drawHitboxes(g);

        } finally {
            // 게임 영역 밖의 UI를 그리기 위해 클리핑 원상복구
            g.setClip(originalClip);
        }
        // --- 클리핑 종료 ---

        // UI 그리기
        drawUI(g);

        // 메시지 그리기
        drawMessage(g);
    }

    /** 배경을 그립니다. */
    private void drawBackground(Graphics2D g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);
        gameContext.getBackground().draw(g);
    }

    /** 모든 엔티티를 그립니다. */
    private void drawEntities(Graphics2D g) {
        for (Entity entity : gameContext.getGameContainer().getEntityManager().getEntities()) {
            entity.draw(g);
        }
    }

    /** 히트박스가 활성화된 경우, 모든 엔티티의 히트박스를 그립니다. */
    private void drawHitboxes(Graphics2D g) {
        if (gameContext.getShowHitboxes()) {
            g.setColor(Color.RED);
            for (Entity entity : gameContext.getGameContainer().getEntityManager().getEntities()) {
                g.drawRect(entity.getX(), entity.getY(), entity.getWidth(), entity.getHeight());
            }
        }
    }

    /** 점수, 웨이브, 플레이 시간 등 UI 정보를 그립니다. */
    private void drawUI(Graphics2D g) {
        g.setColor(Color.white);
        g.setFont(new Font("Dialog", Font.BOLD, 14));
        g.drawString(String.format("점수: %03d", gameContext.getGameContainer().getPlayerManager().getScore()), 680, 30);
        g.drawString(String.format("Wave: %d", gameContext.getGameContainer().getWaveManager().getWave()), 520, 30);

        // 플레이 시간 그리기
        if (gameContext.getGameContainer().getPlayerManager().getGameStartTime() > 0) {
            long elapsedMillis = System.currentTimeMillis() - gameContext.getGameContainer().getPlayerManager().getGameStartTime();
            long elapsedSeconds = elapsedMillis / 1000;
            long minutes = elapsedSeconds / 60;
            long seconds = elapsedSeconds % 60;
            g.drawString(String.format("Time: %02d:%02d", minutes, seconds), 520, 55);
        }

        // 버프 UI 그리기
        if (gameContext.getShip() != null) {
            buffUI.draw(g, gameContext.getShip().getBuffManager());
        }
    }

    /** 화면 중앙에 메시지를 그립니다. */
    private void drawMessage(Graphics2D g) {
        if (gameContext.getMessage() != null && !gameContext.getMessage().isEmpty()) {
            g.setColor(Color.white);
            g.setFont(new Font("Dialog", Font.BOLD, 20));
            g.drawString(gameContext.getMessage(), (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth(gameContext.getMessage())) / 2, 250);
        }
    }
}