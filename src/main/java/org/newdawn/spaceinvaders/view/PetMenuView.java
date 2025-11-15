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
    public void moveLeft() {}

    @Override
    public void moveRight() {}

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
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);

        g.setFont(new Font("Dialog", Font.BOLD, 32));
        g.setColor(Color.WHITE);
        g.drawString("펫 관리", (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth("펫 관리")) / 2, 80);

        if (this.getOwnedPetNames().isEmpty()) {
            g.setFont(new Font("Dialog", Font.PLAIN, 24));
            g.setColor(Color.GRAY);
            g.drawString("보유한 펫이 없습니다.", (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth("보유한 펫이 없습니다.")) / 2, 250);
        } else {
            int startY = 160;
            int itemHeight = 80;
            List<String> ownedPetNames = this.getOwnedPetNames();
            for (int i = 0; i < ownedPetNames.size(); i++) {
                String petName = ownedPetNames.get(i);
                int petCount = playerData.getPetInventory().get(petName);

                try {
                    PetType petType = PetType.valueOf(petName);
                    String displayName = (i + 1) + ". " + petType.getDisplayName() + " (x" + petCount + ")";

                    if (i == this.getSelectedIndex()) {
                        g.setColor(Color.YELLOW);
                    } else {
                        g.setColor(Color.WHITE);
                    }

                    if (petName.equals(playerData.getEquippedPet())) {
                        displayName += " [장착됨]";
                        g.setColor(Color.GREEN);
                        if (i == this.getSelectedIndex()) {
                            g.setColor(new Color(150, 255, 150));
                        }
                    }
                    g.setFont(new Font("Dialog", Font.BOLD, 20));
                    g.drawString(displayName, 150, startY + i * itemHeight);

                } catch (IllegalArgumentException e) {
                    g.setColor(Color.RED);
                    g.drawString((i + 1) + ". " + petName + " (Unknown)", 150, startY + i * itemHeight);
                }
            }

            String selectedPetName = this.getSelectedItem();
            if (selectedPetName != null) {
                Sprite sprite = petSprites.get(selectedPetName);
                if (sprite != null) {
                    int boxX = 550;
                    int boxY = 100;
                    int boxWidth = 150;
                    int boxHeight = 150;

                    g.setColor(Color.DARK_GRAY);
                    g.drawRect(boxX - 1, boxY - 1, boxWidth + 2, boxHeight + 2);
                    sprite.draw(g, boxX, boxY, boxWidth, boxHeight);

                    try {
                        PetType petType = PetType.valueOf(selectedPetName);
                        int currentLevel = playerData.getPetLevel(petType.name());
                        int petCount = playerData.getPetInventory().get(selectedPetName);
                        int duplicates = petCount - 1;

                        g.setFont(new Font("Dialog", Font.BOLD, 18));
                        g.setColor(Color.WHITE);
                        g.drawString(petType.getDisplayName(), boxX, boxY + boxHeight + 25);

                        g.setFont(new Font("Dialog", Font.PLAIN, 16));
                        g.setColor(Color.LIGHT_GRAY);

                        String description = "";
                        switch (petType) {
                            case ATTACK:
                                int projectileCount = 1 + (currentLevel / 3);
                                description = "레벨: " + currentLevel + " (총알 " + projectileCount + "개)";
                                break;
                            case DEFENSE:
                                double cooldown = 5.0 - (currentLevel * 0.2);
                                description = String.format("레벨: %d (재사용 대기시간: %.1f초)", currentLevel, cooldown);
                                break;
                            case HEAL:
                                double healPercent = 30 + (currentLevel * 2);
                                description = String.format("레벨: %d (힐량: %.0f%%)", currentLevel, healPercent);
                                break;
                            case BUFF:
                                double buffPercent = 20 + (currentLevel);
                                description = String.format("레벨: %d (공격력/공속: +%.0f%%)", currentLevel, buffPercent);
                                break;
                        }
                        g.drawString(description, boxX, boxY + boxHeight + 50);
                        g.drawString("강화 재료: " + duplicates + "개 보유", boxX, boxY + boxHeight + 75);

                        int buttonX = boxX;
                        int buttonY = boxY + boxHeight + 100;
                        int buttonWidth = 150;
                        int buttonHeight = 50;

                        if (currentLevel < 10) {
                            g.setColor(Color.YELLOW);
                            g.drawRect(buttonX, buttonY, buttonWidth, buttonHeight);
                            g.setFont(new Font("Dialog", Font.BOLD, 16));
                            String upgradeText = "강화 (U)";
                            int textWidth = g.getFontMetrics().stringWidth(upgradeText);
                            g.drawString(upgradeText, buttonX + (buttonWidth - textWidth) / 2, buttonY + 20);
                            g.setFont(new Font("Dialog", Font.PLAIN, 14));
                            String costText = "비용: 펫 1개";
                            textWidth = g.getFontMetrics().stringWidth(costText);
                            g.drawString(costText, buttonX + (buttonWidth - textWidth) / 2, buttonY + 40);
                        } else {
                            g.setColor(Color.GRAY);
                            g.drawRect(buttonX, buttonY, buttonWidth, buttonHeight);
                            g.setFont(new Font("Dialog", Font.BOLD, 16));
                            String maxLevelText = "최고 레벨";
                            int textWidth = g.getFontMetrics().stringWidth(maxLevelText);
                            g.drawString(maxLevelText, buttonX + (buttonWidth - textWidth) / 2, buttonY + 30);
                        }

                    } catch (IllegalArgumentException e) {
                        // Handle error if pet type is not found
                    }
                }
            }
        }

        if (message != null && !message.isEmpty()) {
            g.setColor(Color.yellow);
            g.setFont(new Font("Dialog", Font.BOLD, 16));
            g.drawString(message, (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth(message)) / 2, Game.SCREEN_HEIGHT - 80);
        }

        g.setFont(new Font("Dialog", Font.PLAIN, 16));
        g.setColor(Color.GRAY);
        g.drawString("Up/Down: 선택 | Enter: 장착/해제 | U: 강화 | ESC: 돌아가기", 20, Game.SCREEN_HEIGHT - 20);
    }
}