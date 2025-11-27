package org.newdawn.spaceinvaders.userinput.command;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameState;
import org.newdawn.spaceinvaders.view.ShopMainMenuView;

import java.util.HashMap;
import java.util.Map;

/**
 * 주 상점 메뉴(ShopMainMenuView)의 선택 사항을 처리하는 커맨드.
 * 캐릭터 강화, 아이템 뽑기, 뒤로가기 등의 액션을 실행합니다.
 */
public class ProcessShopMenuSelectionCommand implements Command {

    /** 게임의 전반적인 컨텍스트. */
    private final GameContext gameContext;
    /** 이 커맨드가 처리할 대상 메뉴 뷰. */
    private final ShopMainMenuView menuView;
    /** 메뉴 선택 항목과 그에 해당하는 커맨드를 매핑하는 맵. */
    private final Map<String, Command> selectionCommands = new HashMap<>();

    /**
     * ProcessShopMenuSelectionCommand 생성자.
     * @param gameContext 게임 컨텍스트
     * @param menuView 이 커맨드가 처리할 주 상점 메뉴 뷰
     */
    public ProcessShopMenuSelectionCommand(GameContext gameContext, ShopMainMenuView menuView) {
        this.gameContext = gameContext;
        this.menuView = menuView;
        mapSelectionCommands();
    }

    /**
     * 각 메뉴 선택 항목에 대한 커맨드를 초기화하고 매핑합니다.
     */
    private void mapSelectionCommands() {
        selectionCommands.put("캐릭터 강화", new GoToStateCommand(gameContext, GameState.Type.SHOP));
        selectionCommands.put("아이템 뽑기", new GoToStateCommand(gameContext, GameState.Type.ITEM_DRAW));
        selectionCommands.put("뒤로가기", new GoToStateCommand(gameContext, GameState.Type.MAIN_MENU));
    }

    /**
     * 주 상점 메뉴에서 현재 선택된 항목을 가져와 그에 매핑된 커맨드를 실행합니다.
     */
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
