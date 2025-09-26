package org.newdawn.spaceinvaders.shop;

import java.util.List;

/**
 * Represents a single type of upgrade available in the shop.
 * This class is a data container for an upgrade's properties.
 */
public class Upgrade {
    private final String id;
    private final String name;
    private final String description;
    private final int maxLevel;
    private final List<Integer> costs; // Cost for each level
    private final List<Double> effects;  // Effect value for each level

    public Upgrade(String id, String name, String description, int maxLevel, List<Integer> costs, List<Double> effects) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.maxLevel = maxLevel;
        this.costs = costs;
        this.effects = effects;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    /**
     * Gets the cost for upgrading to a specific level.
     * @param level The target level (e.g., level 1 costs[0]).
     * @return The cost, or Integer.MAX_VALUE if the level is out of bounds.
     */
    public int getCost(int level) {
        if (level > 0 && level <= costs.size()) {
            return costs.get(level - 1);
        }
        return Integer.MAX_VALUE; // No more upgrades or invalid level
    }

    /**
     * Gets the effect value for a given level.
     * @param level The current level.
     * @return The effect value, or the base value (from index 0) if the level is out of bounds.
     */
    public double getEffect(int level) {
        if (level > 0 && level <= effects.size()) {
            return effects.get(level - 1);
        }
        // Return a default/base effect if level is 0 or out of bounds
        return effects.isEmpty() ? 1.0 : effects.get(0);
    }
}
