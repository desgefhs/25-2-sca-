package org.newdawn.spaceinvaders.userinput.command;

import org.newdawn.spaceinvaders.core.GameContext;
import org.newdawn.spaceinvaders.entity.ShipEntity;
import org.newdawn.spaceinvaders.entity.weapon.Weapon;

/**
 * 플레이어 함선의 현재 무기를 전환하는 커맨드.
 * 무기 전환 전 플레이어가 해당 무기를 소유하고 있는지 확인합니다.
 */
public class SwitchWeaponCommand implements Command {

    /** 게임의 전반적인 컨텍스트. */
    private final GameContext gameContext;
    /** 전환할 무기의 이름. */
    private final String weaponName;

    /**
     * SwitchWeaponCommand 생성자.
     * @param gameContext 게임 컨텍스트
     * @param weaponName 전환할 무기의 이름
     */
    public SwitchWeaponCommand(GameContext gameContext, String weaponName) {
        this.gameContext = gameContext;
        this.weaponName = weaponName;
    }

    /**
     * 지정된 무기로 전환합니다.
     * "DefaultGun"은 항상 사용 가능하며, 다른 무기는 플레이어가 소유(레벨 1 이상)하고 있어야 합니다.
     * 무기 객체를 가져와 플레이어 스탯에 맞는 레벨을 설정한 후 함선에 장착합니다.
     */
    @Override
    public void execute() {
        // 기본 총은 항상 사용 가능. 다른 무기는 플레이어가 업그레이드했는지 확인.
        if (!"DefaultGun".equals(weaponName) && gameContext.getGameContainer().getPlayerManager().getPlayerStats().getWeaponLevel(weaponName) <= 0) {
            return; // 플레이어가 아직 이 무기를 소유하지 않음.
        }

        Weapon weapon = gameContext.getWeapons().get(weaponName);
        if (weapon == null) {
            return; // weaponName이 정확하다면 발생하지 않아야 함.
        }
        
        ShipEntity ship = gameContext.getShip();
        if (ship == null) {
            return;
        }

        // 장착하기 전에 플레이어 스탯에서 무기 레벨 설정
        weapon.setLevel(gameContext.getGameContainer().getPlayerManager().getPlayerStats().getWeaponLevel(weaponName));
        ship.setWeapon(weapon);
    }
}
