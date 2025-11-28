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

    public boolean hasCollectedAllItems() {
        return collectedItems >= 2;
    }

    public void resetItemCollection() {
        collectedItems = 0;
    }

    public boolean canPlayerAttack() {
        return System.currentTimeMillis() > playerAttackDisabledUntil;
    }
}
