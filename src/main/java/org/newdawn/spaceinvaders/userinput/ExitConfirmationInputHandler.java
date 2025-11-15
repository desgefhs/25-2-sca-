package org.newdawn.spaceinvaders.userinput;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.InputHandler;
import org.newdawn.spaceinvaders.userinput.command.Command;
import org.newdawn.spaceinvaders.userinput.command.MenuNavigateCommand;
import org.newdawn.spaceinvaders.userinput.command.ProcessConfirmDialogSelectionCommand;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class ExitConfirmationInputHandler {

    private final Map<Integer, Command> commandMap = new HashMap<>();

    public ExitConfirmationInputHandler(GameContext gameContext) {
        commandMap.put(KeyEvent.VK_LEFT, new MenuNavigateCommand(() -> gameContext.getConfirmDialog(), MenuNavigateCommand.Direction.LEFT));
        commandMap.put(KeyEvent.VK_RIGHT, new MenuNavigateCommand(() -> gameContext.getConfirmDialog(), MenuNavigateCommand.Direction.RIGHT));
        commandMap.put(KeyEvent.VK_ENTER, new ProcessConfirmDialogSelectionCommand(gameContext));
    }

    public void handle(InputHandler input) {
        for (Map.Entry<Integer, Command> entry : commandMap.entrySet()) {
            if (input.isPressedAndConsume(entry.getKey())) {
                entry.getValue().execute();
            }
        }
    }
}