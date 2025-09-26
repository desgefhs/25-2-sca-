package org.newdawn.spaceinvaders;

import org.newdawn.spaceinvaders.entity.Entity;
import org.newdawn.spaceinvaders.view.MainMenu;
import org.newdawn.spaceinvaders.SpriteStore;
import org.newdawn.spaceinvaders.Sprite;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.List;

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
        // setVisible(true) will be called later, after successful login
    }

    public void setVisible(boolean visible) {
        container.setVisible(visible);
    }

    public void setTitle(String title) {
        container.setTitle(title);
    }

    public GameCanvas getGameCanvas() {
        return gameCanvas;
    }

    // Inner class for the game's drawing canvas
    public static class GameCanvas extends Canvas {
        private BufferStrategy strategy;
        private final Sprite backgroundSprite;

        public GameCanvas(InputHandler inputHandler) {
            this.backgroundSprite = SpriteStore.get().getSprite("sprites/background.jpg");
            setPreferredSize(new Dimension(800, 600));
            addKeyListener(inputHandler);
            setIgnoreRepaint(true);
        }

        public void createStrategy() {
            createBufferStrategy(2);
            strategy = getBufferStrategy();
        }

        public void render(List<Entity> entities, String message, int score, GameState currentState, double backgroundY, int wave, org.newdawn.spaceinvaders.view.PauseMenu pauseMenu) {
            if (strategy == null) createStrategy();
            Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
            g.setColor(Color.black);
            g.fillRect(0, 0, 800, 600);

            for (Entity entity : entities) {
                entity.draw(g);
            }

            g.setColor(Color.white);
            g.setFont(new Font("Dialog", Font.BOLD, 14));
            g.drawString(String.format("Score: %03d", score), 680, 30);
            g.drawString(String.format("Wave: %d", wave), 20, 30);

            if (message != null && !message.isEmpty() && currentState != GameState.PAUSED) {
                g.setColor(Color.white);
                g.setFont(new Font("Dialog", Font.BOLD, 20));
                g.drawString(message, (800 - g.getFontMetrics().stringWidth(message)) / 2, 250);
                if (currentState == GameState.GAME_OVER || currentState == GameState.GAME_WON) {
                    g.drawString("Press Enter to Continue", (800 - g.getFontMetrics().stringWidth("Press Enter to Continue")) / 2, 300);
                }
            }

            if (currentState == GameState.PAUSED) {
                g.setColor(new Color(0, 0, 0, 150));
                g.fillRect(0, 0, 800, 600);
                g.setFont(new Font("Dialog", Font.BOLD, 24));
                int itemHeight = 40;
