package org.newdawn.spaceinvaders.view;

import org.newdawn.spaceinvaders.core.GameState;
import org.newdawn.spaceinvaders.core.InputHandler;
import org.newdawn.spaceinvaders.entity.Entity;
import org.newdawn.spaceinvaders.graphics.Sprite;
import org.newdawn.spaceinvaders.graphics.SpriteStore;


import javax.swing.*;
import java.awt.*;

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
                int startY = (600 - (pauseMenu.getItemCount() * itemHeight)) / 2;

                for (int i = 0; i < pauseMenu.getItemCount(); i++) {
                    if (i == pauseMenu.getSelectedIndex()) {
                        g.setColor(Color.GREEN);
                    } else {
                        g.setColor(Color.WHITE);
                    }
                    String itemText = pauseMenu.getItem(i);
                    int textWidth = g.getFontMetrics().stringWidth(itemText);
                    g.drawString(itemText, (800 - textWidth) / 2, startY + (i * itemHeight));
                }
            }

            g.dispose();
            strategy.show();
        }

        public void renderMenu(org.newdawn.spaceinvaders.view.MainMenu menu) {
            if (strategy == null) createStrategy();
            Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
            g.drawImage(backgroundSprite.getImage(), 0, 0, 800, 600, null);

            g.setFont(new Font("Dialog", Font.BOLD, 24));
            int totalWidth = 0;
            int spacing = 40;

            for (int i = 0; i < menu.getItemCount(); i++) {
                totalWidth += g.getFontMetrics().stringWidth(menu.getItem(i));
            }
            totalWidth += (menu.getItemCount() - 1) * spacing;

            int currentX = (800 - totalWidth) / 2;

            for (int i = 0; i < menu.getItemCount(); i++) {
                if (i == menu.getSelectedIndex()) {
                    g.setColor(Color.GREEN);
                } else {
                    g.setColor(Color.WHITE);
                }
                g.drawString(menu.getItem(i), currentX, 500);
                currentX += g.getFontMetrics().stringWidth(menu.getItem(i)) + spacing;
            }

            g.dispose();
            strategy.show();
        }

        public void renderRanking(java.util.List<String> highScores) {
            if (strategy == null) createStrategy();
            Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
            g.setColor(Color.black);
            g.fillRect(0, 0, 800, 600);

            g.setColor(Color.white);
            g.setFont(new Font("Dialog", Font.BOLD, 24));
            g.drawString("Ranking", (800 - g.getFontMetrics().stringWidth("Ranking")) / 2, 100);

            g.setFont(new Font("Dialog", Font.BOLD, 18));
            int y = 150;
            for (String score : highScores) {
                g.drawString(score, (800 - g.getFontMetrics().stringWidth(score)) / 2, y);
                y += 30;
            }

            g.setFont(new Font("Dialog", Font.BOLD, 14));
            g.drawString("Press Fire to return to Main Menu", (800 - g.getFontMetrics().stringWidth("Press Fire to return to Main Menu")) / 2, 500);

            g.dispose();
            strategy.show();
        }

        public void renderShop(int credit) {
            if (strategy == null) createStrategy();
            Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
            g.setColor(Color.black);
            g.fillRect(0, 0, 800, 600);

            g.setColor(Color.white);
            g.setFont(new Font("Dialog", Font.BOLD, 24));
            g.drawString("Shop", (800 - g.getFontMetrics().stringWidth("Shop")) / 2, 100);

            g.setFont(new Font("Dialog", Font.BOLD, 18));
            String creditText = "Your Credit: " + credit;
            g.drawString(creditText, (800 - g.getFontMetrics().stringWidth(creditText)) / 2, 250);

            g.setFont(new Font("Dialog", Font.BOLD, 14));
            g.drawString("Press Fire to return to Main Menu", (800 - g.getFontMetrics().stringWidth("Press Fire to return to Main Menu")) / 2, 500);

            g.dispose();
            strategy.show();
        }
    }
}