package org.newdawn.spaceinvaders.userinput;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.InputHandler;
import org.newdawn.spaceinvaders.userinput.command.Command;
import org.newdawn.spaceinvaders.userinput.command.MenuNavigateCommand;
import org.newdawn.spaceinvaders.userinput.command.ProcessGameOverMenuSelectionCommand;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class GameOverInputHandler {

    private final Map<Integer, Command> commandMap = new HashMap<>();

    public GameOverInputHandler(GameContext gameContext) {
        commandMap.put(KeyEvent.VK_LEFT, new MenuNavigateCommand(() -> gameContext.getGameOverMenu(), MenuNavigateCommand.Direction.LEFT));
        commandMap.put(KeyEvent.VK_RIGHT, new MenuNavigateCommand(() -> gameContext.getGameOverMenu(), MenuNavigateCommand.Direction.RIGHT));
        commandMap.put(KeyEvent.VK_ENTER, new ProcessGameOverMenuSelectionCommand(gameContext));
    }

    public void handle(InputHandler input) {
        if (input.isLeftPressedAndConsume()) {
            commandMap.get(KeyEvent.VK_LEFT).execute();
        }
        if (input.isRightPressedAndConsume()) {
            commandMap.get(KeyEvent.VK_RIGHT).execute();
        }
        if (input.isEnterPressedAndConsume()) {
            commandMap.get(KeyEvent.VK_ENTER).execute();
        }
    }
}