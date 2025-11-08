package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameState;
import org.newdawn.spaceinvaders.core.InputHandler;
import org.newdawn.spaceinvaders.userinput.ExitConfirmationInputHandler;

import java.awt.*;

public class ExitConfirmationState implements GameState {
    private final GameContext gameContext;
    private final ExitConfirmationInputHandler inputHandler;

    public ExitConfirmationState(GameContext gameContext) {
        this.gameContext = gameContext;
        this.inputHandler = new ExitConfirmationInputHandler(gameContext);
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
        // Render the main menu underneath
        MainMenuState mainMenuState = gameContext.getGsm().getMainMenuState();
        if (mainMenuState != null) {
            mainMenuState.render(g);
        }

        // Draw a semi-transparent overlay
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);

        // Draw the dialog box
        g.setColor(Color.BLACK);
        g.fillRect(200, 200, 400, 200);
        g.setColor(Color.WHITE);
        g.drawRect(200, 200, 400, 200);

        // Draw the message
        g.setFont(new Font("Dialog", Font.BOLD, 20));
        g.drawString(gameContext.getConfirmDialog().getMessage(), (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth(gameContext.getConfirmDialog().getMessage())) / 2, 260);

        // Draw the buttons
        g.setFont(new Font("Dialog", Font.BOLD, 24));
        int totalWidth = 0;
        int spacing = 80;

        for (int i = 0; i < gameContext.getConfirmDialog().getItemCount(); i++) {
            totalWidth += g.getFontMetrics().stringWidth(gameContext.getConfirmDialog().getItem(i));
        }
        totalWidth += (gameContext.getConfirmDialog().getItemCount() - 1) * spacing;

        int currentX = (Game.SCREEN_WIDTH - totalWidth) / 2;

        for (int i = 0; i < gameContext.getConfirmDialog().getItemCount(); i++) {
            if (i == gameContext.getConfirmDialog().getSelectedIndex()) {
                g.setColor(Color.GREEN);
            } else {
                g.setColor(Color.WHITE);
            }
            g.drawString(gameContext.getConfirmDialog().getItem(i), currentX, 350);
            currentX += g.getFontMetrics().stringWidth(gameContext.getConfirmDialog().getItem(i)) + spacing;
        }
    }

    @Override
    public void onEnter() {}

    @Override
    public void onExit() {}
}
