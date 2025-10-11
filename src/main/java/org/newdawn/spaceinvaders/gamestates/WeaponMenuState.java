package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameManager;
import org.newdawn.spaceinvaders.core.InputHandler;
import org.newdawn.spaceinvaders.data.PlayerData;
import org.newdawn.spaceinvaders.graphics.Sprite;
import org.newdawn.spaceinvaders.graphics.SpriteStore;
import org.newdawn.spaceinvaders.view.WeaponMenu;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 플레이어가 보유한 무기를 관리(장착, 업그레이드)하는 메뉴 상태
 */
public class WeaponMenuState implements GameState {

    private final GameManager gameManager;
    private WeaponMenu weaponMenu;
    private Map<String, Sprite> weaponSprites = new HashMap<>();

    public WeaponMenuState(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    /**
     * 상태 초기화 시 무기 목록과 스프라이트를 로드
     */
    @Override
    public void init() {
        ArrayList<String> weaponNames = new ArrayList<>();
        weaponNames.add("DefaultGun");
        weaponNames.add("Shotgun");
        weaponNames.add("Laser");
        this.weaponMenu = new WeaponMenu(weaponNames);

        weaponSprites.put("DefaultGun", SpriteStore.get().getSprite("sprites/weapon/gun.png"));
        weaponSprites.put("Shotgun", SpriteStore.get().getSprite("sprites/weapon/shotgun.png"));
        weaponSprites.put("Laser", SpriteStore.get().getSprite("sprites/weapon/lasergun.png"));
    }

    /**
     * 사용자 입력을 처리하여 무기 선택, 장착, 업그레이드를 수행
     *
     * @param input 입력 핸들러
     */
    @Override
    public void handleInput(InputHandler input) {
        if (input.isUpPressedAndConsume()) weaponMenu.moveUp();
        if (input.isDownPressedAndConsume()) weaponMenu.moveDown();
        if (input.isEscPressedAndConsume()) gameManager.setCurrentState(Type.MAIN_MENU);

        if (input.isEnterPressedAndConsume()) handleEquip();
        if (input.isUPressedAndConsume()) handleUpgrade();
    }

    /**
     * 선택된 무기를 장착하는 로직을 처리
     */
    private void handleEquip() {
        gameManager.getSoundManager().playSound("buttonselect");
        String selectedWeapon = weaponMenu.getSelectedItem();
        PlayerData playerData = gameManager.currentPlayer;

        int level = playerData.getWeaponLevels().getOrDefault(selectedWeapon, 0);
        
        // 기본 총은 항상 장착 가능, 다른 무기는 레벨 1 이상이어야 함
        if (selectedWeapon.equals("DefaultGun") || level > 0) {
            playerData.setEquippedWeapon(selectedWeapon);
            gameManager.savePlayerData();
            gameManager.message = selectedWeapon + " 장착됨";
        } else {
            gameManager.message = "상점에서 먼저 무기를 잠금 해제해야 합니다.";
        }
    }

    /**
     * 선택된 무기를 업그레이드하는 로직을 처리
     */
    private void handleUpgrade() {
        String selectedWeapon = weaponMenu.getSelectedItem();
        PlayerData playerData = gameManager.currentPlayer;
        int currentLevel = playerData.getWeaponLevels().getOrDefault(selectedWeapon, 0);
        
        if (currentLevel == 0) {
            gameManager.message = "잠금 해제되지 않은 무기는 강화할 수 없습니다.";
            return;
        }
        if (currentLevel >= 5) {
            gameManager.message = "이미 최고 레벨입니다.";
            return;
        }

        int cost = selectedWeapon.equals("Shotgun") ? getShotgunUpgradeCost(currentLevel + 1) : getLaserUpgradeCost(currentLevel + 1);

        if (playerData.getCredit() >= cost) {
            playerData.setCredit(playerData.getCredit() - cost);
            playerData.getWeaponLevels().put(selectedWeapon, currentLevel + 1);
            gameManager.savePlayerData();
            gameManager.message = selectedWeapon + " (으)로 강화 성공! 레벨 " + (currentLevel + 1);
        } else {
            gameManager.message = "크레딧이 부족합니다!";
        }
    }

    @Override
    public void update(long delta) {}

    /**
     * 무기 메뉴 UI를 렌더링
     *
     * @param g 그래픽 컨텍스트
     */
    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);
        
