package org.newdawn.spaceinvaders.userinput.command;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameState;
import org.newdawn.spaceinvaders.view.ShopMainMenuView;

import java.util.HashMap;
import java.util.Map;

public class ProcessShopMenuSelectionCommand implements Command {

    private final GameContext gameContext;
    // This command is specific to ShopMainMenuView, so we can reference it directly.
    private final ShopMainMenuView menuView; 
    private final Map<String, Command> selectionCommands = new HashMap<>();

    public ProcessShopMenuSelectionCommand(GameContext gameContext, ShopMainMenuView menuView) {
        this.gameContext = gameContext;
        this.menuView = menuView;
        mapSelectionCommands();
    }

    private void mapSelectionCommands() {
        selectionCommands.put("캐릭터 강화", new GoToStateCommand(gameContext, GameState.Type.SHOP));
        selectionCommands.put("아이템 뽑기", new GoToStateCommand(gameContext, GameState.Type.ITEM_DRAW));
        selectionCommands.put("뒤로가기", new GoToStateCommand(gameContext, GameState.Type.MAIN_MENU));
    }

    @Override
    public void execute() {
        if (menuView == null) {
            return;
        }
        String selectedItem = menuView.getSelectedItem();
        
        Command command = selectionCommands.get(selectedItem);
        if (command != null) {
            gameContext.getGameContainer().getSoundManager().playSound("buttonselect");
            command.execute();
        }
    }
}
