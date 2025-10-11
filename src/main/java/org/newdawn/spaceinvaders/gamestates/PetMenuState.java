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

/**
 * 플레이어가 보유한 펫을 관리(장착, 해제, 강화)하는 메뉴 상태
 */
public class PetMenuState implements GameState {

    private final GameManager gameManager;
    private PlayerData playerData;
    private int selectedPetIndex = 0;
    /** 보유 중인 펫의 이름 목록 */
    private List<String> ownedPetNames = new ArrayList<>();
    /** 펫 이름과 스프라이트를 매핑하는 맵 */
    private Map<String, Sprite> petSprites = new HashMap<>();

    public PetMenuState(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    /**
     * 상태 초기화 시 플레이어 데이터를 가져오고, 보유 펫 목록과 스프라이트를 로드
     */
    @Override
    public void init() {
        this.playerData = gameManager.getCurrentPlayer();
        updateOwnedPetList();

        petSprites.put("ATTACK", SpriteStore.get().getSprite("sprites/pet/Attackpet.gif"));
        petSprites.put("DEFENSE", SpriteStore.get().getSprite("sprites/pet/Defensepet.gif"));
        petSprites.put("HEAL", SpriteStore.get().getSprite("sprites/pet/Healpet.gif"));
        petSprites.put("BUFF", SpriteStore.get().getSprite("sprites/pet/Buffpet.gif"));
    }

    /**
     * 플레이어 데이터에서 보유한 펫 목록을 업데이트
     */
    private void updateOwnedPetList() {
        if (playerData != null) {
            ownedPetNames = new ArrayList<>(playerData.getPetInventory().keySet());
            ownedPetNames.sort(String::compareTo);
        }
    }

    /**
     * 사용자 입력을 처리하여 펫 선택, 장착/해제, 강화를 수행
     *
     * @param input 입력 핸들러
     */
    @Override
    public void handleInput(InputHandler input) {
        if (input.isEscPressedAndConsume()) {
            gameManager.setCurrentState(Type.MAIN_MENU);
        }

        if (ownedPetNames.isEmpty()) {
            return;
        }

        // 펫 목록 탐색
        if (input.isUpPressedAndConsume()) {
            selectedPetIndex = (selectedPetIndex - 1 + ownedPetNames.size()) % ownedPetNames.size();
        }
        if (input.isDownPressedAndConsume()) {
            selectedPetIndex = (selectedPetIndex + 1) % ownedPetNames.size();
        }

        String selectedPetName = ownedPetNames.get(selectedPetIndex);

        // Enter: 장착/해제 로직
        if (input.isEnterPressedAndConsume()) {
            gameManager.getSoundManager().playSound("buttonselect");
            if (selectedPetName.equals(playerData.getEquippedPet())) {
                playerData.setEquippedPet(null); // 이미 장착된 펫이면 해제
            } else {
                playerData.setEquippedPet(selectedPetName); // 장착
            }
            gameManager.savePlayerData();
        }

        // U: 강화 로직
        if (input.isUPressedAndConsume()) {
            handleUpgrade(selectedPetName);
        }
    }

    /**
     * 선택된 펫의 강화 로직을 처리
     * @param petName 강화할 펫의 이름
     */
    private void handleUpgrade(String petName) {
        int currentAmount = playerData.getPetInventory().getOrDefault(petName, 0);
        if (currentAmount <= 1) {
            gameManager.message = "강화에 필요한 중복 펫이 부족합니다.";
            return;
        }

        try {
            PetType petType = PetType.valueOf(petName);
            int currentLevel = playerData.getPetLevel(petType.name());

            if (currentLevel >= 10) {
                gameManager.message = "이미 최고 레벨입니다.";
                return;
            }

            // 강화 진행
            playerData.increasePetLevel(petType.name());
            playerData.getPetInventory().put(petName, currentAmount - 1); // 재료로 사용된 펫 1개 차감
            gameManager.savePlayerData();
            gameManager.message = petType.getDisplayName() + " 강화 성공!";

        } catch (IllegalArgumentException e) {
            gameManager.message = "알 수 없는 펫입니다.";
        }
    }

    @Override
    public void update(long delta) {}

    /**
     * 펫 메뉴 UI를 렌더링
     * 보유 펫 목록, 선택된 펫의 상세 정보(이미지, 레벨, 능력치, 강화 버튼)를 그림
     *
     * @param g 그래픽 컨텍스트
     */
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
            // 보유 펫 목록 렌더링
            renderPetList(g);
            // 선택된 펫 상세 정보 렌더링
            renderSelectedPetDetails(g);
        }

