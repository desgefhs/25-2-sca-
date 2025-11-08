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
        if (input.isUpPressedAndConsume()) {
            commandMap.get(KeyEvent.VK_UP).execute();
        }
        if (input.isDownPressedAndConsume()) {
            commandMap.get(KeyEvent.VK_DOWN).execute();
        }
        if (input.isEnterPressedAndConsume()) {
            commandMap.get(KeyEvent.VK_ENTER).execute();
        }
    }
}