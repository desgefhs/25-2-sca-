package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameState;
import org.newdawn.spaceinvaders.core.InputHandler;
import org.newdawn.spaceinvaders.userinput.ShopMainMenuInputHandler;
import org.newdawn.spaceinvaders.view.ShopMainMenuView;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class ShopMainMenuState implements GameState {

    private final GameContext gameContext;
    private final ShopMainMenuView shopMainMenuView;
    private final ShopMainMenuInputHandler inputHandler;

    private final Rectangle[] menuBounds;

    public ShopMainMenuState(GameContext gameContext) {
        this.gameContext = gameContext;
        this.shopMainMenuView = new ShopMainMenuView();
        this.inputHandler = new ShopMainMenuInputHandler(gameContext, this.shopMainMenuView);
        this.menuBounds = new Rectangle[shopMainMenuView.getItemCount()];
        for (int i = 0; i < shopMainMenuView.getItemCount(); i++) {
            menuBounds[i] = new Rectangle();
        }
    }

    @Override
    public void init() {
        // 이 상태에서는 사용하지 않음
    }

    @Override
    public void handleInput(InputHandler input) {
        inputHandler.handle(input);
    }

    @Override
    public void update(long delta) {
        // 이 상태에서는 사용하지 않음
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);

        g.setFont(new Font("Dialog", Font.BOLD, 32));
        g.setColor(Color.white);
        String title = "상점";
        int titleWidth = g.getFontMetrics().stringWidth(title);
        g.drawString(title, (Game.SCREEN_WIDTH - titleWidth) / 2, 150);

        g.setFont(new Font("Dialog", Font.BOLD, 24));
        int itemHeight = 60;
        int startY = 250;

        for (int i = 0; i < shopMainMenuView.getItemCount(); i++) {
            String menuItem = shopMainMenuView.getItem(i);
            int itemWidth = g.getFontMetrics().stringWidth(menuItem);
            int x = (Game.SCREEN_WIDTH - itemWidth) / 2;
            int y = startY + (i * itemHeight);

            menuBounds[i].setBounds(x - 20, y - 40, itemWidth + 40, itemHeight);

            if (i == shopMainMenuView.getSelectedIndex()) {
                g.setColor(Color.GREEN);
                g.fillRect(menuBounds[i].x, menuBounds[i].y, menuBounds[i].width, menuBounds[i].height);
                g.setColor(Color.BLACK);
            } else {
                g.setColor(Color.WHITE);
                g.drawRect(menuBounds[i].x, menuBounds[i].y, menuBounds[i].width, menuBounds[i].height);
            }
            g.drawString(menuItem, x, y);
        }
    }

    @Override
    public void onEnter() {
        // The selected index is now managed by the ShopMainMenuView itself,
        // but we can reset it if needed when entering the state.
        // For now, the view's own default is sufficient.
    }

    @Override
    public void onExit() {
        // 이 상태에서는 사용하지 않음
    }
}
