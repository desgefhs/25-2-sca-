package org.newdawn.spaceinvaders.userinput;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameState;
import org.newdawn.spaceinvaders.core.InputHandler;
import org.newdawn.spaceinvaders.userinput.command.Command;
import org.newdawn.spaceinvaders.userinput.command.DrawItemCommand;
import org.newdawn.spaceinvaders.userinput.command.GoToStateCommand;
import org.newdawn.spaceinvaders.userinput.command.MenuNavigateCommand;
import org.newdawn.spaceinvaders.view.ItemDrawView;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class ItemDrawInputHandler {

    private final Map<Integer, Command> commandMap = new HashMap<>();
    private final GameContext gameContext;
    private final ItemDrawView itemView;

    public ItemDrawInputHandler(GameContext gameContext, ItemDrawView itemView) {
        this.gameContext = gameContext;
        this.itemView = itemView;
        mapCommands();
    }

    private void mapCommands() {
        commandMap.put(KeyEvent.VK_UP, new MenuNavigateCommand(() -> itemView, MenuNavigateCommand.Direction.UP));
        commandMap.put(KeyEvent.VK_DOWN, new MenuNavigateCommand(() -> itemView, MenuNavigateCommand.Direction.DOWN));
        commandMap.put(KeyEvent.VK_ESCAPE, new GoToStateCommand(gameContext, GameState.Type.SHOP_MAIN_MENU));
        
        // The Enter key has conditional logic, so we handle it with a lambda
        // that dispatches to the correct command.
        commandMap.put(KeyEvent.VK_ENTER, () -> {
            if (itemView.getSelectedIndex() == 0) { // "아이템 뽑기"
                new DrawItemCommand(gameContext).execute();
            } else { // "뒤로가기"
                new GoToStateCommand(gameContext, GameState.Type.SHOP_MAIN_MENU).execute();
            }
        });
    }

    public void handle(InputHandler input) {
        if (input.isPressedAndConsume(KeyEvent.VK_UP)) {
            commandMap.get(KeyEvent.VK_UP).execute();
        }
        if (input.isPressedAndConsume(KeyEvent.VK_DOWN)) {
            commandMap.get(KeyEvent.VK_DOWN).execute();
        }
        if (input.isPressedAndConsume(KeyEvent.VK_ENTER)) {
            gameContext.getGameContainer().getSoundManager().playSound("buttonselect");
            commandMap.get(KeyEvent.VK_ENTER).execute();
        }
        if (input.isPressedAndConsume(KeyEvent.VK_ESCAPE)) {
            commandMap.get(KeyEvent.VK_ESCAPE).execute();
        }
    }
}
