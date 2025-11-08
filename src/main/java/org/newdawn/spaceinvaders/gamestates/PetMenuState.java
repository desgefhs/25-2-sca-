package org.newdawn.spaceinvaders.gamestates;

import org.newdawn.spaceinvaders.core.Game;
import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.core.GameState;
import org.newdawn.spaceinvaders.core.InputHandler;
import org.newdawn.spaceinvaders.data.PlayerData;
import org.newdawn.spaceinvaders.entity.Pet.PetType;
import org.newdawn.spaceinvaders.graphics.Sprite;
import org.newdawn.spaceinvaders.graphics.SpriteStore;
import org.newdawn.spaceinvaders.userinput.PetMenuInputHandler;
import org.newdawn.spaceinvaders.view.PetMenuView;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PetMenuState implements GameState {

    private final GameContext gameContext;
    private PlayerData playerData;
    private PetMenuView petMenuView;
    private PetMenuInputHandler inputHandler;
    private Map<String, Sprite> petSprites = new HashMap<>();

    public PetMenuState(GameContext gameContext) {
        this.gameContext = gameContext;
    }

    @Override
    public void init() {
        petSprites.put("ATTACK", SpriteStore.get().getSprite("sprites/pet/Attackpet.gif"));
        petSprites.put("DEFENSE", SpriteStore.get().getSprite("sprites/pet/Defensepet.gif"));
        petSprites.put("HEAL", SpriteStore.get().getSprite("sprites/pet/Healpet.gif"));
        petSprites.put("BUFF", SpriteStore.get().getSprite("sprites/pet/Buffpet.gif"));
    }

    private void setupMenu() {
        List<String> ownedPetNames = new ArrayList<>();
        if (playerData != null) {
            ownedPetNames = new ArrayList<>(playerData.getPetInventory().keySet());
            ownedPetNames.sort(String::compareTo);
        }
        this.petMenuView = new PetMenuView(ownedPetNames);
        this.inputHandler = new PetMenuInputHandler(gameContext, () -> this.petMenuView);
    }

    @Override
    public void handleInput(InputHandler input) {
        if (inputHandler != null) {
            inputHandler.handle(input);
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

        if (petMenuView == null || petMenuView.getOwnedPetNames().isEmpty()) {
            g.setFont(new Font("Dialog", Font.PLAIN, 24));
            g.setColor(Color.GRAY);
            g.drawString("보유한 펫이 없습니다.", (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth("보유한 펫이 없습니다.")) / 2, 250);
        } else {
            int startY = 160;
            int itemHeight = 80;
            List<String> ownedPetNames = petMenuView.getOwnedPetNames();
            for (int i = 0; i < ownedPetNames.size(); i++) {
                String petName = ownedPetNames.get(i);
                int petCount = playerData.getPetInventory().get(petName);

                try {
                    PetType petType = PetType.valueOf(petName);
                    String displayName = (i + 1) + ". " + petType.getDisplayName() + " (x" + petCount + ")";

                    if (i == petMenuView.getSelectedIndex()) {
                        g.setColor(Color.YELLOW);
                    } else {
                        g.setColor(Color.WHITE);
                    }

                    if (petName.equals(playerData.getEquippedPet())) {
                        displayName += " [장착됨]";
                        g.setColor(Color.GREEN);
                        if (i == petMenuView.getSelectedIndex()) {
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

            String selectedPetName = petMenuView.getSelectedItem();
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
                                double buffPercent = 20 + (currentLevel * 1);
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

        if (gameContext.getMessage() != null && !gameContext.getMessage().isEmpty()) {
            g.setColor(Color.yellow);
            g.setFont(new Font("Dialog", Font.BOLD, 16));
            g.drawString(gameContext.getMessage(), (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth(gameContext.getMessage())) / 2, Game.SCREEN_HEIGHT - 80);
        }

        g.setFont(new Font("Dialog", Font.PLAIN, 16));
        g.setColor(Color.GRAY);
        g.drawString("Up/Down: 선택 | Enter: 장착/해제 | U: 강화 | ESC: 돌아가기", 20, Game.SCREEN_HEIGHT - 20);
    }

    @Override
    public void onEnter() {
        this.playerData = gameContext.getPlayerManager().getCurrentPlayer();
        setupMenu();
        gameContext.setMessage("");
    }

    @Override
    public void onExit() {}
}
