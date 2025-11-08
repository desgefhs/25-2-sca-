package org.newdawn.spaceinvaders.userinput;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameState;
import org.newdawn.spaceinvaders.core.InputHandler;
import org.newdawn.spaceinvaders.userinput.command.Command;
import org.newdawn.spaceinvaders.userinput.command.GoToStateCommand;
import org.newdawn.spaceinvaders.userinput.command.MenuNavigateCommand;
import org.newdawn.spaceinvaders.userinput.command.ProcessMainMenuSelectionCommand;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class MainMenuInputHandler {

    private final Map<Integer, Command> commandMap = new HashMap<>();

    public MainMenuInputHandler(GameContext gameContext) {
        commandMap.put(KeyEvent.VK_LEFT, new MenuNavigateCommand(() -> gameContext.getMainMenu(), MenuNavigateCommand.Direction.LEFT));
        commandMap.put(KeyEvent.VK_RIGHT, new MenuNavigateCommand(() -> gameContext.getMainMenu(), MenuNavigateCommand.Direction.RIGHT));
        commandMap.put(KeyEvent.VK_ENTER, new ProcessMainMenuSelectionCommand(gameContext));
        commandMap.put(KeyEvent.VK_ESCAPE, new GoToStateCommand(gameContext, GameState.Type.EXIT_CONFIRMATION));
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
        if (input.isEscPressedAndConsume()) {
            commandMap.get(KeyEvent.VK_ESCAPE).execute();
        }
    }
}