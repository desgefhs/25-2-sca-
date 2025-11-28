package org.newdawn.spaceinvaders.wave;

import java.util.List;

/**
 * 단일 웨이브를 정의하는 모든 정보를 담는 데이터 클래스.
 * 이 객체는 {@link WaveManager}가 웨이브를 실행하는 데 사용되며, '무엇을(what)'과 '어떻게(how)'를 분리하는 역할을 합니다.
 */
public class WaveDefinition {

    /** 웨이브의 번호. */
    private final int waveNumber;
    /** 이 웨이브 동안 재생될 배경 음악의 이름. */
    private final String music;
    /** 이 웨이브에서 발생할 모든 스폰 이벤트 목록. */
    private final List<SpawnInfo> spawns;

    /**
     * WaveDefinition 생성자.
     *
     * @param waveNumber 웨이브 번호
     * @param music 배경 음악 이름
     * @param spawns 스폰 이벤트 목록
     */
    public WaveDefinition(int waveNumber, String music, List<SpawnInfo> spawns) {
        this.waveNumber = waveNumber;
        this.music = music;
        this.spawns = spawns;
    }

    public String getMusic() {
        return music;
    }

    /**
     * 스폰 이벤트 목록을 반환합니다.
     * @return {@link SpawnInfo} 객체의 리스트
     */
    public List<SpawnInfo> getSpawns() {
        return spawns;
    }
}
