package org.newdawn.spaceinvaders.userinput.command;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.shop.DrawResult;
import org.newdawn.spaceinvaders.shop.ShopManager;

/**
 * 상점에서 아이템 뽑기(Gacha) 기능을 실행하는 커맨드.
 */
public class DrawItemCommand implements Command {

    /** 게임의 전반적인 컨텍스트. */
    private final GameContext gameContext;

    /**
     * DrawItemCommand 생성자.
     * @param gameContext 게임 컨텍스트
     */
    public DrawItemCommand(GameContext gameContext) {
        this.gameContext = gameContext;
    }

    /**
     * {@link ShopManager#drawItem} 메소드를 호출하여 아이템 뽑기를 실행하고,
     * 그 결과를 메시지로 표시하며, 성공 시 플레이어 데이터를 저장합니다.
     */
    @Override
    public void execute() {
        DrawResult result = gameContext.getGameContainer().getShopManager().drawItem(gameContext.getGameContainer().getPlayerManager().getCurrentPlayer());
        gameContext.setMessage(result.getMessage());

        if (result.isSuccess()) {
            gameContext.getGameContainer().getPlayerManager().savePlayerData();
        }
    }
}