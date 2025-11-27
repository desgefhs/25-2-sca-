package org.newdawn.spaceinvaders.userinput.command;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameState;
import org.newdawn.spaceinvaders.view.MainMenu;

import java.util.HashMap;
import java.util.Map;

public class ProcessMainMenuSelectionCommand implements Command {

    private final GameContext gameContext;
    private final Map<String, Command> selectionCommands = new HashMap<>();

    public ProcessMainMenuSelectionCommand(GameContext gameContext) {
        this.gameContext = gameContext;
        mapSelectionCommands();
    }

    private void mapSelectionCommands() {
        selectionCommands.put("1. 게임시작", new StartGameplayCommand(gameContext));
        selectionCommands.put("2. 랭킹", new GoToStateCommand(gameContext, GameState.Type.RANKING));
        selectionCommands.put("3. 무기", new GoToStateCommand(gameContext, GameState.Type.WEAPON_MENU));
        selectionCommands.put("4. 펫", new GoToStateCommand(gameContext, GameState.Type.PET_MENU));
        selectionCommands.put("5. 상점", new GoToStateCommand(gameContext, GameState.Type.SHOP_MAIN_MENU));
        selectionCommands.put("6. 설정", () -> System.exit(0)); // 단순한 액션은 람다 사용
    }

    @Override
    public void execute() {
        MainMenu mainMenu = gameContext.getGameContainer().getUiManager().getMainMenu();
        if (mainMenu == null) {
            return;
        }
        String selectedItem = mainMenu.getSelectedItem();
        
        Command command = selectionCommands.get(selectedItem);
        if (command != null) {
            gameContext.getGameContainer().getSoundManager().playSound("buttonselect");
            command.execute();
        }
    }
}
