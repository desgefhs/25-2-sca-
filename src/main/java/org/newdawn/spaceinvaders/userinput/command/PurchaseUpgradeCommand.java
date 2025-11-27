package org.newdawn.spaceinvaders.userinput.command;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.data.PlayerData;
import org.newdawn.spaceinvaders.shop.Upgrade;
import org.newdawn.spaceinvaders.view.ShopView;

/**
 * 상점에서 선택한 업그레이드를 구매하는 로직을 처리하는 커맨드.
 */
public class PurchaseUpgradeCommand implements Command {

    /** 게임의 전반적인 컨텍스트. */
    private final GameContext gameContext;
    /** 이 커맨드가 처리할 대상 상점 뷰. */
    private final ShopView shopView;

    /**
     * PurchaseUpgradeCommand 생성자.
     * @param gameContext 게임 컨텍스트
     * @param shopView 이 커맨드가 처리할 상점 뷰
     */
    public PurchaseUpgradeCommand(GameContext gameContext, ShopView shopView) {
        this.gameContext = gameContext;
        this.shopView = shopView;
    }

    /**
     * 선택된 업그레이드를 구매합니다.
     * 플레이어의 크레딧과 업그레이드의 현재 레벨을 확인한 후,
     * 조건이 충족되면 크레딧을 차감하고 업그레이드 레벨을 올린 뒤 데이터를 저장합니다.
     * 조건 미충족 시 적절한 메시지를 표시합니다.
     */
    @Override
    public void execute() {
        gameContext.getGameContainer().getSoundManager().playSound("buttonselect");
            
        Upgrade selectedUpgrade = shopView.getSelectedUpgrade();
        if (selectedUpgrade == null) return;

        PlayerData currentPlayer = gameContext.getGameContainer().getPlayerManager().getCurrentPlayer();
        int currentLevel = currentPlayer.getUpgradeLevel(selectedUpgrade.getId());
        
        if (currentLevel >= selectedUpgrade.getMaxLevel()) {
            gameContext.setMessage("이미 최고 레벨입니다.");
            return;
        }

        int cost = selectedUpgrade.getCost(currentLevel + 1);
        if (currentPlayer.getCredit() >= cost) {
            currentPlayer.setCredit(currentPlayer.getCredit() - cost);
            currentPlayer.setUpgradeLevel(selectedUpgrade.getId(), currentLevel + 1);
            gameContext.getGameContainer().getPlayerManager().savePlayerData(); // 구매 후 저장
            gameContext.setMessage("업그레이드 성공!");
        } else {
            gameContext.setMessage("크레딧이 부족합니다!");
        }
    }
}