        renderHeader(g);
        renderWeaponList(g);
        renderSelectedWeaponDetails(g);
        renderFooter(g);
    }

    private void renderHeader(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Dialog", Font.BOLD, 24));
        g.drawString("Weapon Locker", 50, 50);
    }

    private void renderWeaponList(Graphics2D g) {
        g.setFont(new Font("Dialog", Font.PLAIN, 18));
        int yPos = 100;
        for (int i = 0; i < weaponMenu.getItems().size(); i++) {
            String weaponName = weaponMenu.getItems().get(i);
            int level = gameManager.currentPlayer.getWeaponLevels().getOrDefault(weaponName, 0);
            String equippedWeapon = gameManager.currentPlayer.getEquippedWeapon();

            String status = weaponName.equals("DefaultGun") ? "기본 무기" : (level > 0 ? "Level " + level : "[LOCKED]");
            String displayText = weaponName + " - " + status;

            if (weaponName.equals(equippedWeapon)) {
                g.setColor(i == weaponMenu.getSelectedIndex() ? Color.CYAN : Color.GREEN);
                displayText += " [EQUIPPED]";
            } else {
                g.setColor(i == weaponMenu.getSelectedIndex() ? Color.YELLOW : Color.WHITE);
            }

            g.drawString(displayText, 100, yPos);
            yPos += 40;
        }
    }

    private void renderSelectedWeaponDetails(Graphics2D g) {
        String selectedWeapon = weaponMenu.getSelectedItem();
        if (selectedWeapon == null) return;

        // 무기 이미지
        Sprite sprite = weaponSprites.get(selectedWeapon);
        if (sprite == null) return;
        int boxX = 550, boxY = 100, boxWidth = 150, boxHeight = 150;
        g.setColor(Color.DARK_GRAY);
        g.drawRect(boxX - 1, boxY - 1, boxWidth + 2, boxHeight + 2);
        sprite.draw(g, boxX, boxY, boxWidth, boxHeight);

        // 무기 설명 및 업그레이드 버튼
        int level = gameManager.currentPlayer.getWeaponLevels().getOrDefault(selectedWeapon, 0);
        boolean isUpgradeable = (selectedWeapon.equals("Shotgun") || selectedWeapon.equals("Laser")) && level > 0;

        if (isUpgradeable) {
            renderUpgradeButton(g, selectedWeapon, level);
            renderWeaponDescription(g, selectedWeapon, level);
        }
    }

    private void renderUpgradeButton(Graphics2D g, String weaponName, int level) {
        int buttonX = 550, buttonY = 270, buttonWidth = 150, buttonHeight = 50;
        
        if (level < 5) { // 강화 가능
            int cost = weaponName.equals("Shotgun") ? getShotgunUpgradeCost(level + 1) : getLaserUpgradeCost(level + 1);
            
            g.setColor(Color.YELLOW);
            g.drawRect(buttonX, buttonY, buttonWidth, buttonHeight);
            g.setFont(new Font("Dialog", Font.BOLD, 16));
            g.drawString("Upgrade (U)", buttonX + 30, buttonY + 20);
            g.setFont(new Font("Dialog", Font.PLAIN, 14));
            g.drawString("Cost: " + cost, buttonX + 45, buttonY + 40);
        } else { // 최고 레벨
            g.setColor(Color.GRAY);
            g.drawRect(buttonX, buttonY, buttonWidth, buttonHeight);
            g.setFont(new Font("Dialog", Font.BOLD, 16));
            g.drawString("Max Level", buttonX + 35, buttonY + 30);
        }
    }

    private void renderWeaponDescription(Graphics2D g, String weaponName, int level) {
        int descX = 550, descY = 355;
        g.setFont(new Font("Dialog", Font.PLAIN, 14));
        g.setColor(Color.LIGHT_GRAY);
        String desc1 = "", desc2 = "";

        if (weaponName.equals("Shotgun")) {
            int[] numProjectiles = {3, 4, 5, 6, 7};
            double[] spreadAngle = {15, 20, 25, 30, 35};
            desc1 = "탄환 " + numProjectiles[level-1] + "개, " + spreadAngle[level-1] + "도 각도로 발사";
            if (level < 5) desc2 = "다음 레벨: 탄환 " + numProjectiles[level] + "개";
        } else if (weaponName.equals("Laser")) {
            int damage = 3 + (level - 1);
            long interval = 1500 - ((level - 1) * 250);
            desc1 = "데미지 " + damage + ", 쿨타임 " + (interval/1000.0) + "초";
            if (level < 5) desc2 = "다음 레벨: 데미지 " + (damage + 1);
        }
        g.drawString(desc1, descX, descY);
        if (level < 5) g.drawString(desc2, descX, descY + 20);
    }

    private void renderFooter(Graphics2D g) {
        if (gameManager.message != null && !gameManager.message.isEmpty()) {
            g.setColor(Color.YELLOW);
            g.drawString(gameManager.message, 50, 500);
        }
        g.setColor(Color.GRAY);
        g.drawString("상점에서 새로운 무기를 잠금 해제할 수 있습니다.", 50, 450);
    }

    public int getShotgunUpgradeCost(int level) {
        return (int) (500 * Math.pow(2, level - 1)); // 500, 1000, 2000, 4000
    }

    public int getLaserUpgradeCost(int level) {
        return (int) (600 * Math.pow(2, level - 1)); // 600, 1200, 2400, 4800
    }

    @Override
    public void onEnter() {
        init();
        gameManager.message = "";
    }

    @Override
    public void onExit() {
        gameManager.message = "";
    }
}
