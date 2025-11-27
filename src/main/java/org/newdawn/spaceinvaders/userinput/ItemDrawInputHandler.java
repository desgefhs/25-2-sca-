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

/**
 * 아이템 뽑기 화면(ItemDrawView)에서의 사용자 입력을 처리하는 클래스.
 * 상/하 방향키, 엔터, ESC 키 입력에 따라 다른 커맨드를 실행합니다.
 */
public class ItemDrawInputHandler {

    /** 키 코드와 커맨드를 매핑하는 맵. */
    private final Map<Integer, Command> commandMap = new HashMap<>();
    /** 게임의 전반적인 컨텍스트. */
    private final GameContext gameContext;
    /** 이 핸들러가 제어하는 아이템 뽑기 뷰. */
    private final ItemDrawView itemView;

    /**
     * ItemDrawInputHandler 생성자.
     * @param gameContext 게임 컨텍스트
     * @param itemView 아이템 뽑기 뷰
     */
    public ItemDrawInputHandler(GameContext gameContext, ItemDrawView itemView) {
        this.gameContext = gameContext;
        this.itemView = itemView;
        mapCommands();
    }

    /**
     * 각 키 입력에 대한 커맨드를 초기화하고 매핑합니다.
     * 엔터 키는 선택된 메뉴 항목에 따라 조건부 로직을 가집니다.
     */
    private void mapCommands() {
        commandMap.put(KeyEvent.VK_UP, new MenuNavigateCommand(() -> itemView, MenuNavigateCommand.Direction.UP));
        commandMap.put(KeyEvent.VK_DOWN, new MenuNavigateCommand(() -> itemView, MenuNavigateCommand.Direction.DOWN));
        commandMap.put(KeyEvent.VK_ESCAPE, new GoToStateCommand(gameContext, GameState.Type.SHOP_MAIN_MENU));
        
        // 엔터 키는 선택된 항목에 따라 다른 커맨드를 실행하도록 람다로 처리
        commandMap.put(KeyEvent.VK_ENTER, () -> {
            if (itemView.getSelectedIndex() == 0) { // "아이템 뽑기"
                new DrawItemCommand(gameContext).execute();
            } else { // "뒤로가기"
                new GoToStateCommand(gameContext, GameState.Type.SHOP_MAIN_MENU).execute();
            }
        });
    }

    /**
     * 입력을 받아 처리합니다.
     * 매핑된 키가 눌렸는지 확인하고, 해당하는 커맨드를 실행합니다.
     * @param input 게임의 주 입력 핸들러
     */
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
