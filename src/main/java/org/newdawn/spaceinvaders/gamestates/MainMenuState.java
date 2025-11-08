package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameState;
import org.newdawn.spaceinvaders.core.InputHandler;
import org.newdawn.spaceinvaders.userinput.MainMenuInputHandler;
import org.newdawn.spaceinvaders.view.MainMenu;

import java.awt.*;

public class MainMenuState implements GameState {

    private final GameContext gameContext;
    private final MainMenu mainMenu;
    private final MainMenuInputHandler inputHandler;

    public MainMenuState(GameContext gameContext) {
        this.gameContext = gameContext;
        this.mainMenu = gameContext.getMainMenu();
        this.inputHandler = new MainMenuInputHandler(gameContext);
    }

    @Override
    public void init() {}

    @Override
    public void handleInput(InputHandler input) {
        inputHandler.handle(input);
    }

    @Override
    public void update(long delta) {}

    @Override
    public void render(Graphics2D g) {
        g.drawImage(gameContext.getStaticBackgroundSprite().getImage(), 0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT, null);

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
    public void onEnter() {
        gameContext.getSoundManager().loopSound("menubackground");
    }

    @Override
    public void onExit() {}
}