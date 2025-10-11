package org.newdawn.spaceinvaders.shop;

import java.util.List;

/**
 * 상점에서 구매할 수 있는 단일 업그레이드 유형을 나타내는 데이터 클래스
 * 업그레이드의 속성(ID, 이름, 설명, 레벨별 비용 및 효과 등)을 저장
 */
public class Upgrade {
    /** 업그레이드의 고유 식별자 */
    private final String id;
    /** 업그레이드의 이름 */
    private final String name;
    /** 업그레이드에 대한 설명 */
    private final String description;
    /** 업그레이드의 최대 레벨 */
    private final int maxLevel;
    /** 각 레벨별 업그레이드 비용 목록 */
    private final List<Integer> costs;
    /** 각 레벨별 업그레이드 효과 값 목록 */
    private final List<Double> effects;

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
     * 특정 레벨로 업그레이드하는 데 필요한 비용을 가져옴
     *
     * @param level 비용을 조회할 대상 레벨 (예: 1레벨 -> 2레벨 업그레이드 비용을 알고 싶으면 level=2)
     * @return 해당 레벨의 비용. 유효하지 않은 레벨이면 Integer.MAX_VALUE 반환.
     */
    public int getCost(int level) {
        if (level > 0 && level <= costs.size()) {
            return costs.get(level - 1);
        }
        return Integer.MAX_VALUE;
    }

    public double getEffect(int level) {
        if (level > 0 && level <= effects.size()) {
            return effects.get(level - 1);
        }
        return effects.isEmpty() ? 1.0 : effects.get(0);
    }
}
