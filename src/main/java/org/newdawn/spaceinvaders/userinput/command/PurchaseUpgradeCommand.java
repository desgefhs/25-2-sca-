package org.newdawn.spaceinvaders.userinput.command;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.data.PlayerData;
import org.newdawn.spaceinvaders.shop.Upgrade;
import org.newdawn.spaceinvaders.view.ShopView;

public class PurchaseUpgradeCommand implements Command {

    private final GameContext gameContext;
    private final ShopView shopView;

    public PurchaseUpgradeCommand(GameContext gameContext, ShopView shopView) {
        this.gameContext = gameContext;
        this.shopView = shopView;
    }

    @Override
    public void execute() {
        gameContext.getSoundManager().playSound("buttonselect");
            
        Upgrade selectedUpgrade = shopView.getSelectedUpgrade();
        if (selectedUpgrade == null) return;

        PlayerData currentPlayer = gameContext.getPlayerManager().getCurrentPlayer();
        int currentLevel = currentPlayer.getUpgradeLevel(selectedUpgrade.getId());
        
        if (currentLevel >= selectedUpgrade.getMaxLevel()) {
            gameContext.setMessage("이미 최고 레벨입니다.");
            return;
        }

        int cost = selectedUpgrade.getCost(currentLevel + 1);
        if (currentPlayer.getCredit() >= cost) {
            currentPlayer.setCredit(currentPlayer.getCredit() - cost);
            currentPlayer.setUpgradeLevel(selectedUpgrade.getId(), currentLevel + 1);
            gameContext.savePlayerData(); // Save after purchase
            gameContext.setMessage("업그레이드 성공!");
        } else {
            gameContext.setMessage("크레딧이 부족합니다!");
        }
    }
}
