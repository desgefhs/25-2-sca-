package org.newdawn.spaceinvaders.entity;

/**
 * 엔티티의 현재 체력(currentHp)과 최대 체력(MAX_HP) 값을 저장하는 데이터 클래스.
 */
public class HP {
    /** 최대 체력. */
    private double MAX_HP;
    /** 현재 체력. */
    private double currentHp;

    /**
     * HP 생성자.
     * @param MAX_HP 최대 체력
     * @param currentHp 현재 체력
     */
    public HP(double MAX_HP, double currentHp) {
        this.MAX_HP = MAX_HP;
        this.currentHp = currentHp;
    }

    /**
     * 기본 생성자.
     * Firestore Deserialization 등을 위해 존재합니다.
     */
    public HP() {
    }

    public double getMAX_HP() {
        return MAX_HP;
    }

    public void setMAX_HP(double MAX_HP) {
        this.MAX_HP = MAX_HP;
    }

    public double getCurrentHp() {
        return currentHp;
    }

    public void setCurrentHp(double currentHp) {
        this.currentHp = currentHp;
    }
}
