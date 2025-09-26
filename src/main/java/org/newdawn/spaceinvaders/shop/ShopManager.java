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
                "Damage Up",
                "Increases bullet damage.",
                5, // Max Level
                Arrays.asList(100, 250, 500, 1000, 2000), // Costs per level
                Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0)      // Damage value per level
        ));

        // 2. Max Health Upgrade
        upgradeList.add(new Upgrade(
                "HEALTH",
                "Health Up",
                "Increases ship's maximum health.",
                5,
                Arrays.asList(200, 400, 800, 1500, 3000),
                Arrays.asList(3.0, 4.0, 5.0, 6.0, 7.0)      // Max health value per level
        ));

        // 3. Attack Speed Upgrade
        upgradeList.add(new Upgrade(
                "ATK_SPEED",
                "Attack Speed Up",
                "Reduces time between shots.",
                5,
                Arrays.asList(300, 600, 1200, 2500, 5000),
                Arrays.asList(500.0, 400.0, 300.0, 200.0, 100.0) // Firing interval in ms
        ));

        // 4. Extra Projectile Upgrade
        upgradeList.add(new Upgrade(
                "PROJECTILE",
                "Multi-Shot",
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
