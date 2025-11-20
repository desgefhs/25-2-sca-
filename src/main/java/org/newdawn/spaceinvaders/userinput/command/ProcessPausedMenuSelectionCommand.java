package org.newdawn.spaceinvaders.userinput.command;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameState;
import org.newdawn.spaceinvaders.view.PauseMenu;

public class ProcessPausedMenuSelectionCommand implements Command {

    private final GameContext gameContext;

    public ProcessPausedMenuSelectionCommand(GameContext gameContext) {
        this.gameContext = gameContext;
    }

    @Override
    public void execute() {
        PauseMenu menu = gameContext.getGameContainer().getUiManager().getPauseMenu();
        if (menu == null) {
            return;
        }
        String selectedItem = menu.getSelectedItem();

        gameContext.getGameContainer().getSoundManager().playSound("buttonselect");

        switch (selectedItem) {
            case "재개하기":
                gameContext.setCurrentState(GameState.Type.PLAYING);
                break;
            case "메인메뉴로 나가기":
                gameContext.getGameContainer().getPlayerManager().saveGameResults();
                gameContext.setCurrentState(GameState.Type.MAIN_MENU);
                break;
            case "종료하기":
                System.exit(0);
                break;
        }
    }
}
