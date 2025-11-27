package org.newdawn.spaceinvaders.userinput.command;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameState;
import org.newdawn.spaceinvaders.view.GameOverMenu;

import java.util.HashMap;
import java.util.Map;

/**
 * 게임 오버 메뉴(GameOverMenu)의 선택 사항을 처리하는 커맨드.
 * "다시하기" 또는 "메인 메뉴로" 선택에 따라 다른 액션을 실행합니다.
 */
public class ProcessGameOverMenuSelectionCommand implements Command {

    /** 게임의 전반적인 컨텍스트. */
    private final GameContext gameContext;
    /** 메뉴 선택 항목과 그에 해당하는 커맨드를 매핑하는 맵. */
    private final Map<String, Command> selectionCommands = new HashMap<>();

    /**
     * ProcessGameOverMenuSelectionCommand 생성자.
     * @param gameContext 게임 컨텍스트
     */
    public ProcessGameOverMenuSelectionCommand(GameContext gameContext) {
        this.gameContext = gameContext;
        mapSelectionCommands();
    }

    /**
     * 각 메뉴 선택 항목에 대한 커맨드를 초기화하고 매핑합니다.
     * "다시하기"는 게임 플레이를 다시 시작하고, "메인 메뉴로"는 메인 메뉴 상태로 전환합니다.
     */
    private void mapSelectionCommands() {
        selectionCommands.put("다시하기", new StartGameplayCommand(gameContext));
        selectionCommands.put("메인 메뉴로", new GoToStateCommand(gameContext, GameState.Type.MAIN_MENU));
    }

    /**
     * 게임 오버 메뉴에서 현재 선택된 항목을 가져와 그에 매핑된 커맨드를 실행합니다.
     */
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
