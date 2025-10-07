package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameManager;
import org.newdawn.spaceinvaders.core.InputHandler;
import org.newdawn.spaceinvaders.data.PlayerData;
import org.newdawn.spaceinvaders.entity.Pet.PetType;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PetMenuState implements GameState {

    private final GameManager gameManager;
    private PlayerData playerData;
    private int selectedPetIndex = 0;
    private List<String> ownedPetNames = new ArrayList<>();

    public PetMenuState(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void init() {
        this.playerData = gameManager.getCurrentPlayer();
        updateOwnedPetList();
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
        g.drawImage(gameManager.staticBackgroundSprite.getImage(), 0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT, null);

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

                    g.setFont(new Font("Dialog", Font.PLAIN, 16));
                    g.setColor(Color.LIGHT_GRAY);
                    int duplicates = petCount - 1;
                    String upgradeInfo = "강화 재료: " + duplicates + "개 보유";
                    int currentLevel = playerData.getPetLevel(petType.name());

                    // Draw upgrade info for each pet type
                    switch (petType) {
                        case ATTACK:
                            g.drawString("레벨: " + currentLevel + " (총알 " + (currentLevel + 1) + "개)", 170, startY + i * itemHeight + 25);
                            break;
                        case DEFENSE:
                            double cooldown = 5.0 - (currentLevel * 0.2);
                            g.drawString(String.format("레벨: %d (재사용 대기시간: %.1f초)", currentLevel, cooldown), 170, startY + i * itemHeight + 25);
                            break;
                        case HEAL:
                            double healPercent = 30 + (currentLevel * 2);
                            g.drawString(String.format("레벨: %d (힐량: %.0f%%)", currentLevel, healPercent), 170, startY + i * itemHeight + 25);
                            break;
                        case BUFF:
                            double buffPercent = 20 + (currentLevel * 1);
                            g.drawString(String.format("레벨: %d (공격력/공속: +%.0f%%)", currentLevel, buffPercent), 170, startY + i * itemHeight + 25);
                            break;
                        // Add other pets here
                    }
                    g.drawString(upgradeInfo, 450, startY + i * itemHeight + 25);

                } catch (IllegalArgumentException e) {
                    g.setColor(Color.RED);
                    g.drawString((i + 1) + ". " + petName + " (Unknown)", 150, startY + i * itemHeight);
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
