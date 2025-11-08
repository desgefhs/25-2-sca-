package org.newdawn.spaceinvaders.userinput;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameState;
import org.newdawn.spaceinvaders.core.InputHandler;
import org.newdawn.spaceinvaders.userinput.command.Command;
import org.newdawn.spaceinvaders.userinput.command.GoToStateCommand;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class RankingInputHandler {

    private final Map<Integer, Command> commandMap = new HashMap<>();
    private final GameContext gameContext; // Field added

    public RankingInputHandler(GameContext gameContext) {
        this.gameContext = gameContext; // Initialize field
        commandMap.put(KeyEvent.VK_ENTER, new GoToStateCommand(gameContext, GameState.Type.MAIN_MENU));
    }

    public void handle(InputHandler input) {
        if (input.isEnterPressedAndConsume()) {
            gameContext.getSoundManager().playSound("buttonselect");
            commandMap.get(KeyEvent.VK_ENTER).execute();
        }
    }
}