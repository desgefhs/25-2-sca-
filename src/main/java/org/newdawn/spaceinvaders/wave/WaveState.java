package org.newdawn.spaceinvaders.wave;

/**
 * 웨이브 진행의 각 상태를 정의하는 상태 패턴(State Pattern)의 인터페이스.
 * 이 인터페이스를 구현하는 클래스는 웨이브의 특정 상태(예: 전투 중, 웨이브 종료)에 대한 로직을 캡슐화합니다.
 */
public interface WaveState {

    /**
     * 이 상태에 진입할 때 호출됩니다.
     * @param waveManager 컨텍스트 역할을 하는 WaveManager
     */
    void onEnter(WaveManager waveManager);

    /**
     * 매 프레임마다 호출되어 이 상태의 로직을 업데이트합니다.
     * @param waveManager 컨텍스트 역할을 하는 WaveManager
     * @param delta 마지막 프레임 이후 경과된 시간
     */
    void update(WaveManager waveManager, long delta);

    /**
     * 이 상태를 벗어날 때 호출됩니다.
     * @param waveManager 컨텍스트 역할을 하는 WaveManager
     */
    void onExit(WaveManager waveManager);
}
