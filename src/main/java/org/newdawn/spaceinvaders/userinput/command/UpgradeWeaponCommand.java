package org.newdawn.spaceinvaders.userinput.command;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.data.PlayerData;
import org.newdawn.spaceinvaders.view.WeaponMenu;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 무기 메뉴에서 선택한 무기를 강화(업그레이드)하는 커맨드.
 */
public class UpgradeWeaponCommand implements Command {

    /** 게임의 전반적인 컨텍스트. */
    private final GameContext gameContext;
    /** 현재 WeaponMenu 인스턴스를 제공하는 공급자(Supplier). */
    private final Supplier<WeaponMenu> weaponMenuSupplier;

    /**
     * UpgradeWeaponCommand 생성자.
     * @param gameContext 게임 컨텍스트
     * @param weaponMenuSupplier WeaponMenu 인스턴스를 제공하는 공급자
     */
    public UpgradeWeaponCommand(GameContext gameContext, Supplier<WeaponMenu> weaponMenuSupplier) {
        this.gameContext = gameContext;
        this.weaponMenuSupplier = weaponMenuSupplier;
    }

    /**
     * 무기 메뉴에서 선택된 무기를 식별하고 해당 업그레이드 로직을 호출합니다.
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

        if ("Shotgun".equals(selectedWeapon)) {
            upgradeSpecificWeapon("Shotgun", 5, this::getShotgunUpgradeCost);
        } else if ("Laser".equals(selectedWeapon)) {
            upgradeSpecificWeapon("Laser", 5, this::getLaserUpgradeCost);
        }
    }

    /**
     * 특정 무기에 대한 업그레이드를 처리하는 범용 메소드.
     * 플레이어의 크레딧과 무기의 현재 레벨을 확인한 후 업그레이드를 진행합니다.
     *
     * @param weaponName 업그레이드할 무기 이름
     * @param maxLevel 해당 무기의 최대 레벨
     * @param costFunction 레벨에 따른 비용을 계산하는 함수
     */
    private void upgradeSpecificWeapon(String weaponName, int maxLevel, Function<Integer, Integer> costFunction) {
        PlayerData playerData = gameContext.getGameContainer().getPlayerManager().getCurrentPlayer();
        int currentLevel = playerData.getWeaponLevels().getOrDefault(weaponName, 0);

        if (currentLevel <= 0) {
            gameContext.setMessage("상점에서 먼저 무기를 잠금 해제해야 합니다.");
            return;
        }
        
        if (currentLevel >= maxLevel) {
            gameContext.setMessage(weaponName + "은(는) 이미 최고 레벨입니다.");
            return;
        }

        int cost = costFunction.apply(currentLevel + 1);
        if (playerData.getCredit() >= cost) {
            playerData.setCredit(playerData.getCredit() - cost);
            playerData.getWeaponLevels().put(weaponName, currentLevel + 1);
            gameContext.getGameContainer().getPlayerManager().savePlayerData();
            gameContext.setMessage(weaponName + "이(가) " + (currentLevel + 1) + " 레벨로 업그레이드되었습니다!");
        } else {
            gameContext.setMessage("크레딧이 부족합니다!");
        }
    }

    /**
     * 샷건의 다음 레벨 업그레이드 비용을 반환합니다.
     * @param level 업그레이드할 목표 레벨
     * @return 비용
     */
    private int getShotgunUpgradeCost(int level) {
        switch (level) {
            case 2: return 1000;
            case 3: return 2000;
            case 4: return 4000;
            case 5: return 8000;
            default: return 999999; // Should not happen
        }
    }

    /**
     * 레이저의 다음 레벨 업그레이드 비용을 반환합니다.
     * @param level 업그레이드할 목표 레벨
     * @return 비용
     */
    private int getLaserUpgradeCost(int level) {
        switch (level) {
            case 2: return 1000;
            case 3: return 2000;
            case 4: return 4000;
            case 5: return 8000;
            default: return 999999; // Should not happen
        }
    }
}