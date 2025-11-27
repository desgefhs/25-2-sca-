package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameState;
import org.newdawn.spaceinvaders.core.InputHandler;
import org.newdawn.spaceinvaders.userinput.ExitConfirmationInputHandler;

import java.awt.*;

/**
 * 게임 종료 확인 대화 상자를 표시하고 사용자 입력을 처리하는 게임 상태.
 * 메인 메뉴 위에 반투명 오버레이와 함께 확인 대화 상자를 렌더링합니다.
 */
public class ExitConfirmationState implements GameState {
    private final GameContext gameContext;
    private final ExitConfirmationInputHandler inputHandler;

    /**
     * ExitConfirmationState 생성자.
     * @param gameContext 게임 컨텍스트
     */
    public ExitConfirmationState(GameContext gameContext) {
        this.gameContext = gameContext;
        this.inputHandler = new ExitConfirmationInputHandler(gameContext);
    }

    /**
     * 이 상태에서는 특별한 초기화가 필요하지 않습니다.
     */
    @Override
    public void init() {
        // 이 상태에서는 사용하지 않음
    }

    /**
     * 종료 확인 대화 상자에 대한 사용자 입력을 처리합니다.
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
     * 종료 확인 대화 상자를 화면에 렌더링합니다.
     * 메인 메뉴 위에 반투명 오버레이를 그린 후 대화 상자를 중앙에 표시합니다.
     * @param g 그리기를 수행할 그래픽 컨텍스트
     */
    @Override
    public void render(Graphics2D g) {
        // 하단에 메인 메뉴 렌더링 (그 위에 대화 상자를 그림)
        MainMenuState mainMenuState = gameContext.getGameContainer().getGsm().getMainMenuState();
        if (mainMenuState != null) {
            mainMenuState.render(g);
        }

        // 반투명 오버레이 그리기
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);

        // 대화 상자 박스 그리기
        g.setColor(Color.BLACK);
        g.fillRect(200, 200, 400, 200);
        g.setColor(Color.WHITE);
        g.drawRect(200, 200, 400, 200);

        // 메시지 그리기
        g.setFont(new Font("Dialog", Font.BOLD, 20));
        g.drawString(gameContext.getGameContainer().getUiManager().getConfirmDialog().getMessage(), (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth(gameContext.getGameContainer().getUiManager().getConfirmDialog().getMessage())) / 2, 260);

        // 버튼 그리기
        g.setFont(new Font("Dialog", Font.BOLD, 24));
        int totalWidth = 0;
        int spacing = 80;

        for (int i = 0; i < gameContext.getGameContainer().getUiManager().getConfirmDialog().getItemCount(); i++) {
            totalWidth += g.getFontMetrics().stringWidth(gameContext.getGameContainer().getUiManager().getConfirmDialog().getItem(i));
        }
        totalWidth += (gameContext.getGameContainer().getUiManager().getConfirmDialog().getItemCount() - 1) * spacing;

        int currentX = (Game.SCREEN_WIDTH - totalWidth) / 2;

        for (int i = 0; i < gameContext.getGameContainer().getUiManager().getConfirmDialog().getItemCount(); i++) {
            if (i == gameContext.getGameContainer().getUiManager().getConfirmDialog().getSelectedIndex()) {
                g.setColor(Color.GREEN);
            } else {
                g.setColor(Color.WHITE);
            }
            g.drawString(gameContext.getGameContainer().getUiManager().getConfirmDialog().getItem(i), currentX, 350);
            currentX += g.getFontMetrics().stringWidth(gameContext.getGameContainer().getUiManager().getConfirmDialog().getItem(i)) + spacing;
        }
    }

    /**
     * 이 상태에 진입할 때 특별한 로직이 필요하지 않습니다.
     */
    @Override
    public void onEnter() {
        // 이 상태에서는 사용하지 않음
    }

    /**
     * 이 상태를 벗어날 때 특별한 로직이 필요하지 않습니다.
     */
    @Override
    public void onExit() {
        // 이 상태에서는 사용하지 않음
    }
}
