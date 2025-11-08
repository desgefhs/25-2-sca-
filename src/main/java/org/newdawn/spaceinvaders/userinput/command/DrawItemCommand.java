package org.newdawn.spaceinvaders.userinput.command;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.shop.DrawResult;

public class DrawItemCommand implements Command {

    private final GameContext gameContext;

    public DrawItemCommand(GameContext gameContext) {
        this.gameContext = gameContext;
    }

    @Override
    public void execute() {
        DrawResult result = gameContext.getShopManager().drawItem(gameContext.getPlayerManager().getCurrentPlayer());
        gameContext.setMessage(result.getMessage());
        
        // The drawItem method now handles saving internally if needed,
        // but we can also save here to ensure consistency.
        if (result.isSuccess()) {
            gameContext.savePlayerData();
        }
    }
}