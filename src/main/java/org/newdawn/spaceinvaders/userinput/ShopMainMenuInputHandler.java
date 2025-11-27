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

/**
 * 주 상점 메뉴(ShopMainMenuView)에서의 사용자 입력을 처리하는 클래스.
 * 상/하 방향키, 엔터, ESC 키 입력에 대한 커맨드를 실행합니다.
 */
public class ShopMainMenuInputHandler {

    /** 키 코드와 커맨드를 매핑하는 맵. */
    private final Map<Integer, Command> commandMap = new HashMap<>();

    /**
     * ShopMainMenuInputHandler 생성자.
     * 키 입력에 따른 커맨드를 매핑합니다.
     * @param gameContext 게임 컨텍스트
     * @param menuView 이 핸들러가 제어하는 주 상점 메뉴 뷰
     */
    public ShopMainMenuInputHandler(GameContext gameContext, ShopMainMenuView menuView) {
        commandMap.put(KeyEvent.VK_UP, new MenuNavigateCommand(() -> menuView, MenuNavigateCommand.Direction.UP));
        commandMap.put(KeyEvent.VK_DOWN, new MenuNavigateCommand(() -> menuView, MenuNavigateCommand.Direction.DOWN));
        commandMap.put(KeyEvent.VK_ENTER, new ProcessShopMenuSelectionCommand(gameContext, menuView));
        commandMap.put(KeyEvent.VK_ESCAPE, new GoToStateCommand(gameContext, GameState.Type.MAIN_MENU));
    }

    /**
     * 입력을 받아 처리합니다.
     * 매핑된 키가 눌렸는지 확인하고, 해당하는 커맨드를 실행합니다.
     * @param input 게임의 주 입력 핸들러
     */
    public void handle(InputHandler input) {
        for (Map.Entry<Integer, Command> entry : commandMap.entrySet()) {
            if (input.isPressedAndConsume(entry.getKey())) {
                entry.getValue().execute();
            }
        }
    }
}