package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameManager;
import org.newdawn.spaceinvaders.core.InputHandler;

import java.awt.*;

/**
 * 아이템 뽑기(가챠) 화면을 담당하는 게임 상태
 * 플레이어는 크레딧을 사용하여 무작위 아이템(크레딧, 펫, 무기)을 얻을 수 있음
 */
public class ItemDrawState implements GameState {

    private final GameManager gameManager;
    private int selectedIndex = 0;
    private final String[] menuItems = {"아이템 뽑기", "뒤로가기"};
    private final Rectangle[] menuBounds = new Rectangle[menuItems.length];

    public ItemDrawState(GameManager gameManager) {
        this.gameManager = gameManager;
        for (int i = 0; i < menuItems.length; i++) {
            menuBounds[i] = new Rectangle();
        }
    }

    @Override
    public void init() {
    }

    /**
     * 사용자 입력을 처리하여 메뉴를 탐색하고, 아이템 뽑기 또는 뒤로가기를 실행
     *
     * @param input 입력 핸들러
     */
    @Override
    public void handleInput(InputHandler input) {
        if (input.isUpPressedAndConsume()) {
            selectedIndex = (selectedIndex - 1 + menuItems.length) % menuItems.length;
        }
        if (input.isDownPressedAndConsume()) {
            selectedIndex = (selectedIndex + 1) % menuItems.length;
        }
        if (input.isEnterPressedAndConsume()) {
            gameManager.getSoundManager().playSound("buttonselect");
            if (selectedIndex == 0) { // 아이템 뽑기 선택
                handleItemDraw();
            } else { // 뒤로가기 선택
                gameManager.setCurrentState(Type.SHOP_MAIN_MENU);
            }
        }
        if (input.isEscPressedAndConsume()) {
            gameManager.setCurrentState(Type.SHOP_MAIN_MENU);
        }
    }

    /**
     * 아이템 뽑기 로직을 처리하고 결과에 따라 메시지를 설정
     */
    private void handleItemDraw() {
        String result = gameManager.shopManager.drawItem(gameManager.currentPlayer);
        switch (result) {
            case "INSUFFICIENT_FUNDS":
                gameManager.message = "크레딧이 부족합니다!";
                break;
            case "CREDIT_250":
                gameManager.message = "250 크레딧에 당첨되었습니다!";
                break;
            case "PET_ATTACK":
                gameManager.message = "'공격형 펫'을 획득했습니다!";
                break;
            case "PET_DEFENSE":
                gameManager.message = "'방어형 펫'을 획득했습니다!";
                break;
            case "PET_HEAL":
                gameManager.message = "'치유형 펫'을 획득했습니다!";
                break;
            case "PET_BUFF":
                gameManager.message = "'버프형 펫'을 획득했습니다!";
                break;
            case "WEAPON_SHOTGUN":
                gameManager.currentPlayer.getWeaponLevels().put("Shotgun", 1);
                gameManager.message = "새로운 무기 '샷건'을 잠금 해제했습니다!";
                break;
            case "WEAPON_LASER":
                gameManager.currentPlayer.getWeaponLevels().put("Laser", 1);
                gameManager.message = "새로운 무기 '레이저'를 잠금 해제했습니다!";
                break;
            case "DUPLICATE_WEAPON":
                gameManager.message = "이미 보유한 무기입니다! 300 크레딧을 돌려받습니다.";
                break;
        }
        gameManager.savePlayerData(); // 뽑기 결과 저장
    }


    @Override
    public void update(long delta) {
    }

    /**
     * 아이템 뽑기 화면 UI를 렌더링
     *
     * @param g 그래픽 컨텍스트
     */
    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);

        g.setFont(new Font("Dialog", Font.BOLD, 32));
        g.setColor(Color.white);
        String title = "아이템 뽑기";
        int titleWidth = g.getFontMetrics().stringWidth(title);
        g.drawString(title, (Game.SCREEN_WIDTH - titleWidth) / 2, 150);

        g.setFont(new Font("Dialog", Font.BOLD, 20));
        String creditText = "보유 크레딧: " + gameManager.currentPlayer.getCredit();
        g.drawString(creditText, (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth(creditText)) / 2, 200);

        // 결과 메시지 표시
        if (gameManager.message != null && !gameManager.message.isEmpty()) {
            g.setColor(Color.yellow);
            g.setFont(new Font("Dialog", Font.BOLD, 16));
            g.drawString(gameManager.message, (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth(gameManager.message)) / 2, 450);
        }

        g.setFont(new Font("Dialog", Font.BOLD, 24));
        int itemHeight = 60;
        int startY = 300;

        // 메뉴 항목 렌더링
        for (int i = 0; i < menuItems.length; i++) {
            String menuItemText = menuItems[i];
            if (i == 0) { // "아이템 뽑기" 버튼에 비용 표시
                menuItemText += " (비용: " + gameManager.shopManager.getItemDrawCost() + ")";
            }

            int itemWidth = g.getFontMetrics().stringWidth(menuItemText);
            int x = (Game.SCREEN_WIDTH - itemWidth) / 2;
            int y = startY + (i * itemHeight);

            menuBounds[i].setBounds(x - 20, y - 40, itemWidth + 40, itemHeight);

            if (i == selectedIndex) {
                g.setColor(Color.GREEN);
                g.fillRect(menuBounds[i].x, menuBounds[i].y, menuBounds[i].width, menuBounds[i].height);
                g.setColor(Color.BLACK);
            } else {
                g.setColor(Color.WHITE);
                g.drawRect(menuBounds[i].x, menuBounds[i].y, menuBounds[i].width, menuBounds[i].height);
            }
            g.drawString(menuItemText, x, y);
        }
    }

    /**
     * 상태 진입 시 메시지를 초기화하고 선택 인덱스를 0으로 설정
     */
    @Override
    public void onEnter() {
        gameManager.message = "";
        selectedIndex = 0;
    }

    @Override
    public void onExit() {
    }
}
