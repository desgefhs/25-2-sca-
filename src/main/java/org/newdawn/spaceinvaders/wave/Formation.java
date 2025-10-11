package org.newdawn.spaceinvaders.wave;

import org.newdawn.spaceinvaders.entity.SpawnInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 적의 배치(formation)를 정의하는 클래스
 * 하나의 포메이션은 이름과, 어떤 적을 어디에 생성할지에 대한 정보(SpawnInfo) 목록을 가짐
 */
public class Formation {
    /** 포메이션의 이름 */
    private final String name;
    /** 이 포메이션에 포함된 모든 엔티티의 생성 정보 목록 */
    private final List<SpawnInfo> spawnList = new ArrayList<>();

    /**
     * 이름을 가진 새로운 포메이션을 생성
     *
     * @param name 포메이션의 이름
     */
    public Formation(String name) {
        this.name = name;
    }

    /**
     * 포메이션의 이름을 반환
     *
     * @return 포메이션 이름
     */
    public String getName() {
        return name;
    }

    /**
     * 포메이션에 새로운 생성 정보를 추가
     *
     * @param spawnInfo 추가할 생성 정보
     */
    public void add(SpawnInfo spawnInfo) {
        spawnList.add(spawnInfo);
    }

    /**
     * 이 포메이션의 모든 생성 정보 목록을 반환
     *
     * @return 생성 정보 목록
     */
    public List<SpawnInfo> getSpawnList() {
        return spawnList;
    }
}
