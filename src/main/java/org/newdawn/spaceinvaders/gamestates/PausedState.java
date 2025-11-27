package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameState;
import org.newdawn.spaceinvaders.core.InputHandler;
import org.newdawn.spaceinvaders.userinput.PausedInputHandler;

import java.awt.*;

/**
 * 게임 일시 정지 상태를 나타내는 클래스.
 * 현재 게임 화면 위에 반투명 오버레이와 함께 일시 정지 메뉴를 표시하고,
 * 사용자 입력을 통해 메뉴를 탐색합니다.
 */
public class PausedState implements GameState {
    private final GameContext gameContext;
    private final PausedInputHandler inputHandler;

    /**
     * PausedState 생성자.
     * @param gameContext 게임 컨텍스트
     */
    public PausedState(GameContext gameContext) {
        this.gameContext = gameContext;
        this.inputHandler = new PausedInputHandler(gameContext);
    }

    /**
     * 이 상태에서는 특별한 초기화가 필요하지 않습니다.
     */
    @Override
    public void init() {
        // 이 상태에서는 사용하지 않음
    }

    /**
     * 일시 정지 메뉴에 대한 사용자 입력을 처리합니다.
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
     * 일시 정지 화면을 렌더링합니다.
     * 하단에 게임 플레이 화면을 희미하게 렌더링하고, 반투명 오버레이와 일시 정지 메뉴를 표시합니다.
     * @param g 그리기를 수행할 그래픽 컨텍스트
     */
    @Override
    public void render(Graphics2D g) {
        // 배경에 게임 플레이 상태를 렌더링 (그 위에 메뉴를 그림)
        PlayingState playingState = gameContext.getGameContainer().getGsm().getPlayingState();
        if (playingState != null) {
            playingState.render(g);
        }

        // 반투명 오버레이 그리기
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);

        // 일시정지 메뉴 항목 그리기
        g.setFont(new Font("Dialog", Font.BOLD, 24));
        int itemHeight = 40;
        int startY = (Game.SCREEN_HEIGHT - (gameContext.getGameContainer().getUiManager().getPauseMenu().getItemCount() * itemHeight)) / 2;

        for (int i = 0; i < gameContext.getGameContainer().getUiManager().getPauseMenu().getItemCount(); i++) {
            if (i == gameContext.getGameContainer().getUiManager().getPauseMenu().getSelectedIndex()) {
                g.setColor(Color.GREEN); // 선택된 항목은 녹색으로 표시
            } else {
                g.setColor(Color.WHITE); // 선택되지 않은 항목은 흰색으로 표시
            }
            String itemText = gameContext.getGameContainer().getUiManager().getPauseMenu().getItem(i);
            int textWidth = g.getFontMetrics().stringWidth(itemText);
            g.drawString(itemText, (Game.SCREEN_WIDTH - textWidth) / 2, startY + (i * itemHeight));
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
