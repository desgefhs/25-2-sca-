package org.newdawn.spaceinvaders.userinput;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.InputHandler;
import org.newdawn.spaceinvaders.userinput.command.Command;
import org.newdawn.spaceinvaders.userinput.command.MenuNavigateCommand;
import org.newdawn.spaceinvaders.userinput.command.ProcessPausedMenuSelectionCommand;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class PausedInputHandler {

    private final Map<Integer, Command> commandMap = new HashMap<>();

    public PausedInputHandler(GameContext gameContext) {
        // Map single-press keys to their respective commands
        commandMap.put(KeyEvent.VK_UP, new MenuNavigateCommand(() -> gameContext.getPauseMenu(), MenuNavigateCommand.Direction.UP));
        commandMap.put(KeyEvent.VK_DOWN, new MenuNavigateCommand(() -> gameContext.getPauseMenu(), MenuNavigateCommand.Direction.DOWN));
        commandMap.put(KeyEvent.VK_ENTER, new ProcessPausedMenuSelectionCommand(gameContext));
    }

    public void handle(InputHandler input) {
        for (Map.Entry<Integer, Command> entry : commandMap.entrySet()) {
            if (input.isPressedAndConsume(entry.getKey())) {
                entry.getValue().execute();
            }
        }
    }
}