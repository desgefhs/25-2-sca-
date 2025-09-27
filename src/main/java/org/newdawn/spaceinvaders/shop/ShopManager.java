package org.newdawn.spaceinvaders.shop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Manages all available upgrades in the game.
 * Initializes and provides access to upgrade objects.
 */
public class ShopManager {

    private final Map<String, Upgrade> upgrades;

    public ShopManager() {
        List<Upgrade> upgradeList = new ArrayList<>();

        // 1. Bullet Damage Upgrade
        upgradeList.add(new Upgrade(
                "DAMAGE",
                "공격력 증가",
                "Increases bullet damage.",
                30, // Max Level
                Arrays.asList(100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 1100, 1200, 1300, 1400, 1500, 1600, 1700, 1800, 1900, 2000, 2100, 2200, 2300, 2400, 2500, 2600, 2700, 2800, 2900, 3000), // Costs per level (level * 100)
                Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 20.0, 21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0)      // Damage value per level
        ));

        // 2. Max Health Upgrade
        upgradeList.add(new Upgrade(
                "HEALTH",
                "최대 체력 증가",
                "Increases ship's maximum health.",
                5,
                Arrays.asList(200, 400, 800, 1500, 3000),
                Arrays.asList(3.0, 4.0, 5.0, 6.0, 7.0)      // Max health value per level
        ));

        // 3. Attack Speed Upgrade
        upgradeList.add(new Upgrade(
                "ATK_SPEED",
                "공격 속도 증가",
                "Reduces time between shots.",
                5,
                Arrays.asList(300, 600, 1200, 2500, 5000),
                Arrays.asList(500.0, 400.0, 300.0, 200.0, 100.0) // Firing interval in ms
        ));

        // 4. Extra Projectile Upgrade
        upgradeList.add(new Upgrade(
                "PROJECTILE",
                "다중 발사",
                "Fire additional projectiles.",
                3,
                Arrays.asList(1000, 3000, 7000),
                Arrays.asList(1.0, 2.0, 3.0) // Number of projectiles
        ));

        // Convert the list to a map for easy lookup by ID
        this.upgrades = upgradeList.stream()
                .collect(Collectors.toMap(Upgrade::getId, Function.identity()));
    }

    public Upgrade getUpgrade(String id) {
        return upgrades.get(id);
    }

    public List<Upgrade> getAllUpgrades() {
        return new ArrayList<>(upgrades.values());
    }
}
