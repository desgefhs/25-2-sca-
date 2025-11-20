package org.newdawn.spaceinvaders.userinput.command;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.data.PlayerData;
import org.newdawn.spaceinvaders.view.PetMenuView;

import java.util.function.Supplier;

public class ToggleEquipPetCommand implements Command {

    private final GameContext gameContext;
    private final Supplier<PetMenuView> petMenuSupplier;

    public ToggleEquipPetCommand(GameContext gameContext, Supplier<PetMenuView> petMenuSupplier) {
        this.gameContext = gameContext;
        this.petMenuSupplier = petMenuSupplier;
    }

    @Override
    public void execute() {
        PetMenuView menuView = petMenuSupplier.get();
        if (menuView == null) return;

        String selectedPetName = menuView.getSelectedItem();
        if (selectedPetName == null) return;

        gameContext.getGameContainer().getSoundManager().playSound("buttonselect");
        PlayerData playerData = gameContext.getGameContainer().getPlayerManager().getCurrentPlayer();

        if (selectedPetName.equals(playerData.getEquippedPet())) {
            playerData.setEquippedPet(null); // Unequip
        } else {
            playerData.setEquippedPet(selectedPetName); // Equip
        }
        gameContext.getGameContainer().getPlayerManager().savePlayerData();
    }
}
