package org.newdawn.spaceinvaders.view;

import org.newdawn.spaceinvaders.core.InputHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;

/**
 * 게임의 주 윈도우(JFrame)를 생성하고 관리하는 클래스
 * 게임이 그려지는 GameCanvas를 포함
 */
public class GameWindow {

    /** 게임 창의 최상위 컨테이너인 JFrame */
    private final JFrame container;
    /** 게임이 실제로 그려지는 Canvas */
    private final GameCanvas gameCanvas;

    /**
     * GameWindow를 생성하고, JFrame과 GameCanvas를 설정
     *
     * @param inputHandler 키 입력을 처리할 입력 핸들러
     */
    public GameWindow(InputHandler inputHandler) {
        container = new JFrame("Space Invaders");
        container.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gameCanvas = new GameCanvas(inputHandler);
        container.getContentPane().add(gameCanvas);

        container.setResizable(false);
        container.pack();
        container.setLocationRelativeTo(null); // 화면 중앙에 창 표시
    }

    public void setVisible(boolean visible) {
        container.setVisible(visible);
    }

    public GameCanvas getGameCanvas() {
        return gameCanvas;
    }

    /**
     * 더블 버퍼링을 사용하여 게임을 그리는 Canvas 클래스
     */
    public static class GameCanvas extends Canvas {
        /** 더블 버퍼링을 위한 버퍼 전략 */
        private BufferStrategy strategy;

        public GameCanvas(InputHandler inputHandler) {
            setPreferredSize(new Dimension(800, 600));
            addKeyListener(inputHandler);
            setIgnoreRepaint(true); // OS의 다시 그리기 요청을 무시하여 직접 렌더링 제어
        }

        /**
         * 버퍼에서 그림을 그릴 수 있는 Graphics2D 객체를 가져옴
         * 버퍼 전략이 없으면 새로 생성
         *
         * @return 렌더링에 사용할 Graphics2D 객체
         */
        public Graphics2D getGraphics2D() {
            if (strategy == null || strategy.getDrawGraphics() == null) {
                createBufferStrategy(2); // 2개의 버퍼를 사용하는 더블 버퍼링
                strategy = getBufferStrategy();
            }
            return (Graphics2D) strategy.getDrawGraphics();
        }

        /**
         * 백 버퍼에 그려진 내용을 화면에 표시 (버퍼를 교체).
         */
        public void showStrategy() {
            if (strategy != null) {
                strategy.show();
            }
        }

        public BufferStrategy getStrategy() {
            return strategy;
        }
    }
}
