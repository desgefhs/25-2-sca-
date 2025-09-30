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
     * 업그레이드 하는데 드는 비용 가져오기
     * @param level 대상 레벨
     * @return 크레딧이 부족하거나, 레벨 초과
     */
    public int getCost(int level) {
        if (level > 0 && level <= costs.size()) {
            return costs.get(level - 1);
        }
        return Integer.MAX_VALUE;
    }

    /**
     * 효과 가져오기
     * @param level 현재 레벨
     * @return 기본값
     */
    public double getEffect(int level) {
        if (level > 0 && level <= effects.size()) {
            return effects.get(level - 1);
        }
        return effects.isEmpty() ? 1.0 : effects.get(0);
    }
}
