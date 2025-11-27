package org.newdawn.spaceinvaders.userinput.command;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.data.PlayerData;
import org.newdawn.spaceinvaders.view.PetMenuView;

import java.util.function.Supplier;

/**
 * 펫 메뉴에서 선택한 펫의 장착 상태를 토글(equip/unequip)하는 커맨드.
 */
public class ToggleEquipPetCommand implements Command {

    /** 게임의 전반적인 컨텍스트. */
    private final GameContext gameContext;
    /** 현재 PetMenuView 인스턴스를 제공하는 공급자(Supplier). */
    private final Supplier<PetMenuView> petMenuSupplier;

    /**
     * ToggleEquipPetCommand 생성자.
     * @param gameContext 게임 컨텍스트
     * @param petMenuSupplier PetMenuView 인스턴스를 제공하는 공급자
     */
    public ToggleEquipPetCommand(GameContext gameContext, Supplier<PetMenuView> petMenuSupplier) {
        this.gameContext = gameContext;
        this.petMenuSupplier = petMenuSupplier;
    }

    /**
     * 선택된 펫의 장착 상태를 변경합니다.
     * 이미 장착된 펫이면 장착을 해제하고, 장착되지 않은 펫이면 장착합니다.
     * 변경된 상태는 플레이어 데이터에 저장됩니다.
     */
    @Override
    public void execute() {
        PetMenuView menuView = petMenuSupplier.get();
        if (menuView == null) return;

        String selectedPetName = menuView.getSelectedItem();
        if (selectedPetName == null) return;

        gameContext.getGameContainer().getSoundManager().playSound("buttonselect");
        PlayerData playerData = gameContext.getGameContainer().getPlayerManager().getCurrentPlayer();

        if (selectedPetName.equals(playerData.getEquippedPet())) {
            playerData.setEquippedPet(null); // 장착 해제
            gameContext.setMessage(selectedPetName + " 장착 해제됨");
        } else {
            playerData.setEquippedPet(selectedPetName); // 장착
            gameContext.setMessage(selectedPetName + " 장착됨");
        }
        gameContext.getGameContainer().getPlayerManager().savePlayerData();
    }
}
