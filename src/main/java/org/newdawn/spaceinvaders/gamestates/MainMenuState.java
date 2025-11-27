package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameState;
import org.newdawn.spaceinvaders.core.InputHandler;
import org.newdawn.spaceinvaders.userinput.MainMenuInputHandler;
import org.newdawn.spaceinvaders.view.MainMenu;

import java.awt.*;

/**
 * 게임의 메인 메뉴 화면을 담당하는 게임 상태.
 * 메뉴 항목을 표시하고, 사용자 입력을 통해 메뉴를 탐색하며, 메인 메뉴 배경 음악을 관리합니다.
 */
public class MainMenuState implements GameState {

    private final GameContext gameContext;
    private final MainMenu mainMenu;
    private final MainMenuInputHandler inputHandler;

    /**
     * MainMenuState 생성자.
     * @param gameContext 게임 컨텍스트
     */
    public MainMenuState(GameContext gameContext) {
        this.gameContext = gameContext;
        this.mainMenu = gameContext .getGameContainer().getUiManager().getMainMenu();
        this.inputHandler = new MainMenuInputHandler(gameContext);
    }

    /**
     * 이 상태에서는 특별한 초기화가 필요하지 않습니다.
     */
    @Override
    public void init() {
        // 이 상태에서는 사용하지 않음
    }

    /**
     * 메인 메뉴에 대한 사용자 입력을 처리합니다.
     * @param input 현재 키 상태를 제공하는 입력 핸들러
     */
    @Override
    public void handleInput(InputHandler input) {
        inputHandler.handle(input);
    }

    /**
     * 이 상태에서는 특별한 업데이트 로직이 필요하지 않습니다.
     * @param delta 마지막 업데이트 이후 경과 시간
     */
    @Override
    public void update(long delta) {
        // 이 상태에서는 사용하지 않음
    }

    /**
     * 메인 메뉴 화면을 렌더링합니다.
     * 정적 배경 이미지를 그리고, 메뉴 항목들을 화면 하단에 표시합니다.
     * @param g 그리기를 수행할 그래픽 컨텍스트
     */
    @Override
    public void render(Graphics2D g) {
        // 정적 배경 이미지 그리기
        g.drawImage(gameContext.getStaticBackgroundSprite().getImage(), 0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT, null);

        // 메뉴 항목 그리기
        g.setFont(new Font("Dialog", Font.BOLD, 24));
        int totalWidth = 0;
        int spacing = 40;

        for (int i = 0; i < mainMenu.getItemCount(); i++) {
            totalWidth += g.getFontMetrics().stringWidth(mainMenu.getItem(i));
        }
        totalWidth += (mainMenu.getItemCount() - 1) * spacing;

        int currentX = (Game.SCREEN_WIDTH - totalWidth) / 2;

        for (int i = 0; i < mainMenu.getItemCount(); i++) {
            if (i == mainMenu.getSelectedIndex()) {
                g.setColor(Color.GREEN); // 선택된 항목은 녹색으로 표시
            } else {
                g.setColor(Color.WHITE); // 선택되지 않은 항목은 흰색으로 표시
            }
            g.drawString(mainMenu.getItem(i), currentX, 500);
            currentX += g.getFontMetrics().stringWidth(mainMenu.getItem(i)) + spacing;
        }
    }

    /**
     * 이 상태에 진입할 때 호출됩니다.
     * 메인 메뉴 배경 음악을 반복 재생합니다.
     */
    @Override
    public void onEnter() {
        gameContext.getGameContainer().getSoundManager().loopSound("menubackground");
    }

    /**
     * 이 상태를 벗어날 때 특별한 로직이 필요하지 않습니다.
     */
    @Override
    public void onExit() {
        // 이 상태에서는 사용하지 않음
    }
}