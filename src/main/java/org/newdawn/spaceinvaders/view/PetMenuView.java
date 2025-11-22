package org.newdawn.spaceinvaders.view;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.data.PlayerData;
import org.newdawn.spaceinvaders.entity.Pet.PetType;
import org.newdawn.spaceinvaders.graphics.Sprite;
import org.newdawn.spaceinvaders.userinput.Menu;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PetMenuView implements Menu {

    private static final String FONT_NAME = "Dialog";
    private List<String> ownedPetNames = new ArrayList<>();
    private int selectedItemIndex = 0;

    public PetMenuView(List<String> ownedPetNames) {
        this.ownedPetNames = ownedPetNames;
        if (this.ownedPetNames == null) {
            this.ownedPetNames = new ArrayList<>();
        }
    }

    @Override
    public void moveUp() {
        if (ownedPetNames.isEmpty()) return;
        selectedItemIndex = (selectedItemIndex - 1 + ownedPetNames.size()) % ownedPetNames.size();
    }

    @Override
    public void moveDown() {
        if (ownedPetNames.isEmpty()) return;
        selectedItemIndex = (selectedItemIndex + 1) % ownedPetNames.size();
    }

    @Override
    public void moveLeft() {
        // 이 뷰에서는 사용하지 않음
    }

    @Override
    public void moveRight() {
        // 이 뷰에서는 사용하지 않음
    }

    @Override
    public String getSelectedItem() {
        if (ownedPetNames.isEmpty() || selectedItemIndex < 0 || selectedItemIndex >= ownedPetNames.size()) {
            return null;
        }
        return ownedPetNames.get(selectedItemIndex);
    }

    public List<String> getOwnedPetNames() {
        return ownedPetNames;
    }

    public int getSelectedIndex() {
        return selectedItemIndex;
    }

    public void setSelectedIndex(int index) {
        if (index >= 0 && index < ownedPetNames.size()) {
            this.selectedItemIndex = index;
        }
    }

    public void render(Graphics2D g, PlayerData playerData, Map<String, Sprite> petSprites, String message) {
        drawBackground(g);
        if (ownedPetNames.isEmpty()) {
            drawEmptyMessage(g);
        } else {
            drawPetList(g, playerData);
            drawSelectedPetDetails(g, playerData, petSprites);
        }
        drawHelpMessages(g, message);
    }

    private void drawBackground(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);
        g.setFont(new Font(FONT_NAME, Font.BOLD, 32));
        g.setColor(Color.WHITE);
        g.drawString("펫 관리", (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth("펫 관리")) / 2, 80);
    }

    private void drawEmptyMessage(Graphics2D g) {
        g.setFont(new Font(FONT_NAME, Font.PLAIN, 24));
        g.setColor(Color.GRAY);
        g.drawString("보유한 펫이 없습니다.", (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth("보유한 펫이 없습니다.")) / 2, 250);
    }

    private void drawPetList(Graphics2D g, PlayerData playerData) {
        int startY = 160;
        int itemHeight = 80;
        for (int i = 0; i < ownedPetNames.size(); i++) {
            String petName = ownedPetNames.get(i);
            int petCount = playerData.getPetInventory().get(petName);
            try {
                PetType petType = PetType.valueOf(petName);
                String displayName = (i + 1) + ". " + petType.getDisplayName() + " (x" + petCount + ")";

                g.setColor(i == selectedItemIndex ? Color.YELLOW : Color.WHITE);

                if (petName.equals(playerData.getEquippedPet())) {
                    displayName += " [장착됨]";
                    g.setColor(i == selectedItemIndex ? new Color(150, 255, 150) : Color.GREEN);
                }
                g.setFont(new Font(FONT_NAME, Font.BOLD, 20));
                g.drawString(displayName, 150, startY + i * itemHeight);
            } catch (IllegalArgumentException e) {
                g.setColor(Color.RED);
                g.drawString((i + 1) + ". " + petName + " (Unknown)", 150, startY + i * itemHeight);
            }
        }
    }

    private void drawSelectedPetDetails(Graphics2D g, PlayerData playerData, Map<String, Sprite> petSprites) {
        String selectedPetName = getSelectedItem();
        if (selectedPetName == null) return;

        Sprite sprite = petSprites.get(selectedPetName);
        if (sprite == null) return;

        int boxX = 550;
        int boxY = 100;
        drawPetSprite(g, sprite, boxX, boxY);

        try {
            PetType petType = PetType.valueOf(selectedPetName);
            drawPetInfo(g, playerData, petType, boxX, boxY);
            drawUpgradeButton(g, playerData, petType, boxX, boxY);
        } catch (IllegalArgumentException e) {
            // Error handled by displaying "Unknown" in the list
        }
    }

    private void drawPetSprite(Graphics2D g, Sprite sprite, int boxX, int boxY) {
        int boxWidth = 150;
        int boxHeight = 150;
        g.setColor(Color.DARK_GRAY);
        g.drawRect(boxX - 1, boxY - 1, boxWidth + 2, boxHeight + 2);
        sprite.draw(g, boxX, boxY, boxWidth, boxHeight);
    }

    private void drawPetInfo(Graphics2D g, PlayerData playerData, PetType petType, int boxX, int boxY) {
        int currentLevel = playerData.getPetLevel(petType.name());
        int petCount = playerData.getPetInventory().get(petType.name());
        int duplicates = petCount - 1;

        g.setFont(new Font(FONT_NAME, Font.BOLD, 18));
        g.setColor(Color.WHITE);
        g.drawString(petType.getDisplayName(), boxX, boxY + 175);

        g.setFont(new Font(FONT_NAME, Font.PLAIN, 16));
        g.setColor(Color.LIGHT_GRAY);
        String description = getPetDescription(petType, currentLevel);
        g.drawString(description, boxX, boxY + 200);
        g.drawString("강화 재료: " + duplicates + "개 보유", boxX, boxY + 225);
    }

    private String getPetDescription(PetType petType, int currentLevel) {
        switch (petType) {
            case ATTACK:
                int projectileCount = 1 + (currentLevel / 3);
                return "레벨: " + currentLevel + " (총알 " + projectileCount + "개)";
            case DEFENSE:
                double cooldown = 5.0 - (currentLevel * 0.2);
                return String.format("레벨: %d (재사용 대기시간: %.1f초)", currentLevel, cooldown);
            case HEAL:
                double healPercent = 30 + (currentLevel * 2);
                return String.format("레벨: %d (힐량: %.0f%%)", currentLevel, healPercent);
            case BUFF:
                double buffPercent = 20 + (currentLevel);
                return String.format("레벨: %d (공격력/공속: +%.0f%%)", currentLevel, buffPercent);
            default:
                return "레벨: " + currentLevel;
        }
    }

    private void drawUpgradeButton(Graphics2D g, PlayerData playerData, PetType petType, int boxX, int boxY) {
        int currentLevel = playerData.getPetLevel(petType.name());
        int buttonX = boxX;
        int buttonY = boxY + 250;
        int buttonWidth = 150;
        int buttonHeight = 50;

        if (currentLevel < 10) {
            g.setColor(Color.YELLOW);
            g.drawRect(buttonX, buttonY, buttonWidth, buttonHeight);
            g.setFont(new Font(FONT_NAME, Font.BOLD, 16));
            String upgradeText = "강화 (U)";
            int textWidth = g.getFontMetrics().stringWidth(upgradeText);
            g.drawString(upgradeText, buttonX + (buttonWidth - textWidth) / 2, buttonY + 20);
            g.setFont(new Font(FONT_NAME, Font.PLAIN, 14));
            String costText = "비용: 펫 1개";
            textWidth = g.getFontMetrics().stringWidth(costText);
            g.drawString(costText, buttonX + (buttonWidth - textWidth) / 2, buttonY + 40);
        } else {
            g.setColor(Color.GRAY);
            g.drawRect(buttonX, buttonY, buttonWidth, buttonHeight);
            g.setFont(new Font(FONT_NAME, Font.BOLD, 16));
            String maxLevelText = "최고 레벨";
            int textWidth = g.getFontMetrics().stringWidth(maxLevelText);
            g.drawString(maxLevelText, buttonX + (buttonWidth - textWidth) / 2, buttonY + 30);
        }
    }

    private void drawHelpMessages(Graphics2D g, String message) {
        if (message != null && !message.isEmpty()) {
            g.setColor(Color.YELLOW);
            g.setFont(new Font(FONT_NAME, Font.BOLD, 16));
            g.drawString(message, (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth(message)) / 2, Game.SCREEN_HEIGHT - 80);
        }
        g.setFont(new Font(FONT_NAME, Font.PLAIN, 16));
        g.setColor(Color.GRAY);
        g.drawString("Up/Down: 선택 | Enter: 장착/해제 | U: 강화 | ESC: 돌아가기", 20, Game.SCREEN_HEIGHT - 20);
    }
}
