package org.newdawn.spaceinvaders.core.events;

/**
 * 메테오(운석)가 파괴되었을 때 발생하는 이벤트를 나타내는 클래스.
 * 파괴 시 플레이어에게 부여될 점수 값을 포함합니다.
 */
public class MeteorDestroyedEvent implements Event {
    /** 획득할 점수. */
    private final int scoreValue;

    /**
     * MeteorDestroyedEvent 생성자.
     * @param scoreValue 플레이어에게 부여할 점수
     */
    public MeteorDestroyedEvent(int scoreValue) {
        this.scoreValue = scoreValue;
    }

    /**
     * 획득할 점수 값을 반환합니다.
     * @return 점수 값
     */
    public int getScoreValue() {
        return scoreValue;
    }
}
