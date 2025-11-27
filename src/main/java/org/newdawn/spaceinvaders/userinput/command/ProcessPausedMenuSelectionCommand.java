package org.newdawn.spaceinvaders.userinput.command;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameState;
import org.newdawn.spaceinvaders.view.PauseMenu;

/**
 * 일시 정지 메뉴(PauseMenu)의 선택 사항을 처리하는 커맨드.
 * 게임 재개, 메인 메뉴로 돌아가기, 게임 종료 등의 액션을 실행합니다.
 */
public class ProcessPausedMenuSelectionCommand implements Command {

    /** 게임의 전반적인 컨텍스트. */
    private final GameContext gameContext;

    /**
     * ProcessPausedMenuSelectionCommand 생성자.
     * @param gameContext 게임 컨텍스트
     */
    public ProcessPausedMenuSelectionCommand(GameContext gameContext) {
        this.gameContext = gameContext;
    }

    /**
     * 일시 정지 메뉴에서 현재 선택된 항목에 따라 적절한 액션을 수행합니다.
     * <ul>
     *     <li>"재개하기": 게임 플레이 상태로 돌아갑니다.</li>
     *     <li>"메인메뉴로 나가기": 현재 게임 결과를 저장하고 메인 메뉴로 돌아갑니다.</li>
     *     <li>"종료하기": 게임을 종료합니다.</li>
     * </ul>
     */
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
            default:
                // 아무것도 하지 않음
                break;
        }
    }
}
