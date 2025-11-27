package org.newdawn.spaceinvaders.userinput.command;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.data.PlayerData;
import org.newdawn.spaceinvaders.entity.Pet.PetType;
import org.newdawn.spaceinvaders.view.PetMenuView;

import java.util.function.Supplier;

public class UpgradePetCommand implements Command {

    private final GameContext gameContext;
    private final Supplier<PetMenuView> petMenuSupplier;

    public UpgradePetCommand(GameContext gameContext, Supplier<PetMenuView> petMenuSupplier) {
        this.gameContext = gameContext;
        this.petMenuSupplier = petMenuSupplier;
    }

    @Override
    public void execute() {
        PetMenuView menuView = petMenuSupplier.get();
        if (menuView == null) return;

        String selectedPetName = menuView.getSelectedItem();
        if (selectedPetName == null) return;

        PlayerData playerData = gameContext.getGameContainer().getPlayerManager().getCurrentPlayer();
        int currentAmount = playerData.getPetInventory().getOrDefault(selectedPetName, 0);

        if (currentAmount <= 1) {
            gameContext.setMessage("강화에 필요한 중복 펫이 부족합니다.");
            return;
        }

        try {
            PetType petType = PetType.valueOf(selectedPetName);
            int currentLevel = playerData.getPetLevel(petType.name());

            if (currentLevel >= 10) {
                gameContext.setMessage("이미 최고 레벨입니다.");
                return;
            }

            // Proceed with upgrade
            playerData.increasePetLevel(petType.name());
            playerData.getPetInventory().put(selectedPetName, currentAmount - 1);
            gameContext.getGameContainer().getPlayerManager().savePlayerData();
            gameContext.setMessage(petType.getDisplayName() + " 강화 성공!");

        } catch (IllegalArgumentException e) {
            gameContext.setMessage("알 수 없는 펫입니다.");
        }
    }
}
