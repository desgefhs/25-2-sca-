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
        // Render the main menu underneath
        MainMenuState mainMenuState = gameContext.getGameContainer().getGsm().getMainMenuState();
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
        g.drawString(gameContext.getGameContainer().getUiManager().getConfirmDialog().getMessage(), (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth(gameContext.getGameContainer().getUiManager().getConfirmDialog().getMessage())) / 2, 260);

        // Draw the buttons
        g.setFont(new Font("Dialog", Font.BOLD, 24));
        int totalWidth = 0;
        int spacing = 80;

        for (int i = 0; i < gameContext.getGameContainer().getUiManager().getConfirmDialog().getItemCount(); i++) {
            totalWidth += g.getFontMetrics().stringWidth(gameContext.getGameContainer().getUiManager().getConfirmDialog().getItem(i));
        }
        totalWidth += (gameContext.getGameContainer().getUiManager().getConfirmDialog().getItemCount() - 1) * spacing;

        int currentX = (Game.SCREEN_WIDTH - totalWidth) / 2;

        for (int i = 0; i < gameContext.getGameContainer().getUiManager().getConfirmDialog().getItemCount(); i++) {
            if (i == gameContext.getGameContainer().getUiManager().getConfirmDialog().getSelectedIndex()) {
                g.setColor(Color.GREEN);
            } else {
                g.setColor(Color.WHITE);
            }
            g.drawString(gameContext.getGameContainer().getUiManager().getConfirmDialog().getItem(i), currentX, 350);
            currentX += g.getFontMetrics().stringWidth(gameContext.getGameContainer().getUiManager().getConfirmDialog().getItem(i)) + spacing;
        }
    }

    @Override
    public void onEnter() {
        // 이 상태에서는 사용하지 않음
    }

    @Override
    public void onExit() {
        // 이 상태에서는 사용하지 않음
    }
}
