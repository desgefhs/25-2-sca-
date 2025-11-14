package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameState;
import org.newdawn.spaceinvaders.core.InputHandler;
import org.newdawn.spaceinvaders.ranking.Ranking;
import org.newdawn.spaceinvaders.userinput.RankingInputHandler;

import java.awt.*;
import java.util.List;

public class RankingState implements GameState {
    private final GameContext gameContext;
    private List<Ranking> highScores;
    private final RankingInputHandler inputHandler;
    private final RankingDrawer rankingDrawer;

    public RankingState(GameContext gameContext) {
        this.gameContext = gameContext;
        this.inputHandler = new RankingInputHandler(gameContext);
        this.rankingDrawer = new RankingDrawer();
    }

    @Override
    public void init() {}

    @Override
    public void handleInput(InputHandler input) {
        inputHandler.handle(input);
    }

    @Override
    public void update(long delta) {}

    @Override
    public void render(Graphics2D g) {
        rankingDrawer.draw(g, highScores);
    }

    @Override
    public void onEnter() {
        highScores = gameContext.getDatabaseManager().getHighScores();
    }

    @Override
    public void onExit() {}
}