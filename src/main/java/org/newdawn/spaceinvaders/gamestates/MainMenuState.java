package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameManager;
import org.newdawn.spaceinvaders.core.InputHandler;
import org.newdawn.spaceinvaders.view.MainMenu;

import java.awt.*;

public class MainMenuState implements GameState {

    private final GameManager gameManager;
    private final MainMenu mainMenu;

    public MainMenuState(GameManager gameManager) {
        this.gameManager = gameManager;
        this.mainMenu = gameManager.mainMenu;
    }

    @Override
    public void init() {}

    @Override
    public void handleInput(InputHandler input) {
        if (input.isLeftPressedAndConsume()) mainMenu.moveLeft();
        if (input.isRightPressedAndConsume()) mainMenu.moveRight();
        if (input.isEscPressedAndConsume()) {
            gameManager.setCurrentState(Type.EXIT_CONFIRMATION);
        }

        if (input.isFirePressedAndConsume()) {
            String selected = mainMenu.getSelectedItem();
            if ("1. 게임시작".equals(selected)) {
                gameManager.startGameplay();
            } else if ("2. 랭킹".equals(selected)) {
                gameManager.setCurrentState(Type.RANKING);
            } else if ("4. 상점".equals(selected)) {
                gameManager.setCurrentState(Type.SHOP);
            } else if ("5. 설정".equals(selected)){
                System.exit(0);
            }
        }
    }

    @Override
    public void update(long delta) {}

    @Override
    public void render(Graphics2D g) {
        g.drawImage(gameManager.staticBackgroundSprite.getImage(), 0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT, null);

        g.setFont(new Font("Dialog", Font.BOLD, 24));
        int totalWidth = 0;
        int spacing = 40;

        for (int i = 0; i < mainMenu.getItemCount(); i++) {
            totalWidth += g.getFontMetrics().stringWidth(mainMenu.getItem(i));
        }
        totalWidth += (mainMenu.getItemCount() - 1) * spacing;

        int currentX = (Game.SCREEN_WIDTH - totalWidth) / 2;

        for (int i = 0; i < mainMenu.getItemCount(); i++) {
            if (i == mainMenu.getSelectedIndex()) {
                g.setColor(Color.GREEN);
            } else {
                g.setColor(Color.WHITE);
            }
            g.drawString(mainMenu.getItem(i), currentX, 500);
            currentX += g.getFontMetrics().stringWidth(mainMenu.getItem(i)) + spacing;
        }
    }

    @Override
    public void onEnter() {}

    @Override
    public void onExit() {}
}
