package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameManager;
import org.newdawn.spaceinvaders.core.InputHandler;
import org.newdawn.spaceinvaders.data.PlayerData;
import org.newdawn.spaceinvaders.entity.Pet.PetType;
import org.newdawn.spaceinvaders.graphics.Sprite;
import org.newdawn.spaceinvaders.graphics.SpriteStore;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PetMenuState implements GameState {

    private final GameManager gameManager;
    private PlayerData playerData;
    private int selectedPetIndex = 0;
    private List<String> ownedPetNames = new ArrayList<>();
    private Map<String, Sprite> petSprites = new HashMap<>();

    public PetMenuState(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void init() {
        this.playerData = gameManager.getCurrentPlayer();
        updateOwnedPetList();

        petSprites.put("ATTACK", SpriteStore.get().getSprite("sprites/pet/Attackpet.gif"));
        petSprites.put("DEFENSE", SpriteStore.get().getSprite("sprites/pet/Defensepet.gif"));
        petSprites.put("HEAL", SpriteStore.get().getSprite("sprites/pet/Healpet.gif"));
        petSprites.put("BUFF", SpriteStore.get().getSprite("sprites/pet/Buffpet.gif"));
    }

    private void updateOwnedPetList() {
        if (playerData != null) {
            ownedPetNames = new ArrayList<>(playerData.getPetInventory().keySet());
            ownedPetNames.sort(String::compareTo);
        }
    }

    @Override
    public void handleInput(InputHandler input) {
        if (input.isEscPressedAndConsume()) {
            gameManager.setCurrentState(Type.MAIN_MENU);
        }

        if (!ownedPetNames.isEmpty()) {
            if (input.isUpPressedAndConsume()) {
                selectedPetIndex = (selectedPetIndex - 1 + ownedPetNames.size()) % ownedPetNames.size();
            }
            if (input.isDownPressedAndConsume()) {
                selectedPetIndex = (selectedPetIndex + 1) % ownedPetNames.size();
            }

            String selectedPetName = ownedPetNames.get(selectedPetIndex);

            // Equip/Unequip Logic
            if (input.isFirePressedAndConsume()) {
                if (selectedPetName.equals(playerData.getEquippedPet())) {
                    playerData.setEquippedPet(null);
                } else {
                    playerData.setEquippedPet(selectedPetName);
                }
                gameManager.savePlayerData();
            }

            // Upgrade Logic
            if (input.isUPressedAndConsume()) {
                int currentAmount = playerData.getPetInventory().getOrDefault(selectedPetName, 0);
                if (currentAmount <= 1) {
                    gameManager.message = "강화에 필요한 중복 펫이 부족합니다.";
                    return;
                }

                try {
                    PetType petType = PetType.valueOf(selectedPetName);
                    int currentLevel = playerData.getPetLevel(petType.name());

                    if (currentLevel >= 10) {
                        gameManager.message = "이미 최고 레벨입니다.";
                        return;
                    }

                    // Proceed with upgrade
                    playerData.increasePetLevel(petType.name());
                    playerData.getPetInventory().put(selectedPetName, currentAmount - 1);
                    gameManager.savePlayerData();
                    gameManager.message = petType.getDisplayName() + " 강화 성공!";

                } catch (IllegalArgumentException e) {
                    gameManager.message = "알 수 없는 펫입니다.";
                }
            }
        }
    }

    @Override
    public void update(long delta) {}

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);

        g.setFont(new Font("Dialog", Font.BOLD, 32));
        g.setColor(Color.WHITE);
        g.drawString("펫 관리", (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth("펫 관리")) / 2, 80);

        if (ownedPetNames.isEmpty()) {
            g.setFont(new Font("Dialog", Font.PLAIN, 24));
            g.setColor(Color.GRAY);
            g.drawString("보유한 펫이 없습니다.", (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth("보유한 펫이 없습니다.")) / 2, 250);
        } else {
            int startY = 160;
            int itemHeight = 80;
            for (int i = 0; i < ownedPetNames.size(); i++) {
                String petName = ownedPetNames.get(i);
                int petCount = playerData.getPetInventory().get(petName);

                try {
                    PetType petType = PetType.valueOf(petName);
                    String displayName = (i + 1) + ". " + petType.getDisplayName() + " (x" + petCount + ")";

                    if (i == selectedPetIndex) {
                        g.setColor(Color.YELLOW);
                    } else {
                        g.setColor(Color.WHITE);
                    }

                    if (petName.equals(playerData.getEquippedPet())) {
                        displayName += " [장착됨]";
                        g.setColor(Color.GREEN);
                        if (i == selectedPetIndex) {
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

            // Draw selected pet info
            if (!ownedPetNames.isEmpty()) {
                String selectedPetName = ownedPetNames.get(selectedPetIndex);
                if (selectedPetName != null) {
                    // Draw pet image
                    Sprite sprite = petSprites.get(selectedPetName);
                    if (sprite != null) {
                        int boxX = 550;
                        int boxY = 100;
                        int boxWidth = 150;
                        int boxHeight = 150;

                        g.setColor(Color.DARK_GRAY);
                        g.drawRect(boxX - 1, boxY - 1, boxWidth + 2, boxHeight + 2);

                        sprite.draw(g, boxX, boxY, boxWidth, boxHeight);

                        // Draw description under the image
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
                                    int projectileCount = 1;
                                    if (currentLevel >= 3) projectileCount++;
                                    if (currentLevel >= 6) projectileCount++;
                                    if (currentLevel >= 10) projectileCount++;
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
                                    double buffPercent = 20 + (currentLevel * 1);
                                    description = String.format("레벨: %d (공격력/공속: +%.0f%%)", currentLevel, buffPercent);
                                    break;
                            }
                            g.drawString(description, boxX, boxY + boxHeight + 50);
                            g.drawString("강화 재료: " + duplicates + "개 보유", boxX, boxY + boxHeight + 75);

                            // Draw upgrade button
                            int buttonX = boxX;
                            int buttonY = boxY + boxHeight + 100;
                            int buttonWidth = 150;
                            int buttonHeight = 50;

                            if (currentLevel < 10) {
                                g.setColor(Color.YELLOW); // Active color
                                g.drawRect(buttonX, buttonY, buttonWidth, buttonHeight);

                                g.setFont(new Font("Dialog", Font.BOLD, 16));
                                String upgradeText = "강화 (U)";
                                int textWidth = g.getFontMetrics().stringWidth(upgradeText);
                                g.drawString(upgradeText, buttonX + (buttonWidth - textWidth) / 2, buttonY + 20);

                                g.setFont(new Font("Dialog", Font.PLAIN, 14));
                                String costText = "비용: 펫 1개";
                                textWidth = g.getFontMetrics().stringWidth(costText);
                                g.drawString(costText, buttonX + (buttonWidth - textWidth) / 2, buttonY + 40);
                            } else { // Max level
                                g.setColor(Color.GRAY); // Disabled color
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
        }

        if (gameManager.message != null && !gameManager.message.isEmpty()) {
            g.setColor(Color.yellow);
            g.setFont(new Font("Dialog", Font.BOLD, 16));
            g.drawString(gameManager.message, (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth(gameManager.message)) / 2, Game.SCREEN_HEIGHT - 80);
        }

        g.setFont(new Font("Dialog", Font.PLAIN, 16));
        g.setColor(Color.GRAY);
        g.drawString("Up/Down: 선택 | Enter: 장착/해제 | U: 강화 | ESC: 돌아가기", 20, Game.SCREEN_HEIGHT - 20);
    }

    @Override
    public void onEnter() {
        this.playerData = gameManager.getCurrentPlayer();
        updateOwnedPetList();
        gameManager.message = "";
        this.selectedPetIndex = 0;
    }

    @Override
    public void onExit() {}
}
