package org.newdawn.spaceinvaders.shop;

import org.newdawn.spaceinvaders.data.PlayerData;
import org.newdawn.spaceinvaders.entity.Pet.PetType;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 상점에서 판매하는 모든 업그레이드와 아이템 뽑기(가챠) 로직을 관리하는 클래스
 */
public class ShopManager {

    /** ID를 키로 하여 모든 업그레이드 정보를 저장하는 맵 */
    private final Map<String, Upgrade> upgrades;
    /** 아이템 뽑기 비용 */
    private static final int ITEM_DRAW_COST = 500;

    /**
     * ShopManager를 생성하고, 게임 내 모든 영구 업그레이드를 초기화
     */
    public ShopManager() {
        List<Upgrade> upgradeList = new ArrayList<>();

        // 공격력 증가 업그레이드
        upgradeList.add(new Upgrade(
                "DAMAGE",
                "공격력 증가",
                "Increases bullet damage.",
                30, // Max Level
                Arrays.asList(100, 300, 600, 900, 1200, 1800, 2400, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000, 10000, 10000, 10000, 10000, 10000, 10000, 10000, 10000, 10000, 10000, 10000, 10000, 10000, 10000, 10000), // Costs per level
                Arrays.asList(2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 20.0, 21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0, 31.0)      // Damage value per level
        ));

        // 최대 체력 증가 업그레이드
        upgradeList.add(new Upgrade(
                "HEALTH",
                "최대 체력 증가",
                "Increases ship's maximum health.",
                5,
                Arrays.asList(200, 400, 800, 1500, 3000),
                Arrays.asList(4.0, 5.0, 6.0, 7.0, 8.0)
        ));

        // 공격 속도 증가 업그레이드
        upgradeList.add(new Upgrade(
                "ATK_SPEED",
                "공격 속도 증가",
                "Reduces time between shots.",
                5,
                Arrays.asList(300, 600, 1200, 2500, 5000),
                Arrays.asList(400.0, 300.0, 200.0, 150.0, 100.0) // ms
        ));

        // 다중 발사 업그레이드
        upgradeList.add(new Upgrade(
                "PROJECTILE",
                "다중 발사",
                "Fire additional projectiles.",
                3,
                Arrays.asList(1000, 5000, 10000),
                Arrays.asList(2.0, 3.0, 4.0) //
        ));

        // 리스트를 맵으로 변환하여 ID로 쉽게 조회할 수 있도록 함
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

    /**
     * 아이템 뽑기를 실행하고 결과 문자열을 반환
     *
     * @param playerData 현재 플레이어 데이터
     * @return 뽑기 결과 (예: "INSUFFICIENT_FUNDS", "PET_ATTACK", "WEAPON_SHOTGUN")
     */
    public String drawItem(PlayerData playerData) {
        if (playerData.getCredit() < ITEM_DRAW_COST) {
            return "INSUFFICIENT_FUNDS";
        }
        playerData.setCredit(playerData.getCredit() - ITEM_DRAW_COST);

        Random rand = new Random();
        int roll = rand.nextInt(100); // 0-99 사이의 난수 생성

        // 40% 확률: 250 크레딧
        if (roll < 40) {
            playerData.setCredit(playerData.getCredit() + 250);
            return "CREDIT_250";
        }
        // 10% 확률: 공격 펫
        else if (roll < 50) {
            playerData.getPetInventory().put(PetType.ATTACK.name(), playerData.getPetInventory().getOrDefault(PetType.ATTACK.name(), 0) + 1);
            return "PET_ATTACK";
        }
        // 10% 확률: 방어 펫
        else if (roll < 60) {
            playerData.getPetInventory().put(PetType.DEFENSE.name(), playerData.getPetInventory().getOrDefault(PetType.DEFENSE.name(), 0) + 1);
            return "PET_DEFENSE";
        }
        // 10% 확률: 치유 펫
        else if (roll < 70) {
            playerData.getPetInventory().put(PetType.HEAL.name(), playerData.getPetInventory().getOrDefault(PetType.HEAL.name(), 0) + 1);
            return "PET_HEAL";
        }
        // 10% 확률: 버프 펫
        else if (roll < 80) {
            playerData.getPetInventory().put(PetType.BUFF.name(), playerData.getPetInventory().getOrDefault(PetType.BUFF.name(), 0) + 1);
            return "PET_BUFF";
        }
        // 10% 확률: 샷건
        else if (roll < 90) {
            if (playerData.getWeaponLevels().getOrDefault("Shotgun", 0) > 0) {
                playerData.setCredit(playerData.getCredit() + 300); // 중복 보상
                return "DUPLICATE_WEAPON";
            } else {
                playerData.getWeaponLevels().put("Shotgun", 1);
                return "WEAPON_SHOTGUN";
            }
        }
        // 10% 확률: 레이저
        else {
            if (playerData.getWeaponLevels().getOrDefault("Laser", 0) > 0) {
                playerData.setCredit(playerData.getCredit() + 300); // 중복 보상
                return "DUPLICATE_WEAPON";
            } else {
                playerData.getWeaponLevels().put("Laser", 1);
                return "WEAPON_LASER";
            }
        }
    }
}