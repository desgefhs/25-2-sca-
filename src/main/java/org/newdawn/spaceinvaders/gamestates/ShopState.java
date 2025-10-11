package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameManager;
import org.newdawn.spaceinvaders.core.InputHandler;
import org.newdawn.spaceinvaders.shop.Upgrade;

import java.awt.*;
import java.util.List;

/**
 * 플레이어의 능력치를 강화하는 상점 상태를 처리하는 클래스
 */
public class ShopState implements GameState {
    private final GameManager gameManager;
    private int selectedIndex = 0;
    /** 상점에서 판매하는 업그레이드 목록 */
    private List<Upgrade> upgrades;

    public ShopState(GameManager gameManager) {
        this.gameManager = gameManager;
        this.upgrades = gameManager.shopMenu.getItems();
    }

    @Override
    public void init() {
    }

    /**
     * 사용자 입력을 처리하여 업그레이드를 선택하고 구매
     *
     * @param input 입력 핸들러
     */
    @Override
    public void handleInput(InputHandler input) {
        // 메뉴 탐색
        if (input.isUpPressedAndConsume()) {
            selectedIndex = (selectedIndex - 1 + upgrades.size()) % upgrades.size();
        }
        if (input.isDownPressedAndConsume()) {
            selectedIndex = (selectedIndex + 1) % upgrades.size();
        }
        // ESC 키로 이전 메뉴로 돌아가기
        if (input.isEscPressedAndConsume()) {
            gameManager.setCurrentState(Type.SHOP_MAIN_MENU);
        }

        // Enter 키로 업그레이드 구매 시도
        if (input.isEnterPressedAndConsume()) {
            handlePurchase();
        }
    }

    /**
     * 선택된 업그레이드의 구매 로직을 처리
     */
    private void handlePurchase() {
        gameManager.getSoundManager().playSound("buttonselect");

        if (selectedIndex < upgrades.size()) {
            Upgrade selectedUpgrade = upgrades.get(selectedIndex);
            int currentLevel = gameManager.currentPlayer.getUpgradeLevel(selectedUpgrade.getId());

            if (currentLevel >= selectedUpgrade.getMaxLevel()) {
                gameManager.message = "이미 최고 레벨입니다.";
                return;
            }

            int cost = selectedUpgrade.getCost(currentLevel + 1);
            if (gameManager.currentPlayer.getCredit() >= cost) {
                gameManager.currentPlayer.setCredit(gameManager.currentPlayer.getCredit() - cost);
                gameManager.currentPlayer.setUpgradeLevel(selectedUpgrade.getId(), currentLevel + 1);
                gameManager.savePlayerData(); // 구매 후 데이터 저장
                gameManager.message = "업그레이드 성공!";
            } else {
                gameManager.message = "크레딧이 부족합니다!";
            }
        }
    }

    @Override
    public void update(long delta) {
    }

    /**
     * 상점 UI를 렌더링
     *
     * @param g 그래픽 컨텍스트
     */
    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);

        // 제목 및 보유 크레딧 표시
        g.setColor(Color.white);
        g.setFont(new Font("Dialog", Font.BOLD, 32));
        g.drawString("캐릭터 강화", (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth("캐릭터 강화")) / 2, 80);

        g.setFont(new Font("Dialog", Font.BOLD, 20));
        String creditText = "보유 크레딧: " + gameManager.currentPlayer.getCredit();
        g.drawString(creditText, (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth(creditText)) / 2, 120);

        // 하단 안내 메시지
        g.setFont(new Font("Dialog", Font.PLAIN, 14));
        g.drawString("위/아래 키로 이동, 엔터 키로 구매, ESC 키로 나가기", (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth("위/아래 키로 이동, 엔터 키로 구매, ESC 키로 나가기")) / 2, 550);

        // 결과 메시지
        if (gameManager.message != null && !gameManager.message.isEmpty()) {
            g.setColor(Color.yellow);
            g.setFont(new Font("Dialog", Font.BOLD, 16));
            g.drawString(gameManager.message, (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth(gameManager.message)) / 2, 520);
        }

        int itemHeight = 60;
        int startY = 160;

        // 업그레이드 항목 목록 렌더링
        for (int i = 0; i < upgrades.size(); i++) {
            Upgrade upgrade = upgrades.get(i);
            int currentLevel = gameManager.currentPlayer.getUpgradeLevel(upgrade.getId());
            int maxLevel = upgrade.getMaxLevel();

            Rectangle itemBounds = new Rectangle(100, startY + (i * itemHeight) - 40, Game.SCREEN_WIDTH - 200, itemHeight);

            // 선택된 항목 강조
            if (i == selectedIndex) {
                g.setColor(Color.GREEN);
                g.fillRect(itemBounds.x, itemBounds.y, itemBounds.width, itemBounds.height);
                g.setColor(Color.BLACK);
            } else {
                g.setColor(Color.WHITE);
                g.drawRect(itemBounds.x, itemBounds.y, itemBounds.width, itemBounds.height);
            }

            g.setFont(new Font("Dialog", Font.BOLD, 20));
            g.drawString(upgrade.getName(), 120, startY + (i * itemHeight));

            g.setFont(new Font("Dialog", Font.PLAIN, 16));
            g.drawString("레벨: " + currentLevel + " / " + maxLevel, 370, startY + (i * itemHeight));

            String costString = (currentLevel >= maxLevel) ? "최고 레벨" : "비용: " + upgrade.getCost(currentLevel + 1);
            g.drawString(costString, 570, startY + (i * itemHeight));
        }
    }

    /**
     * 상태 진입 시 메시지와 선택 인덱스를 초기화
     */
    @Override
    public void onEnter() {
        gameManager.message = "";
        this.selectedIndex = 0;
    }

    @Override
    public void onExit() {}
}
