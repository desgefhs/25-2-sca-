package org.newdawn.spaceinvaders.userinput.command;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameState;
import org.newdawn.spaceinvaders.view.MainMenu;

import java.util.HashMap;
import java.util.Map;

/**
 * 메인 메뉴(MainMenu)의 선택 사항을 처리하는 커맨드.
 * 각 메뉴 아이템(게임 시작, 랭킹, 종료 등)에 따라 다른 커맨드를 실행합니다.
 */
public class ProcessMainMenuSelectionCommand implements Command {

    /** 게임의 전반적인 컨텍스트. */
    private final GameContext gameContext;
    /** 메뉴 선택 항목과 그에 해당하는 커맨드를 매핑하는 맵. */
    private final Map<String, Command> selectionCommands = new HashMap<>();

    /**
     * ProcessMainMenuSelectionCommand 생성자.
     * @param gameContext 게임 컨텍스트
     */
    public ProcessMainMenuSelectionCommand(GameContext gameContext) {
        this.gameContext = gameContext;
        mapSelectionCommands();
    }

    /**
     * 각 메뉴 선택 항목에 대한 커맨드를 초기화하고 매핑합니다.
     */
    private void mapSelectionCommands() {
        selectionCommands.put("1. 게임시작", new StartGameplayCommand(gameContext));
        selectionCommands.put("2. 랭킹", new GoToStateCommand(gameContext, GameState.Type.RANKING));
        selectionCommands.put("3. 무기", new GoToStateCommand(gameContext, GameState.Type.WEAPON_MENU));
        selectionCommands.put("4. 펫", new GoToStateCommand(gameContext, GameState.Type.PET_MENU));
        selectionCommands.put("5. 상점", new GoToStateCommand(gameContext, GameState.Type.SHOP_MAIN_MENU));
        selectionCommands.put("6. 설정", () -> System.exit(0)); // 단순한 액션은 람다 사용
    }

    /**
     * 메인 메뉴에서 현재 선택된 항목을 가져와 그에 매핑된 커맨드를 실행합니다.
     */
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
