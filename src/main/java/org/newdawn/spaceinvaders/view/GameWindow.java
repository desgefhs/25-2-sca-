package org.newdawn.spaceinvaders.view;

import org.newdawn.spaceinvaders.core.InputHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;

public class GameWindow {

    private final JFrame container;
    private final GameCanvas gameCanvas;

    public GameWindow(InputHandler inputHandler) {
        container = new JFrame("Space Invaders");
        container.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gameCanvas = new GameCanvas(inputHandler);
        container.getContentPane().add(gameCanvas);

        container.setResizable(false);
        container.pack();
        container.setLocationRelativeTo(null);
    }

    public void setVisible(boolean visible) {
        container.setVisible(visible);
    }

    public GameCanvas getGameCanvas() {
        return gameCanvas;
    }

    public static class GameCanvas extends Canvas {
        private BufferStrategy strategy;

        public GameCanvas(InputHandler inputHandler) {
            setPreferredSize(new Dimension(800, 600));
            addKeyListener(inputHandler);
            setIgnoreRepaint(true);
        }

        public Graphics2D getGraphics2D() {
            if (strategy == null || strategy.getDrawGraphics() == null) {
                createBufferStrategy(2);
                strategy = getBufferStrategy();
            }
            return (Graphics2D) strategy.getDrawGraphics();
        }

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
