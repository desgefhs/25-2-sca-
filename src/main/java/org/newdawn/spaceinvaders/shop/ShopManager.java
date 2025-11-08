package org.newdawn.spaceinvaders.shop;

import org.newdawn.spaceinvaders.data.PlayerData;
import org.newdawn.spaceinvaders.entity.Pet.PetType;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


public class ShopManager {

    private final Map<String, Upgrade> upgrades;
    private static final int ITEM_DRAW_COST = 500;

    public ShopManager() {
        List<Upgrade> upgradeList = new ArrayList<>();

        // 총알 데미지 강화
        upgradeList.add(new Upgrade(
                "DAMAGE",
                "공격력 증가",
                "Increases bullet damage.",
                30, // Max Level
                Arrays.asList(100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 1100, 1200, 1300, 1400, 1500, 1600, 1700, 1800, 1900, 2000, 2100, 2200, 2300, 2400, 2500, 2600, 2700, 2800, 2900, 3000), // Costs per level
                Arrays.asList(2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 20.0, 21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0, 31.0)      // Damage value per level
        ));

        // 최대 체력 강화
        upgradeList.add(new Upgrade(
                "HEALTH",
                "최대 체력 증가",
                "Increases ship's maximum health.",
                5,
                Arrays.asList(200, 400, 800, 1500, 3000),
                Arrays.asList(4.0, 5.0, 6.0, 7.0, 8.0)
        ));

        // 공격 속도 증가
        upgradeList.add(new Upgrade(
                "ATK_SPEED",
                "공격 속도 증가",
                "Reduces time between shots.",
                5,
                Arrays.asList(300, 600, 1200, 2500, 5000),
                Arrays.asList(400.0, 300.0, 200.0, 150.0, 100.0) // ms
        ));

        // 총알 개수 추가
        upgradeList.add(new Upgrade(
                "PROJECTILE",
                "다중 발사",
                "Fire additional projectiles.",
                3,
                Arrays.asList(1000, 3000, 7000),
                Arrays.asList(2.0, 3.0, 4.0) //
        ));

        this.upgrades = upgradeList.stream()
                .collect(Collectors.toMap(Upgrade::getId, Function.identity()));
    }

    public Upgrade getUpgrade(String id) {
        return upgrades.get(id);
    }

    public List<Upgrade> getAllUpgrades() {
        return new ArrayList<>(upgrades.values());
    }

    public int getItemDrawCost() {
        return ITEM_DRAW_COST;
    }

    public DrawResult drawItem(PlayerData playerData) {
        if (playerData.getCredit() < ITEM_DRAW_COST) {
            return new DrawResult("크레딧이 부족합니다!", false);
        }
        playerData.setCredit(playerData.getCredit() - ITEM_DRAW_COST);

        Random rand = new Random();
        int roll = rand.nextInt(100);

        // 40% chance for 250 credits
        if (roll < 40) {
            playerData.setCredit(playerData.getCredit() + 250);
            return new DrawResult("250 크레딧에 당첨되었습니다!", true);
        }
        // 10% chance for Attack Pet
        else if (roll < 50) {
            playerData.getPetInventory().put(PetType.ATTACK.name(), playerData.getPetInventory().getOrDefault(PetType.ATTACK.name(), 0) + 1);
            return new DrawResult("'공격형 펫'을 획득했습니다!", true);
        }
        // 10% chance for Defense Pet
        else if (roll < 60) {
            playerData.getPetInventory().put(PetType.DEFENSE.name(), playerData.getPetInventory().getOrDefault(PetType.DEFENSE.name(), 0) + 1);
            return new DrawResult("'방어형 펫'을 획득했습니다!", true);
        }
        // 10% chance for Heal Pet
        else if (roll < 70) {
            playerData.getPetInventory().put(PetType.HEAL.name(), playerData.getPetInventory().getOrDefault(PetType.HEAL.name(), 0) + 1);
            return new DrawResult("'치유형 펫'을 획득했습니다!", true);
        }
        // 10% chance for Buff Pet
        else if (roll < 80) {
            playerData.getPetInventory().put(PetType.BUFF.name(), playerData.getPetInventory().getOrDefault(PetType.BUFF.name(), 0) + 1);
            return new DrawResult("'버프형 펫'을 획득했습니다!", true);
        }
        // 10% chance for Shotgun
        else if (roll < 90) {
            if (playerData.getWeaponLevels().getOrDefault("Shotgun", 0) > 0) {
                playerData.setCredit(playerData.getCredit() + 300);
                return new DrawResult("이미 보유한 무기입니다! 300 크레딧을 돌려받습니다.", true);
            } else {
                playerData.getWeaponLevels().put("Shotgun", 1);
                return new DrawResult("새로운 무기 '샷건'을 잠금 해제했습니다!", true);
            }
        }
        // 10% chance for Laser
        else {
            if (playerData.getWeaponLevels().getOrDefault("Laser", 0) > 0) {
                playerData.setCredit(playerData.getCredit() + 300);
                return new DrawResult("이미 보유한 무기입니다! 300 크레딧을 돌려받습니다.", true);
            } else {
                playerData.getWeaponLevels().put("Laser", 1);
                return new DrawResult("새로운 무기 '레이저'를 잠금 해제했습니다!", true);
            }
        }
    }
}
