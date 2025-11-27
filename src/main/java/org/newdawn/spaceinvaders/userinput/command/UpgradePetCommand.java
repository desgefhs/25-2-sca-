package org.newdawn.spaceinvaders.userinput.command;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.data.PlayerData;
import org.newdawn.spaceinvaders.entity.Pet.PetType;
import org.newdawn.spaceinvaders.view.PetMenuView;

import java.util.function.Supplier;

/**
 * 펫 메뉴에서 선택한 펫을 강화(업그레이드)하는 커맨드.
 */
public class UpgradePetCommand implements Command {

    /** 게임의 전반적인 컨텍스트. */
    private final GameContext gameContext;
    /** 현재 PetMenuView 인스턴스를 제공하는 공급자(Supplier). */
    private final Supplier<PetMenuView> petMenuSupplier;

    /**
     * UpgradePetCommand 생성자.
     * @param gameContext 게임 컨텍스트
     * @param petMenuSupplier PetMenuView 인스턴스를 제공하는 공급자
     */
    public UpgradePetCommand(GameContext gameContext, Supplier<PetMenuView> petMenuSupplier) {
        this.gameContext = gameContext;
        this.petMenuSupplier = petMenuSupplier;
    }

    /**
     * 선택된 펫의 강화를 시도합니다.
     * 플레이어가 강화를 위한 충분한 수의 중복 펫을 가지고 있는지 확인합니다.
     * 조건 충족 시, 펫을 하나 소모하여 레벨을 올리고 데이터를 저장합니다.
     * 조건 미충족 시 적절한 메시지를 표시합니다.
     */
    @Override
    public void execute() {
        PetMenuView menuView = petMenuSupplier.get();
        if (menuView == null) return;

        String selectedPetName = menuView.getSelectedItem();
        if (selectedPetName == null) return;

        gameContext.getGameContainer().getSoundManager().playSound("buttonselect");
        PlayerData playerData = gameContext.getGameContainer().getPlayerManager().getCurrentPlayer();
        int currentAmount = playerData.getPetInventory().getOrDefault(selectedPetName, 0);

        // 강화에는 자기 자신 외에 추가 펫이 필요하므로 최소 2개 이상 필요
        if (currentAmount <= 1) {
            gameContext.setMessage("강화에 필요한 중복 펫이 부족합니다.");
            return;
        }

        try {
            PetType petType = PetType.valueOf(selectedPetName);
            int currentLevel = playerData.getPetLevel(petType.name());

            if (currentLevel >= 10) { // 최고 레벨 가정
                gameContext.setMessage("이미 최고 레벨입니다.");
                return;
            }

            // 강화 진행
            playerData.increasePetLevel(petType.name());
            playerData.getPetInventory().put(selectedPetName, currentAmount - 1);
            gameContext.getGameContainer().getPlayerManager().savePlayerData();
            gameContext.setMessage(petType.getDisplayName() + " 강화 성공!");

        } catch (IllegalArgumentException e) {
            gameContext.setMessage("알 수 없는 펫입니다.");
        }
    }
}
