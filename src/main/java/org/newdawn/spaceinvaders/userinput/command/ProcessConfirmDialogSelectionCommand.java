package org.newdawn.spaceinvaders.userinput.command;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameState;
import org.newdawn.spaceinvaders.view.ConfirmDialog;

import java.util.HashMap;
import java.util.Map;

/**
 * 확인 대화 상자(ConfirmDialog)의 선택 사항을 처리하는 커맨드.
 * "Confirm" 또는 "Cancel" 선택에 따라 다른 액션을 실행합니다.
 */
public class ProcessConfirmDialogSelectionCommand implements Command {

    /** 게임의 전반적인 컨텍스트. */
    private final GameContext gameContext;
    /** 메뉴 선택 항목과 그에 해당하는 커맨드를 매핑하는 맵. */
    private final Map<String, Command> selectionCommands = new HashMap<>();

    /**
     * ProcessConfirmDialogSelectionCommand 생성자.
     * @param gameContext 게임 컨텍스트
     */
    public ProcessConfirmDialogSelectionCommand(GameContext gameContext) {
        this.gameContext = gameContext;
        mapSelectionCommands();
    }

    /**
     * 각 메뉴 선택 항목에 대한 커맨드를 초기화하고 매핑합니다.
     * "Confirm"은 게임을 종료하고, "Cancel"은 메인 메뉴로 돌아갑니다.
     */
    private void mapSelectionCommands() {
        selectionCommands.put("Confirm", () -> System.exit(0));
        selectionCommands.put("Cancel", new GoToStateCommand(gameContext, GameState.Type.MAIN_MENU));
    }

    /**
     * 확인 대화 상자에서 현재 선택된 항목을 가져와 그에 매핑된 커맨드를 실행합니다.
     */
    @Override
    public void execute() {
        ConfirmDialog menu = gameContext.getGameContainer().getUiManager().getConfirmDialog();
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