        // 하단 메시지 및 조작법 안내 렌더링
        renderFooter(g);
    }

    private void renderPetList(Graphics2D g) {
        int startY = 160;
        int itemHeight = 80;
        for (int i = 0; i < ownedPetNames.size(); i++) {
            String petName = ownedPetNames.get(i);
            int petCount = playerData.getPetInventory().get(petName);

            try {
                PetType petType = PetType.valueOf(petName);
                String displayName = (i + 1) + ". " + petType.getDisplayName() + " (x" + petCount + ")";

                if (i == selectedPetIndex) g.setColor(Color.YELLOW);
                else g.setColor(Color.WHITE);

                if (petName.equals(playerData.getEquippedPet())) {
                    displayName += " [장착됨]";
                    g.setColor(i == selectedPetIndex ? new Color(150, 255, 150) : Color.GREEN);
                }
                g.setFont(new Font("Dialog", Font.BOLD, 20));
                g.drawString(displayName, 150, startY + i * itemHeight);

            } catch (IllegalArgumentException e) {
                g.setColor(Color.RED);
                g.drawString((i + 1) + ". " + petName + " (Unknown)", 150, startY + i * itemHeight);
            }
        }
    }

    private void renderSelectedPetDetails(Graphics2D g) {
        if (ownedPetNames.isEmpty()) return;

        String selectedPetName = ownedPetNames.get(selectedPetIndex);
        if (selectedPetName == null) return;

        // 펫 이미지
        Sprite sprite = petSprites.get(selectedPetName);
        if (sprite == null) return;

        int boxX = 550, boxY = 100, boxWidth = 150, boxHeight = 150;
        g.setColor(Color.DARK_GRAY);
        g.drawRect(boxX - 1, boxY - 1, boxWidth + 2, boxHeight + 2);
        sprite.draw(g, boxX, boxY, boxWidth, boxHeight);

        // 펫 정보 (이름, 레벨, 능력치)
        try {
            PetType petType = PetType.valueOf(selectedPetName);
            int currentLevel = playerData.getPetLevel(petType.name());
            int duplicates = playerData.getPetInventory().get(selectedPetName) - 1;

            g.setFont(new Font("Dialog", Font.BOLD, 18));
            g.setColor(Color.WHITE);
            g.drawString(petType.getDisplayName(), boxX, boxY + boxHeight + 25);

            g.setFont(new Font("Dialog", Font.PLAIN, 16));
            g.setColor(Color.LIGHT_GRAY);

            String description = "";
            switch (petType) {
                case ATTACK:
                    int projectileCount = 1 + (currentLevel / 3) + (currentLevel >= 10 ? 1 : 0);
                    description = "레벨: " + currentLevel + " (총알 " + projectileCount + "개)";
                    break;
                case DEFENSE:
                    double cooldown = 5.0 - (currentLevel * 0.2);
                    description = String.format("레벨: %d (재사용: %.1f초)", currentLevel, cooldown);
                    break;
                case HEAL:
                    double healPercent = 30 + (currentLevel * 2);
                    description = String.format("레벨: %d (힐량: %.0f%%)", currentLevel, healPercent);
                    break;
                case BUFF:
                    double buffPercent = 20 + (currentLevel * 1);
                    description = String.format("레벨: %d (공/공속: +%.0f%%)", currentLevel, buffPercent);
                    break;
            }
            g.drawString(description, boxX, boxY + boxHeight + 50);
            g.drawString("강화 재료: " + duplicates + "개 보유", boxX, boxY + boxHeight + 75);

            // 강화 버튼
            renderUpgradeButton(g, boxX, boxY + boxHeight + 100, currentLevel < 10);

        } catch (IllegalArgumentException e) { /* 에러 처리 생략 */ }
    }

    private void renderUpgradeButton(Graphics2D g, int x, int y, boolean enabled) {
        int buttonWidth = 150, buttonHeight = 50;
        g.setColor(enabled ? Color.YELLOW : Color.GRAY);
        g.drawRect(x, y, buttonWidth, buttonHeight);

        g.setFont(new Font("Dialog", Font.BOLD, 16));
        String text = enabled ? "강화 (U)" : "최고 레벨";
        int textWidth = g.getFontMetrics().stringWidth(text);
        g.drawString(text, x + (buttonWidth - textWidth) / 2, y + (enabled ? 20 : 30));

        if (enabled) {
            g.setFont(new Font("Dialog", Font.PLAIN, 14));
            String costText = "비용: 펫 1개";
            textWidth = g.getFontMetrics().stringWidth(costText);
            g.drawString(costText, x + (buttonWidth - textWidth) / 2, y + 40);
        }
    }

    private void renderFooter(Graphics2D g) {
        if (gameManager.message != null && !gameManager.message.isEmpty()) {
            g.setColor(Color.yellow);
            g.setFont(new Font("Dialog", Font.BOLD, 16));
            g.drawString(gameManager.message, (Game.SCREEN_WIDTH - g.getFontMetrics().stringWidth(gameManager.message)) / 2, Game.SCREEN_HEIGHT - 80);
        }

        g.setFont(new Font("Dialog", Font.PLAIN, 16));
        g.setColor(Color.GRAY);
        g.drawString("Up/Down: 선택 | Enter: 장착/해제 | U: 강화 | ESC: 돌아가기", 20, Game.SCREEN_HEIGHT - 20);
    }

    /**
     * 상태 진입 시 플레이어 데이터와 펫 목록을 새로고침하고 메시지를 초기화
     */
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
