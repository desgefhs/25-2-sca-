package org.newdawn.spaceinvaders.userinput.command;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.data.PlayerData;
import org.newdawn.spaceinvaders.view.WeaponMenu;

import java.util.function.Supplier;

/**
 * 무기 메뉴에서 선택한 무기를 장착하는 커맨드.
 * 플레이어가 해당 무기를 소유하고 있는지 확인한 후 장착을 진행합니다.
 */
public class EquipWeaponCommand implements Command {

    /** 게임의 전반적인 컨텍스트. */
    private final GameContext gameContext;
    /** 현재 WeaponMenu 인스턴스를 제공하는 공급자(Supplier). */
    private final Supplier<WeaponMenu> weaponMenuSupplier;

    /**
     * EquipWeaponCommand 생성자.
     * @param gameContext 게임 컨텍스트
     * @param weaponMenuSupplier WeaponMenu 인스턴스를 제공하는 공급자
     */
    public EquipWeaponCommand(GameContext gameContext, Supplier<WeaponMenu> weaponMenuSupplier) {
        this.gameContext = gameContext;
        this.weaponMenuSupplier = weaponMenuSupplier;
    }

    /**
     * 무기 메뉴에서 선택된 아이템을 가져와 장착합니다.
     * 'DefaultGun'은 항상 장착 가능하며, 다른 무기는 플레이어가 소유(레벨 1 이상)하고 있어야 합니다.
     * 장착 성공 또는 실패에 대한 메시지를 사용자에게 표시합니다.
     */
    @Override
    public void execute() {
        WeaponMenu weaponMenu = weaponMenuSupplier.get();
        if (weaponMenu == null) {
            return;
        }
        
        String selectedWeapon = weaponMenu.getSelectedItem();
        if (selectedWeapon == null) {
            return;
        }

        gameContext.getGameContainer().getSoundManager().playSound("buttonselect");
        PlayerData playerData = gameContext.getGameContainer().getPlayerManager().getCurrentPlayer();

        // "DefaultGun"은 항상 사용 가능
        if (selectedWeapon.equals("DefaultGun")) {
            playerData.setEquippedWeapon(selectedWeapon);
            gameContext.getGameContainer().getPlayerManager().savePlayerData();
            gameContext.setMessage(selectedWeapon + " 장착됨");
            return;
        }

        int level = playerData.getWeaponLevels().getOrDefault(selectedWeapon, 0);
        if (level > 0) {
            playerData.setEquippedWeapon(selectedWeapon);
            gameContext.getGameContainer().getPlayerManager().savePlayerData();
            gameContext.setMessage(selectedWeapon + " 장착됨");
        } else {
            gameContext.setMessage("상점에서 먼저 무기를 잠금 해제해야 합니다.");
        }
    }
}