package org.newdawn.spaceinvaders.core;

import org.newdawn.spaceinvaders.core.GameStateManager;
import org.newdawn.spaceinvaders.view.GameWindow;

import java.awt.Graphics2D;

/**
 * 게임의 메인 루프를 담당하는 클래스.
 * 정해진 주기에 따라 입력 처리, 게임 상태 업데이트, 렌더링을 반복적으로 수행합니다.
 */
public class GameLoop {

    /** 게임 상태를 관리하는 객체. */
    private final GameStateManager gsm;
    /** 사용자 입력을 처리하는 객체. */
    private final InputHandler inputHandler;
    /** 게임이 그려지는 창. */
    private final GameWindow gameWindow;
    /** 게임의 중앙 관리자. */
    private final GameManager gameManager;

    /** 게임 루프의 실행 여부를 제어. */
    private boolean gameRunning = true;
    /** 마지막 루프 실행 시간을 저장. */
    private long lastLoopTime;

    /**
     * GameLoop의 생성자.
     *
     * @param gsm 게임 상태 관리자
     * @param inputHandler 입력 핸들러
     * @param gameWindow 게임 윈도우
     * @param gameManager 게임 관리자
     */
    public GameLoop(GameStateManager gsm, InputHandler inputHandler, GameWindow gameWindow, GameManager gameManager) {
        this.gsm = gsm;
        this.inputHandler = inputHandler;
        this.gameWindow = gameWindow;
        this.gameManager = gameManager;
    }

    /**
     * 메인 게임 루프를 시작하고 실행합니다.
     * `gameRunning` 플래그가 false가 될 때까지 입력을 처리하고, 상태를 업데이트하며, 화면을 렌더링합니다.
     */
    public void run() {
        lastLoopTime = SystemTimer.getTime();
        while (gameRunning) {
            long delta = SystemTimer.getTime() - lastLoopTime;
            lastLoopTime = SystemTimer.getTime();

            gsm.handleInput(inputHandler);
            gsm.update(delta);

            if (gameManager.getMessageEndTime() > 0 && System.currentTimeMillis() > gameManager.getMessageEndTime()) {
                gameManager.setMessage("");
                gameManager.setMessageEndTime(0);
            }

            if (gameManager.nextState != null) {
                gameManager.setCurrentState(gameManager.nextState);
                gameManager.nextState = null;
            }

            Graphics2D g = gameWindow.getGameCanvas().getGraphics2D();
            if (g != null) {
                gsm.render(g);
                g.dispose();
                gameWindow.getGameCanvas().showStrategy();
            }

            SystemTimer.sleep(lastLoopTime + 10 - SystemTimer.getTime());
        }
    }

    /**
     * 게임 루프를 중지합니다.
     * 다음 루프 반복에서 실행이 멈춥니다.
     */
    public void stop() {
        gameRunning = false;
    }
}
