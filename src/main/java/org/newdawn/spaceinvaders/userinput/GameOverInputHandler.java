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
        for (Map.Entry<Integer, Command> entry : commandMap.entrySet()) {
            if (input.isPressedAndConsume(entry.getKey())) {
                entry.getValue().execute();
            }
        }
    }
}