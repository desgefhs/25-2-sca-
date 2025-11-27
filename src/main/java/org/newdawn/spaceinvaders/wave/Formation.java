package org.newdawn.spaceinvaders.wave;

import org.newdawn.spaceinvaders.entity.SpawnInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 적의 공격 편대(Formation)를 정의하는 데이터 클래스.
 * 하나의 포메이션은 이름과 여러 개의 {@link SpawnInfo} 이벤트 목록으로 구성됩니다.
 */
public class Formation {
    /** 포메이션의 이름 (예: "Wavy Line", "V-Shape"). */
    private final String name;
    /** 이 포메이션에 포함된 모든 스폰 이벤트 목록. */
    private final List<SpawnInfo> spawnList = new ArrayList<>();

    /**
     * Formation 생성자.
     * @param name 포메이션의 이름
     */
    public Formation(String name) {
        this.name = name;
    }

    /**
     * 포메이션의 이름을 반환합니다.
     * @return 포메이션 이름
     */
    public String getName() {
        return name;
    }

    /**
     * 포메이션에 스폰 이벤트를 추가합니다.
     * @param spawnInfo 추가할 스폰 정보
     */
    public void add(SpawnInfo spawnInfo) {
        spawnList.add(spawnInfo);
    }

    /**
     * 이 포메이션의 모든 스폰 이벤트 목록을 반환합니다.
     * @return {@link SpawnInfo} 객체의 리스트
     */
    public List<SpawnInfo> getSpawnList() {
        return spawnList;
    }
}
