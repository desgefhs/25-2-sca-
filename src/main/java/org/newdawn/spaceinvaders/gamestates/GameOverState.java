package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameState;
import org.newdawn.spaceinvaders.core.InputHandler;
import org.newdawn.spaceinvaders.userinput.GameOverInputHandler;

import java.awt.*;

/**
 * 게임 오버 상태를 나타내는 클래스.
 * 게임의 승리 또는 패배 조건을 표시하고, 플레이어에게 다시 시작하거나 메인 메뉴로 돌아갈 수 있는
 * 옵션을 제공합니다.
 */
public class GameOverState implements GameState {
    private final GameContext gameContext;
    private final boolean gameWon; // 게임 승리 여부
    private final GameOverInputHandler inputHandler;

    /**
     * GameOverState 생성자.
     * @param gameContext 게임 컨텍스트
     * @param gameWon 게임 승리 여부 (true면 승리, false면 패배)
     */
    public GameOverState(GameContext gameContext, boolean gameWon) {
        this.gameContext = gameContext;
        this.gameWon = gameWon;
        this.inputHandler = new GameOverInputHandler(gameContext);
    }

    /**
     * 이 상태에서는 특별한 초기화가 필요하지 않습니다.
     */
    @Override
    public void init() {
        // 이 상태에서는 사용하지 않음
    }

    /**
     * 게임 오버 메뉴에 대한 사용자 입력을 처리합니다.
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
     * 게임 오버 화면을 렌더링합니다.
     * 배경에 게임 플레이 화면을 희미하게 렌더링하고, 게임 오버 메시지와 메뉴를 표시합니다.
     * @param g 그리기를 수행할 그래픽 컨텍스트
     */
    @Override
    public void render(Graphics2D g) {
        // 배경에 게임 플레이 상태를 렌더링 (그 위에 메뉴를 그림)
        PlayingState playingState = gameContext.getGameContainer().getGsm().getPlayingState();
        if (playingState != null) {
            playingState.render(g);
        }

        // 메시지 및 게임 오버 메뉴 그리기
        if (gameContext.getMessage() != null && !gameContext.getMessage().isEmpty()) {
            g.setColor(Color.white);
            g.setFont(new Font("Dialog", Font.BOLD, 20));
            g.drawString(gameContext.getMessage(), (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth(gameContext.getMessage())) / 2, 250);
        }

        g.setFont(new Font("Dialog", Font.BOLD, 24));
        int totalWidth = 0;
        int spacing = 40;
        for (int i = 0; i < gameContext.getGameContainer().getUiManager().getGameOverMenu().getItemCount(); i++) {
            totalWidth += g.getFontMetrics().stringWidth(gameContext.getGameContainer().getUiManager().getGameOverMenu().getItem(i));
        }
        totalWidth += (gameContext.getGameContainer().getUiManager().getGameOverMenu().getItemCount() - 1) * spacing;
        int currentX = (Game.SCREEN_WIDTH - totalWidth) / 2;

        for (int i = 0; i < gameContext.getGameContainer().getUiManager().getGameOverMenu().getItemCount(); i++) {
            if (i == gameContext.getGameContainer().getUiManager().getGameOverMenu().getSelectedIndex()) {
                g.setColor(Color.GREEN);
            } else {
                g.setColor(Color.WHITE);
            }
            g.drawString(gameContext.getGameContainer().getUiManager().getGameOverMenu().getItem(i), currentX, 350);
            currentX += g.getFontMetrics().stringWidth(gameContext.getGameContainer().getUiManager().getGameOverMenu().getItem(i)) + spacing;
        }
    }

    /**
     * 이 상태에 진입할 때 호출됩니다.
     * 게임 승리 여부에 따라 적절한 메시지를 설정하고, 게임 패배 시 결과를 저장합니다.
     */
    @Override
    public void onEnter() {
        if (gameWon) {
            gameContext.setMessage("Well done! You Win!");
        } else {
            gameContext.getGameContainer().getPlayerManager().saveGameResults();
            long finalCredit = gameContext.getGameContainer().getPlayerManager().getCurrentPlayer().getCredit();
            gameContext.setMessage(String.format("이번 라운드 점수: %d / 최종 크레딧: %d", gameContext.getGameContainer().getPlayerManager().getScore(), finalCredit));
        }
    }

    /**
     * 이 상태를 벗어날 때 호출됩니다.
     * 표시되던 메시지를 지웁니다.
     */
    @Override
    public void onExit() {
        gameContext.setMessage("");
    }
}