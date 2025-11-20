package org.newdawn.spaceinvaders.userinput.command;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameState;
import org.newdawn.spaceinvaders.view.GameOverMenu;

import java.util.HashMap;
import java.util.Map;

public class ProcessGameOverMenuSelectionCommand implements Command {

    private final GameContext gameContext;
    private final Map<String, Command> selectionCommands = new HashMap<>();

    public ProcessGameOverMenuSelectionCommand(GameContext gameContext) {
        this.gameContext = gameContext;
        mapSelectionCommands();
    }

    private void mapSelectionCommands() {
        selectionCommands.put("다시하기", new StartGameplayCommand(gameContext));
        selectionCommands.put("메인 메뉴로", new GoToStateCommand(gameContext, GameState.Type.MAIN_MENU));
    }

    @Override
    public void execute() {
        GameOverMenu menu = gameContext.getGameContainer().getUiManager().getGameOverMenu();
        if (menu == null) {
            return;
        }
        String selectedItem = menu.getSelectedItem();
        
        Command command = selectionCommands.get(selectedItem);
        if (command != null) {
            gameContext.getGameContainer().getSoundManager().playSound("buttonselect");
            command.execute();
        }
    }
}
