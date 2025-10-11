package org.newdawn.spaceinvaders.entity;

/**
 * 현재 체력과 최대 체력 값을 저장하는 간단한 데이터 클래스
 */
public class HP {
    /** 최대 체력 */
    private double MAX_HP;
    /** 현재 체력 */
    private double currentHp;

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

    public HP(double MAX_HP, double currentHp) {
        this.MAX_HP = MAX_HP;
        this.currentHp = currentHp;
    }

}
