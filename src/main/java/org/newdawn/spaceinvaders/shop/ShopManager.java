package org.newdawn.spaceinvaders.shop;

import org.newdawn.spaceinvaders.data.PlayerData;
import org.newdawn.spaceinvaders.entity.PetType;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


public class ShopManager {

    private final Map<String, Upgrade> upgrades;
    private static final int PET_DRAW_COST = 500;

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

    public int getPetDrawCost() {
        return PET_DRAW_COST;
    }

    public PetType drawPet(PlayerData playerData) {
        if (playerData.getCredit() < PET_DRAW_COST) {
            return null; // Not enough credits
        }

        playerData.setCredit(playerData.getCredit() - PET_DRAW_COST);

        PetType[] allPets = PetType.values();
        Random rand = new Random();
        PetType drawnPet = allPets[rand.nextInt(allPets.length)];

        Map<String, Integer> inventory = playerData.getPetInventory();
        inventory.put(drawnPet.name(), inventory.getOrDefault(drawnPet.name(), 0) + 1);

        return drawnPet;
    }
}