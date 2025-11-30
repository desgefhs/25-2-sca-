package org.newdawn.spaceinvaders.view;

import org.newdawn.spaceinvaders.core.InputHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import javax.swing.WindowConstants;

/**
 * 게임의 메인 창(JFrame)을 생성하고 전체적으로 관리하는 클래스입니다.
 * 게임 렌더링을 위한 {@link GameCanvas}를 내부에 포함합니다.
 */
public class GameWindow {

    /** 메인 창으로 사용되는 JFrame. */
    private final JFrame container;
    /** 실제 게임 화면이 그려지는 캔버스. */
    private final GameCanvas gameCanvas;

    /**
     * GameWindow 생성자.
     * JFrame과 GameCanvas를 설정하고, 입력 핸들러를 캔버스에 등록합니다.
     *
     * @param inputHandler 키 입력을 처리할 입력 핸들러
     */
    public GameWindow(InputHandler inputHandler) {
        container = new JFrame("Space Invaders");
        container.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        gameCanvas = new GameCanvas(inputHandler);
        container.getContentPane().add(gameCanvas);

        container.setResizable(false);
        container.pack();
        container.setLocationRelativeTo(null);
    }

    /**
     * 게임 창의 가시성을 설정합니다.
     * @param visible 창을 보이게 하려면 true, 숨기려면 false
     */
    public void setVisible(boolean visible) {
        container.setVisible(visible);
    }

    /**
     * 게임 캔버스 객체를 반환합니다.
     * @return GameCanvas 인스턴스
     */
    public GameCanvas getGameCanvas() {
        return gameCanvas;
    }

    /**
     * 더블 버퍼링을 사용하여 게임을 그리는 데 사용되는 AWT Canvas의 커스텀 구현체입니다.
     */
    public static class GameCanvas extends Canvas {
        /** 더블 버퍼링을 위한 버퍼 전략. `transient`로 직렬화에서 제외됩니다. */
        private transient BufferStrategy strategy;

        /**
         * GameCanvas 생성자.
         * @param inputHandler 키 입력을 처리할 입력 핸들러
         */
        public GameCanvas(InputHandler inputHandler) {
            setPreferredSize(new Dimension(800, 600));
            addKeyListener(inputHandler);
            setIgnoreRepaint(true); // OS의 다시 그리기 요청을 무시하여 직접 렌더링 제어
        }

        /**
         * 렌더링에 사용할 {@link Graphics2D} 객체를 버퍼 전략에서 가져옵니다.
         * 버퍼 전략이 없으면 새로 생성합니다.
         *
         * @return 렌더링을 위한 Graphics2D 컨텍스트
         */
        public Graphics2D getGraphics2D() {
            if (strategy == null || strategy.getDrawGraphics() == null) {
                createBufferStrategy(2); // 더블 버퍼링
                strategy = getBufferStrategy();
            }
            return (Graphics2D) strategy.getDrawGraphics();
        }

        /**
         * 백 버퍼의 내용을 화면에 표시(플립)합니다.
         */
        public void showStrategy() {
            if (strategy != null) {
                strategy.show();
            }
        }

        /**
         * 현재 사용 중인 버퍼 전략을 반환합니다.
         * @return BufferStrategy 인스턴스
         */
        public BufferStrategy getStrategy() {
            return strategy;
        }
    }
}
