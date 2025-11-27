package org.newdawn.spaceinvaders.core;

/**
 * 단일 게임 플레이 세션 동안의 상태를 추적하는 클래스.
 * 수집한 아이템 수, 플레이어 상태 효과(예: 스턴) 등 일시적인 데이터를 관리합니다.
 */
public class GameSession {

    /** 현재 세션에서 수집한 아이템의 수. */
    private int collectedItems = 0;
    /** 플레이어의 공격이 비활성화되는 시간 (타임스탬프). */
    private long playerAttackDisabledUntil = 0;

    /**
     * 아이템이 수집되었음을 알리고 카운터를 증가시킵니다.
     */
    public void notifyItemCollected() {
        collectedItems++;
    }

    /**
     * 특정 조건(예: 웨이브 클리어)을 만족시키기 위해 모든 아이템을 수집했는지 확인합니다.
     * @return 모든 아이템을 수집했으면 true, 그렇지 않으면 false.
     */
    public boolean hasCollectedAllItems() {
        return collectedItems >= 2; // 특정 게임 모드 또는 웨이브의 요구 조건
    }

    /**
     * 아이템 수집 카운터를 리셋합니다.
     */
    public void resetItemCollection() {
        collectedItems = 0;
    }

    /**
     * 지정된 시간 동안 플레이어를 스턴 상태로 만들어 공격을 비활성화합니다.
     * @param duration 스턴 지속 시간 (밀리초)
     */
    public void stunPlayer(long duration) {
        this.playerAttackDisabledUntil = System.currentTimeMillis() + duration;
    }

    /**
     * 플레이어가 현재 공격할 수 있는 상태인지 확인합니다.
     * @return 공격 가능하면 true, 스턴 상태이면 false.
     */
    public boolean canPlayerAttack() {
        return System.currentTimeMillis() > playerAttackDisabledUntil;
    }
}
