package org.newdawn.spaceinvaders.wave;

/**
 * 웨이브 내의 단일 스폰 이벤트를 나타내는 데이터 클래스.
 * 무엇을, 언제 스폰할지 정의하는 데 사용됩니다.
 */
public class SpawnInfo {

    /**
     * 스폰할 엔티티 또는 그룹의 타입.
     * "FORMATION", "BOSS" 등과 같은 타입을 가집니다.
     */
    private final String type;

    /**
     * 포메이션 스폰의 경우, 무작위 포메이션을 선택하는 데 사용될 스테이지를 나타냅니다.
     * 다른 스폰 타입의 경우 0일 수 있습니다.
     */
    private final int stage;

    /**
     * 스폰된 적을 강제로 업그레이드해야 하는지 여부를 나타내는 플래그.
     */
    private final boolean forceUpgrade;

    /**
     * 이전 이벤트로부터 이 스폰 이벤트가 발생하기까지의 딜레이 (밀리초).
     */
    private final long delay;

    /**
     * SpawnInfo 생성자.
     *
     * @param type 스폰 타입 (예: "FORMATION", "BOSS")
     * @param stage 스테이지 번호
     * @param forceUpgrade 강제 업그레이드 여부
     * @param delay 이전 이벤트로부터의 딜레이
     */
    public SpawnInfo(String type, int stage, boolean forceUpgrade, long delay) {
        this.type = type;
        this.stage = stage;
        this.forceUpgrade = forceUpgrade;
        this.delay = delay;
    }

    public String getType() {
        return type;
    }

    public int getStage() {
        return stage;
    }

    public boolean isForceUpgrade() {
        return forceUpgrade;
    }

    public long getDelay() {
        return delay;
    }
}
