package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameState;
import org.newdawn.spaceinvaders.core.InputHandler;
import org.newdawn.spaceinvaders.ranking.Ranking;
import org.newdawn.spaceinvaders.userinput.RankingInputHandler;

import java.awt.*;
import java.util.List;

/**
 * 게임의 랭킹 화면 상태를 관리하는 클래스.
 * 랭킹 데이터를 로드하고, 사용자 입력을 처리하며, RankingDrawer를 이용해 화면에 랭킹을 그립니다.
 */
public class RankingState implements GameState {
    /** 게임의 컨텍스트. */
    private final GameContext gameContext;
    /** 로드된 상위 점수 목록. */
    private List<Ranking> highScores;
    /** 랭킹 화면의 사용자 입력을 처리하는 핸들러. */
    private final RankingInputHandler inputHandler;
    /** 랭킹 화면의 UI 요소를 그리는 객체. */
    private final RankingDrawer rankingDrawer;

    /**
     * RankingState의 생성자.
     *
     * @param gameContext 게임 컨텍스트
     */
    public RankingState(GameContext gameContext) {
        this.gameContext = gameContext;
        this.inputHandler = new RankingInputHandler(gameContext);
        this.rankingDrawer = new RankingDrawer();
    }

    /**
     * 게임 상태가 초기화될 때 호출되지만, 이 상태에서는 사용하지 않습니다.
     */
    @Override
    public void init() {
        // 이 상태에서는 사용하지 않음
    }

    /**
     * 사용자 입력을 처리합니다.
     *
     * @param input 현재 입력 상태를 포함하는 InputHandler 객체
     */
    @Override
    public void handleInput(InputHandler input) {
        inputHandler.handle(input);
    }

    /**
     * 게임 상태를 업데이트하지만, 랭킹 화면에서는 특별한 업데이트 로직이 없습니다.
     *
     * @param delta 마지막 업데이트 이후 경과된 시간 (밀리초)
     */
    @Override
    public void update(long delta) {
        // 이 상태에서는 사용하지 않음
    }

    /**
     * 랭킹 화면을 그립니다.
     * RankingDrawer를 사용하여 highScores 목록을 화면에 렌더링합니다.
     *
     * @param g 그래픽 컨텍스트
     */
    @Override
    public void render(Graphics2D g) {
        rankingDrawer.draw(g, highScores);
    }

    /**
     * 랭킹 상태로 진입할 때 호출됩니다.
     * 데이터베이스에서 최신 상위 점수 목록을 로드합니다.
     */
    @Override
    public void onEnter() {
        highScores = gameContext.getGameContainer().getDatabaseManager().getHighScores();
    }

    /**
     * 랭킹 상태를 종료할 때 호출되지만, 이 상태에서는 특별한 정리 작업이 없습니다.
     */
    @Override
    public void onExit() {
        // 이 상태에서는 사용하지 않음
    }
}