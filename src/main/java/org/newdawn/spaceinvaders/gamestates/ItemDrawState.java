package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameState;
import org.newdawn.spaceinvaders.core.InputHandler;
import org.newdawn.spaceinvaders.userinput.ItemDrawInputHandler;
import org.newdawn.spaceinvaders.view.ItemDrawView;

import java.awt.*;

public class ItemDrawState implements GameState {

    private final GameContext gameContext;
    private final ItemDrawView itemView;
    private final ItemDrawInputHandler inputHandler;
    private final Rectangle[] menuBounds;

    public ItemDrawState(GameContext gameContext) {
        this.gameContext = gameContext;
        this.itemView = new ItemDrawView();
        this.inputHandler = new ItemDrawInputHandler(gameContext, this.itemView);
        this.menuBounds = new Rectangle[itemView.getItemCount()];
        for (int i = 0; i < itemView.getItemCount(); i++) {
            menuBounds[i] = new Rectangle();
        }
    }

    @Override
    public void init() {
    }

    @Override
    public void handleInput(InputHandler input) {
        inputHandler.handle(input);
    }

    @Override
    public void update(long delta) {
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);

        g.setFont(new Font("Dialog", Font.BOLD, 32));
        g.setColor(Color.white);
        String title = "아이템 뽑기";
        int titleWidth = g.getFontMetrics().stringWidth(title);
        g.drawString(title, (Game.SCREEN_WIDTH - titleWidth) / 2, 150);

        g.setFont(new Font("Dialog", Font.BOLD, 20));
        String creditText = "보유 크레딧: " + gameContext.getPlayerManager().getCurrentPlayer().getCredit();
        g.drawString(creditText, (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth(creditText)) / 2, 200);

        if (gameContext.getMessage() != null && !gameContext.getMessage().isEmpty()) {
            g.setColor(Color.yellow);
            g.setFont(new Font("Dialog", Font.BOLD, 16));
            g.drawString(gameContext.getMessage(), (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth(gameContext.getMessage())) / 2, 450);
        }

        g.setFont(new Font("Dialog", Font.BOLD, 24));
        int itemHeight = 60;
        int startY = 300;

        for (int i = 0; i < itemView.getItemCount(); i++) {
            String menuItemText = itemView.getItem(i);
            if (i == 0) { // "아이템 뽑기" button
                String costText = " (비용: " + gameContext.getShopManager().getItemDrawCost() + ")";
                menuItemText += costText;
            }

            int itemWidth = g.getFontMetrics().stringWidth(menuItemText);
            int x = (Game.SCREEN_WIDTH - itemWidth) / 2;
            int y = startY + (i * itemHeight);

            menuBounds[i].setBounds(x - 20, y - 40, itemWidth + 40, itemHeight);

            if (i == itemView.getSelectedIndex()) {
                g.setColor(Color.GREEN);
                g.fillRect(menuBounds[i].x, menuBounds[i].y, menuBounds[i].width, menuBounds[i].height);
                g.setColor(Color.BLACK);
            } else {
                g.setColor(Color.WHITE);
                g.drawRect(menuBounds[i].x, menuBounds[i].y, menuBounds[i].width, menuBounds[i].height);
            }
            g.drawString(menuItemText, x, y);
        }
    }

    @Override
    public void onEnter() {
        gameContext.setMessage("");
        itemView.setSelectedIndex(0);
    }

    @Override
    public void onExit() {
    }
}
