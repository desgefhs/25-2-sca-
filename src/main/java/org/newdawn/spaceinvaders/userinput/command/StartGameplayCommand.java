package org.newdawn.spaceinvaders.userinput.command;

import org.newdawn.spaceinvaders.core.GameContext;

public class StartGameplayCommand implements Command {

    private final GameContext gameContext;

    public StartGameplayCommand(GameContext gameContext) {
        this.gameContext = gameContext;
    }

    @Override
    public void execute() {
        gameContext.startGameplay();
    }
}
