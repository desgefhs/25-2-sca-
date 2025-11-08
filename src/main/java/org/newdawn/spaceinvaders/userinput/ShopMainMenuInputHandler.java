package org.newdawn.spaceinvaders.userinput;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameState;
import org.newdawn.spaceinvaders.core.InputHandler;
import org.newdawn.spaceinvaders.userinput.command.Command;
import org.newdawn.spaceinvaders.userinput.command.GoToStateCommand;
import org.newdawn.spaceinvaders.userinput.command.MenuNavigateCommand;
import org.newdawn.spaceinvaders.userinput.command.ProcessShopMenuSelectionCommand;
import org.newdawn.spaceinvaders.view.ShopMainMenuView;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class ShopMainMenuInputHandler {

    private final Map<Integer, Command> commandMap = new HashMap<>();

    public ShopMainMenuInputHandler(GameContext gameContext, ShopMainMenuView menuView) {
        commandMap.put(KeyEvent.VK_UP, new MenuNavigateCommand(() -> menuView, MenuNavigateCommand.Direction.UP));
        commandMap.put(KeyEvent.VK_DOWN, new MenuNavigateCommand(() -> menuView, MenuNavigateCommand.Direction.DOWN));
        commandMap.put(KeyEvent.VK_ENTER, new ProcessShopMenuSelectionCommand(gameContext, menuView));
        commandMap.put(KeyEvent.VK_ESCAPE, new GoToStateCommand(gameContext, GameState.Type.MAIN_MENU));
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
        if (input.isEscPressedAndConsume()) {
            commandMap.get(KeyEvent.VK_ESCAPE).execute();
        }
    }
}