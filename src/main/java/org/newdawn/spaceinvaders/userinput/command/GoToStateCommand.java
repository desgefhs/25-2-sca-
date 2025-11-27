package org.newdawn.spaceinvaders.userinput.command;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameState;

public class GoToStateCommand implements Command {

    private final GameContext gameContext;
    private final GameState.Type stateType;

    public GoToStateCommand(GameContext gameContext, GameState.Type stateType) {
        this.gameContext = gameContext;
        this.stateType = stateType;
    }

    @Override
    public void execute() {
        gameContext.setCurrentState(stateType);
    }
}
