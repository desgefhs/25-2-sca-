package org.newdawn.spaceinvaders.shop;

import java.util.List;

/**
 * 상점에서 사용할 수 있는 단일 업그레이드 유형을 나타내는 데이터 클래스.
 * 업그레이드의 ID, 이름, 설명, 최대 레벨, 레벨별 비용 및 효과 값을 포함합니다.
 */
public class Upgrade {
    /** 업그레이드의 고유 식별자. */
    private final String id;
    /** 상점에 표시될 업그레이드의 이름. */
    private final String name;
    /** 업그레이드에 대한 설명. */
    private final String description;
    /** 업그레이드의 최대 레벨. */
    private final int maxLevel;
    /** 각 레벨로 업그레이드하는 데 필요한 비용 목록. */
    private final List<Integer> costs;
    /** 각 레벨에서 적용되는 효과 값 목록. */
    private final List<Double> effects;

    /**
     * Upgrade 생성자.
     *
     * @param id 업그레이드 ID
     * @param name 업그레이드 이름
     * @param description 업그레이드 설명
     * @param maxLevel 최대 레벨
     * @param costs 레벨별 비용 목록
     * @param effects 레벨별 효과 값 목록
     */
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
     * 특정 레벨로 업그레이드하는 데 필요한 비용을 가져옵니다.
     *
     * @param level 비용을 조회할 대상 레벨
     * @return 해당 레벨의 업그레이드 비용. 유효하지 않은 레벨의 경우 {@link Integer#MAX_VALUE} 반환.
     */
    public int getCost(int level) {
        if (level > 0 && level <= costs.size()) {
            return costs.get(level - 1);
        }
        return Integer.MAX_VALUE;
    }

    /**
     * 특정 레벨의 효과 값을 가져옵니다.
     *
     * @param level 효과 값을 조회할 현재 레벨
     * @return 해당 레벨의 효과 값. 유효하지 않은 레벨의 경우 기본값 반환.
     */
    public double getEffect(int level) {
        if (level > 0 && level <= effects.size()) {
            return effects.get(level - 1);
        }
        return effects.isEmpty() ? 1.0 : effects.get(0);
    }
}
